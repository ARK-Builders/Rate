package dev.arkbuilders.rate.feature.portfolio.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.AmountStr
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.core.domain.toBigDecimalArk
import dev.arkbuilders.rate.core.domain.usecase.GetGroupByIdOrCreateDefaultUseCase
import dev.arkbuilders.rate.feature.portfolio.domain.model.Asset
import dev.arkbuilders.rate.feature.portfolio.domain.repo.PortfolioRepo
import dev.arkbuilders.rate.feature.portfolio.domain.usecase.AddNewAssetsUseCase
import dev.arkbuilders.rate.feature.portfolio.presentation.model.AddAssetsNavResult
import dev.arkbuilders.rate.feature.portfolio.presentation.model.NavAsset
import dev.arkbuilders.rate.feature.search.presentation.SearchNavResult
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber

data class AddAssetState(
    val currencies: List<AmountStr> = listOf(AmountStr("USD", "")),
    val group: Group = Group.empty(),
    val availableGroups: List<Group> = emptyList(),
)

sealed class AddAssetSideEffect {
    data class NavigateBackWithResult(val result: AddAssetsNavResult) : AddAssetSideEffect()

    data class NavigateSearchAdd(val prohibitedCodes: List<CurrencyCode>) : AddAssetSideEffect()

    data class NavigateSearchSet(val index: Int, val prohibitedCodes: List<CurrencyCode>) :
        AddAssetSideEffect()

    data object NavigateBack : AddAssetSideEffect()
}

enum class SearchNavResultType {
    ADD,
    SET,
}

class AddAssetViewModel(
    private val groupId: Long?,
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val groupRepo: GroupRepo,
    private val getGroupByIdOrCreateDefaultUseCase: GetGroupByIdOrCreateDefaultUseCase,
    private val analyticsManager: AnalyticsManager,
    private val addNewAssetsUseCase: AddNewAssetsUseCase,
) : ViewModel(), ContainerHost<AddAssetState, AddAssetSideEffect> {
    override val container: Container<AddAssetState, AddAssetSideEffect> =
        container(AddAssetState())

    init {
        intent {
            val group = getGroupByIdOrCreateDefaultUseCase(groupId, GroupFeatureType.Portfolio)
            val groups = groupRepo.getAllSorted(GroupFeatureType.Portfolio)
            reduce {
                state.copy(group = group, availableGroups = groups)
            }
        }
    }

    fun onNavResult(result: SearchNavResult) {
        val type = SearchNavResultType.valueOf(result.key!!)
        when (type) {
            SearchNavResultType.ADD -> handleNavResAddCode(result.code)
            SearchNavResultType.SET -> handleNavResSetCode(result.pos!!, result.code)
        }
    }

    private fun handleNavResAddCode(code: CurrencyCode) =
        intent {
            reduce {
                state.copy(
                    currencies =
                        state.currencies +
                            AmountStr(
                                code,
                                "",
                            ),
                )
            }
        }

    private fun handleNavResSetCode(
        pos: Int,
        code: CurrencyCode,
    ) = intent {
        reduce {
            val newCurrencies = state.currencies.toMutableList()
            newCurrencies[pos] =
                newCurrencies[pos].copy(code = code)
            state.copy(currencies = newCurrencies)
        }
    }

    fun onAssetRemove(removeIndex: Int) =
        intent {
            analyticsManager.logEvent("add_asset_currency_removed")
            reduce {
                state.copy(
                    currencies =
                        state.currencies
                            .filterIndexed { index, _ -> index != removeIndex },
                )
            }
        }

    fun onGroupCreate(name: String) =
        intent {
            analyticsManager.logEvent("add_asset_group_created")
            val group = Group.empty(name = name)
            val inAvailable = state.availableGroups.any { it.name == group.name }
            reduce {
                if (inAvailable) {
                    state.copy(
                        group = group,
                    )
                } else {
                    state.copy(
                        group = group,
                        availableGroups = state.availableGroups + group,
                    )
                }
            }
        }

    fun onGroupSelect(group: Group) =
        intent {
            analyticsManager.logEvent("add_asset_group_selected")
            reduce { state.copy(group = group) }
        }

    // weird bug
    // onAssetValueChange can be called after onAssetRemove with removed pos and empty input
    // this only happens if you try to type wrong characters(e.g. whitespace, "-") before deleting
    // and element being removed is last one in list
    fun onAssetValueChange(
        pos: Int,
        input: String,
    ) = blockingIntent {
        val newCurrencies = state.currencies.toMutableList()
        val old =
            newCurrencies.getOrNull(pos) ?: let {
                Timber.w("onAssetValueChange called with nonexistent pos")
                return@blockingIntent
            }
        val validatedAmount = CurrUtils.validateInput(old.value, input)
        newCurrencies[pos] = newCurrencies[pos].copy(value = validatedAmount)
        reduce {
            state.copy(currencies = newCurrencies)
        }
    }

    fun onAddAsset() =
        intent {
            analyticsManager.logEvent("add_asset_added")
            val group = groupRepo.getByNameOrCreateNew(state.group.name, GroupFeatureType.Portfolio)

            val currencies =
                state.currencies.map {
                    Asset(
                        code = it.code,
                        value = it.value.toBigDecimalArk(),
                        group = group,
                    )
                }
            addNewAssetsUseCase(currencies)
            codeUseStatRepo.codesUsed(*currencies.map { it.code }.toTypedArray())
            val navAssets =
                currencies.map { NavAsset(it.id, it.code, it.value.toPlainString(), it.group.id) }
            postSideEffect(
                AddAssetSideEffect.NavigateBackWithResult(
                    AddAssetsNavResult(navAssets.toTypedArray()),
                ),
            )
        }

    fun onSetCode(index: Int) =
        intent {
            analyticsManager.logEvent("add_asset_set_code_clicked")
            val prohibitedCodes = state.currencies.map { it.code }.toMutableList()
            prohibitedCodes.removeAt(index)
            postSideEffect(AddAssetSideEffect.NavigateSearchSet(index, prohibitedCodes))
        }

    fun onAddCode() =
        intent {
            analyticsManager.logEvent("add_asset_add_code_clicked")
            val prohibitedCodes = state.currencies.map { it.code }
            postSideEffect(AddAssetSideEffect.NavigateSearchAdd(prohibitedCodes))
        }

    fun onBackClick() =
        intent {
            analyticsManager.logEvent("add_asset_back_clicked")
            postSideEffect(AddAssetSideEffect.NavigateBack)
        }
}

class AddAssetViewModelFactory @AssistedInject constructor(
    @Assisted private val groupId: Long?,
    private val assetsRepo: PortfolioRepo,
    private val currencyRepo: CurrencyRepo,
    private val groupRepo: GroupRepo,
    private val getGroupByIdOrCreateDefaultUseCase: GetGroupByIdOrCreateDefaultUseCase,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val analyticsManager: AnalyticsManager,
    private val addNewAssetsUseCase: AddNewAssetsUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddAssetViewModel(
            groupId,
            assetsRepo,
            currencyRepo,
            codeUseStatRepo,
            groupRepo,
            getGroupByIdOrCreateDefaultUseCase,
            analyticsManager,
            addNewAssetsUseCase,
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted groupId: Long?,
        ): AddAssetViewModelFactory
    }
}

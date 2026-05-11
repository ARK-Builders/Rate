package dev.arkbuilders.rate.feature.quick.presentation.add

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
import dev.arkbuilders.rate.core.domain.model.toAmount
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.core.domain.toBigDecimalArk
import dev.arkbuilders.rate.core.domain.toDoubleArk
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.core.domain.usecase.GetGroupByIdOrCreateDefaultUseCase
import dev.arkbuilders.rate.feature.quick.domain.model.QuickCalculation
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import dev.arkbuilders.rate.feature.search.presentation.SearchNavResult
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.time.OffsetDateTime

data class AddQuickScreenState(
    val quickCalculationId: Long? = null,
    val currencies: List<AmountStr> = listOf(AmountStr("USD", "")),
    val group: Group = Group.empty(),
    val availableGroups: List<Group> = emptyList(),
    val finishEnabled: Boolean = false,
    val initialized: Boolean = false,
)

sealed class AddQuickScreenEffect {
    data class NavigateBackWithResult(val newCalculationId: Long) : AddQuickScreenEffect()

    data class NavigateSearchSet(val index: Int, val prohibitedCodes: List<CurrencyCode>) :
        AddQuickScreenEffect()

    data class NavigateSearchAdd(val prohibitedCodes: List<CurrencyCode>) :
        AddQuickScreenEffect()

    data object NavigateBack : AddQuickScreenEffect()
}

enum class SearchNavResultType {
    ADD,
    SET,
}

class AddQuickViewModel(
    private val quickCalculationId: Long?,
    private val newCode: CurrencyCode?,
    private val reuseNotEdit: Boolean,
    private val groupId: Long?,
    private val quickRepo: QuickRepo,
    private val groupRepo: GroupRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    private val getGroupByIdOrCreateDefaultUseCase: GetGroupByIdOrCreateDefaultUseCase,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val analyticsManager: AnalyticsManager,
) : ViewModel(), ContainerHost<AddQuickScreenState, AddQuickScreenEffect> {
    override val container: Container<AddQuickScreenState, AddQuickScreenEffect> =
        container(AddQuickScreenState())

    init {
        intent {
            val groups = groupRepo.getAllSorted(GroupFeatureType.Quick)
            val quickCalculation = quickCalculationId?.let { quickRepo.getById(it) }
            quickCalculation?.let {
                val currencies =
                    listOf(
                        AmountStr(
                            quickCalculation.from,
                            quickCalculation.amount.toPlainString(),
                        ),
                    ) + quickCalculation.to.map { AmountStr(it.code, "") }
                val calc = calcToResult(currencies)

                reduce {
                    state.copy(
                        quickCalculationId = quickCalculationId,
                        currencies = calc,
                        group = quickCalculation.group,
                        availableGroups = groups,
                        initialized = true,
                    )
                }
                checkFinishEnabled()
            } ?: let {
                val group = getGroupByIdOrCreateDefaultUseCase(groupId, GroupFeatureType.Quick)
                val currencies =
                    newCode?.let {
                        listOf(AmountStr(newCode, ""))
                    } ?: state.currencies
                reduce {
                    state.copy(
                        currencies = currencies,
                        availableGroups = groups,
                        group = group,
                        initialized = true,
                    )
                }
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
            val newAmounts = state.currencies + AmountStr(code, "")
            val calc = calcToResult(newAmounts)
            reduce { state.copy(currencies = calc) }
            checkFinishEnabled()
        }

    private fun handleNavResSetCode(
        index: Int,
        code: CurrencyCode,
    ) = intent {
        val mutable = state.currencies.toMutableList()
        val new = mutable[index].copy(code = code)
        mutable[index] = new
        val calc = calcToResult(mutable)
        reduce { state.copy(currencies = calc) }
    }

    fun onCurrencyRemove(removeIndex: Int) =
        intent {
            analyticsManager.logEvent("add_quick_currency_removed")
            reduce {
                state.copy(
                    currencies =
                        state.currencies
                            .filterIndexed { index, _ -> index != removeIndex },
                )
            }
            checkFinishEnabled()
        }

    fun onGroupSelect(group: Group) =
        intent {
            analyticsManager.logEvent("add_quick_group_selected")
            reduce { state.copy(group = group) }
        }

    fun onGroupCreate(name: String) =
        intent {
            analyticsManager.logEvent("add_quick_group_created")
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

    fun onAssetAmountChange(input: String) =
        blockingIntent {
            val from = state.currencies.first()
            val new = from.copy(value = CurrUtils.validateInput(from.value, input))
            val calc = calcToResult(listOf(new) + state.currencies.drop(1))
            reduce { state.copy(currencies = calc) }
            checkFinishEnabled()
        }

    fun onSwapClick() =
        intent {
            if (state.currencies.size < 2)
                return@intent

            analyticsManager.logEvent("add_quick_swap_clicked")

            val newFrom = state.currencies.last()
            val newCurrencies =
                state.currencies.toMutableList().apply {
                    removeAt(lastIndex)
                    add(0, newFrom)
                }

            reduce {
                state.copy(currencies = newCurrencies)
            }
        }

    fun onCurrenciesSwap(
        from: Int,
        to: Int,
    ) = intent {
        analyticsManager.logEvent("add_quick_currencies_reordered")
        val new =
            state.currencies.toMutableList().apply {
                add(to, removeAt(from))
            }
        reduce {
            state.copy(currencies = new)
        }
    }

    fun onAddQuickCalculation() =
        intent {
            val from = state.currencies.first()
            val id =
                if (quickCalculationId != null) {
                    if (reuseNotEdit) 0 else quickCalculationId
                } else {
                    0
                }

            val isNew = id == 0L
            if (isNew) {
                analyticsManager.logEvent("add_quick_calculation_added")
            } else {
                analyticsManager.logEvent("add_quick_calculation_edited")
            }

            val pinnedDate =
                if (id == quickCalculationId) {
                    quickRepo.getById(id)?.pinnedDate
                } else {
                    null
                }

            val group = groupRepo.getByNameOrCreateNew(state.group.name, GroupFeatureType.Quick)

            val quick =
                QuickCalculation(
                    id = id,
                    from = from.code,
                    amount = from.value.toBigDecimalArk(),
                    to = state.currencies.drop(1).map { it.toAmount() },
                    calculatedDate = OffsetDateTime.now(),
                    pinnedDate = pinnedDate,
                    group = group,
                )
            val newId = quickRepo.insert(quick)
            codeUseStatRepo.codesUsed(
                quick.from,
                *quick.to.map { it.code }.toTypedArray(),
            )
            postSideEffect(AddQuickScreenEffect.NavigateBackWithResult(newId))
        }

    private suspend fun calcToResult(old: List<AmountStr>): List<AmountStr> {
        val from = old.first()
        val to = old.drop(1)
        val new =
            to.map {
                if (from.value == "") {
                    it.copy(value = "")
                } else {
                    val (amount, _) = convertUseCase.invoke(from.toAmount(), it.code)
                    val roundValue = CurrUtils.roundOff(amount.value)
                    AmountStr(it.code, roundValue)
                }
            }
        return listOf(from) + new
    }

    private fun checkFinishEnabled() =
        intent {
            val from = state.currencies.first()
            val to = state.currencies.drop(1)

            var finishEnabled = true

            if (from.value.toDoubleArk() == 0.0)
                finishEnabled = false

            if (to.isEmpty())
                finishEnabled = false

            reduce {
                state.copy(finishEnabled = finishEnabled)
            }
        }

    fun onSetCode(index: Int) =
        intent {
            analyticsManager.logEvent("add_quick_set_code_clicked")
            val prohibitedCodes =
                state.currencies.map { it.code }.toMutableList().apply {
                    removeAt(index)
                }
            postSideEffect(AddQuickScreenEffect.NavigateSearchSet(index, prohibitedCodes))
        }

    fun onAddCode() =
        intent {
            analyticsManager.logEvent("add_quick_add_code_clicked")
            val prohibitedCodes = state.currencies.map { it.code }
            postSideEffect(AddQuickScreenEffect.NavigateSearchAdd(prohibitedCodes))
        }

    fun onBackClick() =
        intent {
            analyticsManager.logEvent("quick_back_clicked")
            postSideEffect(AddQuickScreenEffect.NavigateBack)
        }
}

class AddQuickViewModelFactory @AssistedInject constructor(
    @Assisted("calculationId") private val quickCalculationId: Long?,
    @Assisted("newCode") private val newCode: CurrencyCode?,
    @Assisted private val reuseNotEdit: Boolean,
    @Assisted("groupId") private val groupId: Long?,
    private val quickRepo: QuickRepo,
    private val groupRepo: GroupRepo,
    private val codeUseStatRepo: CodeUseStatRepo,
    private val getGroupByIdOrCreateDefaultUseCase: GetGroupByIdOrCreateDefaultUseCase,
    private val convertUseCase: ConvertWithRateUseCase,
    private val analyticsManager: AnalyticsManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddQuickViewModel(
            quickCalculationId,
            newCode,
            reuseNotEdit,
            groupId,
            quickRepo,
            groupRepo,
            convertUseCase,
            getGroupByIdOrCreateDefaultUseCase,
            codeUseStatRepo,
            analyticsManager,
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("calculationId") quickCalculationId: Long?,
            @Assisted("newCode") newCode: CurrencyCode?,
            reuseNotEdit: Boolean,
            @Assisted("groupId") group: Long?,
        ): AddQuickViewModelFactory
    }
}

package dev.arkbuilders.rate.feature.portfolio.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.Prefs
import dev.arkbuilders.rate.core.domain.toBigDecimalArk
import dev.arkbuilders.rate.feature.portfolio.domain.model.Asset
import dev.arkbuilders.rate.feature.portfolio.domain.repo.PortfolioRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

data class EditAssetScreenState(
    val asset: Asset = Asset.EMPTY,
    val info: CurrencyInfo = CurrencyInfo.EMPTY,
    val value: String = "",
    val initialized: Boolean = false,
)

sealed class EditAssetScreenEffect {
    data object NavigateBack : EditAssetScreenEffect()
}

private val PERSIST_AMOUNT_DEBOUNCE = 300L

class EditAssetViewModel(
    private val assetId: Long,
    private val currencyRepo: CurrencyRepo,
    private val assetsRepo: PortfolioRepo,
    private val prefs: Prefs,
    private val analyticsManager: AnalyticsManager,
) : ViewModel(), ContainerHost<EditAssetScreenState, EditAssetScreenEffect> {
    override val container: Container<EditAssetScreenState, EditAssetScreenEffect> =
        container(EditAssetScreenState())

    private val inputFlow = MutableSharedFlow<String>()

    init {
        intent {
            val asset = assetsRepo.getById(assetId)
            val name = currencyRepo.infoByCode(asset!!.code)

            inputFlow.debounce(PERSIST_AMOUNT_DEBOUNCE).onEach {
                assetsRepo.setAsset(asset.copy(value = it.toBigDecimalArk()))
            }.launchIn(viewModelScope)

            reduce {
                state.copy(asset, name, asset.value.toPlainString(), initialized = true)
            }
        }
    }

    fun onValueChange(input: String) =
        blockingIntent {
            val validated = CurrUtils.validateInput(state.value, input)
            inputFlow.emit(validated)
            reduce {
                state.copy(value = validated)
            }
        }

    fun onBackClick() =
        intent {
            analyticsManager.logEvent("edit_asset_back_clicked")
            postSideEffect(EditAssetScreenEffect.NavigateBack)
        }
}

class EditAssetViewModelFactory @AssistedInject constructor(
    @Assisted private val amountId: Long,
    private val currencyRepo: CurrencyRepo,
    private val assetsRepo: PortfolioRepo,
    private val prefs: Prefs,
    private val analyticsManager: AnalyticsManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditAssetViewModel(
            amountId,
            currencyRepo,
            assetsRepo,
            prefs,
            analyticsManager,
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(amountId: Long): EditAssetViewModelFactory
    }
}

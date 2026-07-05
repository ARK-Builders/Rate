package dev.arkbuilders.rate.feature.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.arkbuilders.rate.core.domain.repo.PremiumManager
import dev.arkbuilders.rate.feature.paywall.di.PaywallScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

enum class PaywallPlan {
    Yearly,
    Monthly,
}

data class PaywallScreenState(
    val selectedPlan: PaywallPlan = PaywallPlan.Yearly,
    val isPremium: Boolean = false,
)

sealed class PaywallScreenEffect {
    data object NavigateBack : PaywallScreenEffect()

    data class Purchase(
        val productId: String,
    ) : PaywallScreenEffect()
}

class PaywallViewModel(
    private val premiumManager: PremiumManager,
) : ViewModel(), ContainerHost<PaywallScreenState, PaywallScreenEffect> {
    override val container: Container<PaywallScreenState, PaywallScreenEffect> =
        container(PaywallScreenState(isPremium = premiumManager.isPremium()))

    init {
        intent {
            premiumManager
                .premiumState
                .onEach { isPremium ->
                    reduce {
                        state.copy(isPremium = isPremium)
                    }
                }.launchIn(viewModelScope)

            premiumManager.refresh()
        }
    }

    fun onCloseClick() =
        intent {
            postSideEffect(PaywallScreenEffect.NavigateBack)
        }

    fun onRestoreClick() =
        intent {
            premiumManager.refresh()
        }

    fun onYearlyPlanClick() =
        intent {
            reduce {
                state.copy(selectedPlan = PaywallPlan.Yearly)
            }
        }

    fun onMonthlyPlanClick() =
        intent {
            reduce {
                state.copy(selectedPlan = PaywallPlan.Monthly)
            }
        }

    fun onPrimaryClick() =
        intent {
            val productId =
                when (state.selectedPlan) {
                    PaywallPlan.Yearly -> PremiumManager.TEST_YEARLY_PRODUCT_ID
                    PaywallPlan.Monthly -> PremiumManager.TEST_MONTHLY_PRODUCT_ID
                }

            postSideEffect(PaywallScreenEffect.Purchase(productId))
        }

    fun onContinueFreeClick() =
        intent {
            postSideEffect(PaywallScreenEffect.NavigateBack)
        }

    fun onTermsClick() {
    }

    fun onPrivacyClick() {
    }

    fun onRestorePurchasesClick() {
    }

    fun onBackClick() =
        intent {
            postSideEffect(PaywallScreenEffect.NavigateBack)
        }
}

@PaywallScope
class PaywallViewModelFactory @Inject constructor(
    private val premiumManager: PremiumManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PaywallViewModel(
            premiumManager,
        ) as T
    }
}

package dev.arkbuilders.rate.feature.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.arkbuilders.rate.feature.paywall.di.PaywallScope
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
)

sealed class PaywallScreenEffect {
    data object NavigateBack : PaywallScreenEffect()
}

class PaywallViewModel : ViewModel(), ContainerHost<PaywallScreenState, PaywallScreenEffect> {
    override val container: Container<PaywallScreenState, PaywallScreenEffect> =
        container(PaywallScreenState())

    fun onCloseClick() =
        intent {
            postSideEffect(PaywallScreenEffect.NavigateBack)
        }

    fun onRestoreClick() {
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

    fun onPrimaryClick() {
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
class PaywallViewModelFactory @Inject constructor() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PaywallViewModel() as T
    }
}

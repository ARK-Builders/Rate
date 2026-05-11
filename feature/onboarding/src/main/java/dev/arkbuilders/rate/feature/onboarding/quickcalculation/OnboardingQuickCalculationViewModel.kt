package dev.arkbuilders.rate.feature.onboarding.quickcalculation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.PreferenceKey
import dev.arkbuilders.rate.core.domain.repo.Prefs
import dev.arkbuilders.rate.feature.quick.domain.model.QuickCalculation
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

enum class OnboardingQuickCalcStep {
    PairSwipeToRight,
    PairSwipeToLeft,
    PinnedSwipeToRight,
    PairMenu,
}

data class OnboardingQuickCalcState(
    val calculation: QuickCalculation = QuickCalculation.empty(),
    val stepIndex: Int = 0,
    val currencies: List<CurrencyInfo> = emptyList(),
    val initialized: Boolean = false,
)

sealed class OnboardingQuickCalcEffect {
    data object NavBack : OnboardingQuickCalcEffect()

    data object Finish : OnboardingQuickCalcEffect()
}

class OnboardingQuickCalculationViewModel(
    private val quickRepo: QuickRepo,
    private val prefs: Prefs,
    private val currencyRepo: CurrencyRepo,
) : ViewModel(), ContainerHost<OnboardingQuickCalcState, OnboardingQuickCalcEffect> {
    override val container: Container<OnboardingQuickCalcState, OnboardingQuickCalcEffect> =
        container(OnboardingQuickCalcState())

    init {
        intent {
            val calculation = quickRepo.getAll().first()
            val currencies = currencyRepo.getCurrencyInfo()
            reduce {
                state.copy(
                    calculation = calculation,
                    currencies = currencies,
                    initialized = true,
                )
            }
        }
    }

    fun onNext() =
        intent {
            val nextIndex = state.stepIndex + 1

            if (nextIndex == OnboardingQuickCalcStep.entries.lastIndex + 1) {
                finish()
                return@intent
            }

            reduce {
                state.copy(stepIndex = nextIndex)
            }
        }

    fun onBack() =
        intent {
            val prevIndex = state.stepIndex - 1
            if (prevIndex < 0) {
                postSideEffect(OnboardingQuickCalcEffect.NavBack)
                return@intent
            }

            reduce {
                state.copy(stepIndex = prevIndex)
            }
        }

    fun onSkip() {
        finish()
    }

    private fun finish() =
        intent {
            prefs.set(PreferenceKey.IsOnboardingQuickCalculationCompleted, true)
            postSideEffect(OnboardingQuickCalcEffect.Finish)
        }
}

class OnboardingQuickCalcViewModelFactory @AssistedInject constructor(
    @Assisted private val quickRepo: QuickRepo,
    private val prefs: Prefs,
    private val currencyRepo: CurrencyRepo,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OnboardingQuickCalculationViewModel(
            quickRepo,
            prefs,
            currencyRepo,
        ) as T
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted quickRepo: QuickRepo,
        ): OnboardingQuickCalcViewModelFactory
    }
}

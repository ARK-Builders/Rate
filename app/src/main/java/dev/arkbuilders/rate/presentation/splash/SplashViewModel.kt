package dev.arkbuilders.rate.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.arkbuilders.rate.core.domain.BuildConfigFields
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.PreferenceKey
import dev.arkbuilders.rate.core.domain.repo.Prefs
import dev.arkbuilders.rate.feature.portfolio.domain.repo.PortfolioRepo
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

sealed class SplashScreenEffect {
    data object NavigateOnboarding : SplashScreenEffect()

    data object NavigateQuick : SplashScreenEffect()
}

class SplashViewModel(
    private val quickRepo: QuickRepo,
    private val portfolioRepo: PortfolioRepo,
    private val prefs: Prefs,
    private val buildConfigFields: BuildConfigFields,
    private val currencyRepo: CurrencyRepo,
) : ViewModel(), ContainerHost<Unit, SplashScreenEffect> {
    override val container: Container<Unit, SplashScreenEffect> = container(Unit)

    init {
        intent {
            currencyRepo.initialize()

            val currentVersionCode = buildConfigFields.versionCode
            val previousVersionCode = prefs.get(PreferenceKey.CurrentVersionCode)

            val firstLaunch = prefs.get(PreferenceKey.AppLaunchCount) == 0L
            if (firstLaunch) {
                skipOnboardingIfUserHasData()
                prefs.set(PreferenceKey.FirstInstallVersionCode, buildConfigFields.versionCode)
            }

            prefs.incrementAppLaunchCount()

            if (previousVersionCode != null && previousVersionCode < currentVersionCode) {
                onAppUpdate(previousVersionCode, currentVersionCode)
            }

            prefs.set(PreferenceKey.CurrentVersionCode, currentVersionCode)

            val needToShowOnboarding = prefs.get(PreferenceKey.IsOnboardingCompleted).not()
            if (needToShowOnboarding)
                postSideEffect(SplashScreenEffect.NavigateOnboarding)
            else
                postSideEffect(SplashScreenEffect.NavigateQuick)
        }
    }

    private suspend fun skipOnboardingIfUserHasData() {
        if (quickRepo.getAll().isNotEmpty() || portfolioRepo.allAssets().isNotEmpty()) {
            prefs.set(PreferenceKey.IsOnboardingCompleted, true)
            prefs.set(PreferenceKey.IsOnboardingQuickCalculationCompleted, true)
        }
    }

    private suspend fun onAppUpdate(
        previousCode: Int,
        currentCode: Int,
    ) {
    }
}

class SplashViewModelFactory(
    private val quickRepo: QuickRepo,
    private val portfolioRepo: PortfolioRepo,
    private val prefs: Prefs,
    private val buildConfigFields: BuildConfigFields,
    private val currencyRepo: CurrencyRepo,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SplashViewModel(
            quickRepo,
            portfolioRepo,
            prefs,
            buildConfigFields,
            currencyRepo,
        ) as T
    }
}

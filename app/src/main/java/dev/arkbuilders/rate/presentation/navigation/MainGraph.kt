package dev.arkbuilders.rate.presentation.navigation

import com.ramcosta.composedestinations.animations.defaults.DefaultFadingTransitions
import com.ramcosta.composedestinations.annotation.ExternalDestination
import com.ramcosta.composedestinations.annotation.NavHostGraph
import com.ramcosta.composedestinations.generated.onboarding.destinations.OnboardingQuickCalculationScreenDestination
import com.ramcosta.composedestinations.generated.onboarding.destinations.OnboardingQuickScreenDestination
import com.ramcosta.composedestinations.generated.onboarding.destinations.OnboardingScreenDestination
import com.ramcosta.composedestinations.generated.portfolio.destinations.AddAssetScreenDestination
import com.ramcosta.composedestinations.generated.portfolio.destinations.EditAssetScreenDestination
import com.ramcosta.composedestinations.generated.portfolio.destinations.PortfolioScreenDestination
import com.ramcosta.composedestinations.generated.quick.destinations.AddQuickScreenDestination
import com.ramcosta.composedestinations.generated.quick.destinations.QuickScreenDestination
import com.ramcosta.composedestinations.generated.search.destinations.SearchCurrencyScreenDestination
import com.ramcosta.composedestinations.generated.settings.destinations.AboutScreenDestination
import com.ramcosta.composedestinations.generated.settings.destinations.SettingsScreenDestination

@NavHostGraph(
    defaultTransitions = DefaultFadingTransitions::class,
)
annotation class MainGraph {
    @ExternalDestination<OnboardingScreenDestination>
    @ExternalDestination<OnboardingQuickScreenDestination>
    @ExternalDestination<OnboardingQuickCalculationScreenDestination>
    @ExternalDestination<QuickScreenDestination>
    @ExternalDestination<AddQuickScreenDestination>
    @ExternalDestination<PortfolioScreenDestination>
    @ExternalDestination<AddAssetScreenDestination>
    @ExternalDestination<EditAssetScreenDestination>
    @ExternalDestination<SettingsScreenDestination>
    @ExternalDestination<AboutScreenDestination>
    @ExternalDestination<SearchCurrencyScreenDestination>
    companion object Includes
}

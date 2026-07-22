package dev.arkbuilders.rate.presentation.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.onboarding.destinations.OnboardingScreenDestination
import com.ramcosta.composedestinations.generated.quick.destinations.QuickScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.utils.startDestination
import dev.arkbuilders.rate.core.presentation.ui.LoadingScreen
import dev.arkbuilders.rate.feature.portfolio.di.PortfolioComponentHolder
import dev.arkbuilders.rate.feature.quick.di.QuickComponentHolder
import dev.arkbuilders.rate.presentation.App
import dev.arkbuilders.rate.presentation.navigation.MainGraph
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
@Destination<MainGraph>(start = true)
fun SplashScreen(navigator: DestinationsNavigator) {
    val ctx = LocalContext.current
    val coreComponent =
        remember {
            App.instance.coreComponent
        }
    val quickComponent =
        remember {
            QuickComponentHolder.provide(ctx)
        }
    val portfolioComponent =
        remember {
            PortfolioComponentHolder.provide(ctx)
        }
    val viewModel: SplashViewModel =
        viewModel(
            factory =
                SplashViewModelFactory(
                    quickComponent.quickRepo(),
                    portfolioComponent.assetsRepo(),
                    coreComponent.prefs(),
                    coreComponent.buildConfigFields(),
                    coreComponent.currencyRepo(),
                ),
        )

    viewModel.collectSideEffect { effect ->
        when (effect) {
            SplashScreenEffect.NavigateOnboarding ->
                navigator.navigate(OnboardingScreenDestination) {
                    popUpTo(NavGraphs.main.startDestination) {
                        inclusive = true
                    }
                }
            SplashScreenEffect.NavigateQuick ->
                navigator.navigate(QuickScreenDestination) {
                    popUpTo(NavGraphs.main.startDestination) {
                        inclusive = true
                    }
                }
        }
    }

    LoadingScreen()
}

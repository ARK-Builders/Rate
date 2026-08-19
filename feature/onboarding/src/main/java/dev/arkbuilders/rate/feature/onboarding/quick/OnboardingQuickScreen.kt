package dev.arkbuilders.rate.feature.onboarding.quick

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppButton
import dev.arkbuilders.rate.feature.onboarding.OnboardingExternalNavigator
import dev.arkbuilders.rate.feature.onboarding.di.OnboardingComponentHolder
import dev.arkbuilders.rate.feature.onboarding.spotlight.Spotlight
import dev.arkbuilders.rate.feature.onboarding.spotlight.SpotlightShape
import dev.arkbuilders.rate.feature.onboarding.spotlight.SpotlightTooltip
import dev.arkbuilders.rate.feature.onboarding.spotlight.TooltipPosition
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
@Destination<ExternalModuleGraph>
fun OnboardingQuickScreen(
    navigator: DestinationsNavigator,
    externalNavigator: OnboardingExternalNavigator,
) {
    val ctx = LocalContext.current
    val onboardingComponent =
        remember {
            OnboardingComponentHolder.provide(ctx)
        }
    val viewModel: OnboardingQuickViewModel =
        viewModel(
            factory =
                onboardingComponent.onboardingQuickViewModelFactory(),
        )

    val state by viewModel.collectAsState()

    BackHandler {
        viewModel.onBack()
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            OnboardingQuickEffect.Finish -> externalNavigator.navigateOnFinish()
            OnboardingQuickEffect.NavBack -> navigator.popBackStack()
        }
    }

    var calculateRect by remember { (mutableStateOf<Rect?>(null)) }
    var portfolioRect by remember { (mutableStateOf<Rect?>(null)) }
    var pairAlertRect by remember { (mutableStateOf<Rect?>(null)) }

    Column(modifier = Modifier.fillMaxSize()) {
        QuickEmpty(
            modifier =
                Modifier
                    .weight(1f)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
                        ),
                    ),
            calculateModifier =
                Modifier.onGloballyPositioned { coordinates ->
                    calculateRect = coordinates.boundsInRoot()
                },
        )

        MockBottomNavigation(
            portfolioModifier =
                Modifier.onGloballyPositioned { coordinates ->
                    portfolioRect = coordinates.boundsInRoot()
                },
            pairAlertModifier =
                Modifier.onGloballyPositioned { coordinates ->
                    pairAlertRect = coordinates.boundsInRoot()
                },
        )
    }
    when (OnboardingQuickStep.entries[state.stepIndex]) {
        OnboardingQuickStep.Calculate -> {
            calculateRect?.let {
                Spotlight(
                    targetRect = it,
                    onTargetClicked = {},
                    onDismiss = { viewModel.onNext() },
                    shape = SpotlightShape.Circle,
                    padding = 20.dp,
                )
                SpotlightTooltip(
                    targetRect = it,
                    titleText = stringResource(CoreRString.onboarding_quick_empty_1_title),
                    descText = stringResource(CoreRString.onboarding_quick_empty_1_desc),
                    buttonText = stringResource(CoreRString.okay),
                    position = TooltipPosition.Below,
                    targetPadding = 40.dp,
                    onClick = { viewModel.onNext() },
                )
            }
        }

        OnboardingQuickStep.Portfolio -> {
            portfolioRect?.let {
                Spotlight(
                    targetRect = it,
                    onTargetClicked = {},
                    onDismiss = { viewModel.onNext() },
                    shape = SpotlightShape.Circle,
                    padding = 20.dp,
                )
                SpotlightTooltip(
                    targetRect = it,
                    titleText = stringResource(CoreRString.onboarding_quick_empty_2_title),
                    descText = stringResource(CoreRString.onboarding_quick_empty_2_desc),
                    buttonText = stringResource(CoreRString.okay),
                    position = TooltipPosition.Above,
                    targetPadding = 40.dp,
                    onClick = { viewModel.onNext() },
                )
            }
        }

//        OnboardingQuickStep.PairAlerts -> {
//            pairAlertRect?.let {
//                Spotlight(
//                    targetRect = it,
//                    onTargetClicked = {},
//                    onDismiss = {},
//                    shape = SpotlightShape.Circle,
//                    padding = 20.dp,
//                )
//                SpotlightTooltip(
//                    targetRect = it,
//                    titleText = stringResource(CoreRString.onboarding_quick_empty_3_title),
//                    descText = stringResource(CoreRString.onboarding_quick_empty_3_desc),
//                    buttonText = stringResource(CoreRString.finish),
//                    position = TooltipPosition.Above,
//                    targetPadding = 40.dp,
//                    onClick = { viewModel.onNext() },
//                )
//            }
//        }
    }
}

@Composable
fun QuickEmpty(
    modifier: Modifier,
    calculateModifier: Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = CoreRDrawable.ic_empty_quick),
                contentDescription = null,
                tint = Color.Unspecified,
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(CoreRString.quick_empty_title),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = ArkColor.TextPrimary,
            )
            Text(
                modifier = Modifier.padding(top = 6.dp, start = 24.dp, end = 24.dp),
                text = stringResource(CoreRString.quick_empty_desc),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = ArkColor.TextTertiary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            AppButton(
                modifier = calculateModifier,
                onClick = {
                },
            ) {
                Icon(
                    painter = painterResource(id = CoreRDrawable.ic_add),
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(CoreRString.calculate),
                )
            }
        }
    }
}

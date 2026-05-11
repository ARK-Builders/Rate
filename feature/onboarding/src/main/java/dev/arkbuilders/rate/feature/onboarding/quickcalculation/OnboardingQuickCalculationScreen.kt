package dev.arkbuilders.rate.feature.onboarding.quickcalculation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.CurrencyInfoItem
import dev.arkbuilders.rate.core.presentation.ui.ListHeader
import dev.arkbuilders.rate.core.presentation.ui.SearchTextField
import dev.arkbuilders.rate.feature.onboarding.di.OnboardingComponentHolder
import dev.arkbuilders.rate.feature.onboarding.quick.MockBottomNavigation
import dev.arkbuilders.rate.feature.onboarding.quick.MockQuickItem
import dev.arkbuilders.rate.feature.onboarding.spotlight.Spotlight
import dev.arkbuilders.rate.feature.onboarding.spotlight.SpotlightShape
import dev.arkbuilders.rate.feature.onboarding.spotlight.SpotlightTooltip
import dev.arkbuilders.rate.feature.onboarding.spotlight.TooltipPosition
import dev.arkbuilders.rate.feature.quick.di.QuickComponentHolder
import dev.arkbuilders.rate.feature.quick.presentation.ui.QuickDateFormatter
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
@Destination<ExternalModuleGraph>
fun OnboardingQuickCalculationScreen(navigator: DestinationsNavigator) {
    val ctx = LocalContext.current
    val quickComponent =
        remember {
            QuickComponentHolder.provide(ctx)
        }
    val onboardingComponent =
        remember {
            OnboardingComponentHolder.provide(ctx)
        }
    val viewModel: OnboardingQuickCalculationViewModel =
        viewModel(
            factory =
                onboardingComponent.onboardingQuickCalcViewModelFactory()
                    .create(quickComponent.quickRepo()),
        )

    var quickCalculationRect by remember { (mutableStateOf<Rect?>(null)) }

    val quickCalcTranslatePx by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = with(LocalDensity.current) { 120.dp.toPx() },
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 1800,
                        delayMillis = 500,
                        easing = LinearEasing,
                    ),
            ),
    )

    val state by viewModel.collectAsState()

    BackHandler {
        viewModel.onBack()
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            OnboardingQuickCalcEffect.Finish -> navigator.popBackStack()
            OnboardingQuickCalcEffect.NavBack -> navigator.popBackStack()
        }
    }

    val step = OnboardingQuickCalcStep.entries.get(state.stepIndex)

    val translateCalcModifier =
        when (step) {
            OnboardingQuickCalcStep.PairSwipeToRight ->
                Modifier.graphicsLayer { translationX = quickCalcTranslatePx }

            OnboardingQuickCalcStep.PairSwipeToLeft ->
                Modifier.graphicsLayer { translationX = -quickCalcTranslatePx }

            OnboardingQuickCalcStep.PinnedSwipeToRight ->
                Modifier.graphicsLayer {
                    translationX = quickCalcTranslatePx
                }

            else -> Modifier
        }

    if (state.initialized.not())
        return

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        floatingActionButton = {
            FloatingActionButton(
                contentColor = Color.White,
                containerColor = ArkColor.Secondary,
                shape = CircleShape,
                onClick = {},
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(CoreRString.add))
            }
        },
        bottomBar = {
            MockBottomNavigation(
                portfolioModifier = Modifier,
                pairAlertModifier = Modifier,
            )
        },
    ) {
        Column(Modifier.padding(it)) {
            SearchTextField(
                modifier =
                    Modifier.padding(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp,
                    ),
                text = "",
            ) {}
            Column(
                modifier =
                    Modifier.onGloballyPositioned { coordinates ->
                        quickCalculationRect = coordinates.boundsInRoot()
                    },
            ) {
                if (state.stepIndex <= 1) {
                    ListHeader(text = stringResource(CoreRString.quick_calculations))
                } else {
                    ListHeader(text = stringResource(CoreRString.quick_pinned_calculations))
                }
                Box(propagateMinConstraints = true) {
                    DismissBackground(
                        swipeToDelete =
                            step == OnboardingQuickCalcStep.PairSwipeToLeft,
                        isPinned =
                            step == OnboardingQuickCalcStep.PinnedSwipeToRight,
                    )
                    MockQuickItem(
                        modifier = translateCalcModifier,
                        from = Amount(state.calculation.from, state.calculation.amount),
                        to = state.calculation.to,
                        dateText =
                            QuickDateFormatter.calculationCalculatedTime(
                                ctx,
                                state.calculation.calculatedDate,
                            ),
                    ) { }
                }
            }
            ListHeader(text = stringResource(CoreRString.all_currencies))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(state.currencies, key = { it.code }) { name ->
                    CurrencyInfoItem(name) { }
                }
            }
        }
    }

    quickCalculationRect?.let {
        Spotlight(
            targetRect = it,
            onTargetClicked = {},
            onDismiss = { viewModel.onNext() },
            shape = SpotlightShape.Rect,
            padding = 0.dp,
        )
    }

    when (step) {
        OnboardingQuickCalcStep.PairSwipeToRight -> {
            quickCalculationRect?.let {
                SpotlightTooltip(
                    targetRect = it,
                    titleText = stringResource(CoreRString.onboarding_quick_calc_1_title),
                    descText = stringResource(CoreRString.onboarding_quick_calc_1_desc),
                    buttonText = stringResource(CoreRString.next),
                    position = TooltipPosition.Below,
                    targetPadding = 24.dp,
                    onClick = { viewModel.onNext() },
                    onSkip = { viewModel.onSkip() },
                )
            }
        }

        OnboardingQuickCalcStep.PairSwipeToLeft -> {
            quickCalculationRect?.let {
                SpotlightTooltip(
                    targetRect = it,
                    titleText = stringResource(CoreRString.onboarding_quick_calc_2_title),
                    descText = stringResource(CoreRString.onboarding_quick_calc_2_desc),
                    buttonText = stringResource(CoreRString.next),
                    position = TooltipPosition.Below,
                    targetPadding = 24.dp,
                    onClick = { viewModel.onNext() },
                    onSkip = { viewModel.onSkip() },
                )
            }
        }

        OnboardingQuickCalcStep.PinnedSwipeToRight -> {
            quickCalculationRect?.let {
                SpotlightTooltip(
                    targetRect = it,
                    titleText = stringResource(CoreRString.onboarding_quick_calc_3_title),
                    descText = stringResource(CoreRString.onboarding_quick_calc_3_desc),
                    buttonText = stringResource(CoreRString.next),
                    position = TooltipPosition.Below,
                    targetPadding = 24.dp,
                    onClick = { viewModel.onNext() },
                    onSkip = { viewModel.onSkip() },
                )
            }
        }

        OnboardingQuickCalcStep.PairMenu -> {
            quickCalculationRect?.let {
                SpotlightTooltip(
                    targetRect = it,
                    titleText = stringResource(CoreRString.onboarding_quick_calc_4_title),
                    descText = stringResource(CoreRString.onboarding_quick_calc_4_desc),
                    buttonText = stringResource(CoreRString.finish),
                    position = TooltipPosition.Below,
                    targetPadding = 24.dp,
                    onClick = { viewModel.onNext() },
                )
            }
        }
    }
}

@Composable
private fun BoxScope.DismissBackground(
    swipeToDelete: Boolean,
    isPinned: Boolean,
) {
    val color =
        if (swipeToDelete) {
            ArkColor.UtilityError500
        } else {
            if (isPinned)
                ArkColor.FGQuarterary
            else
                ArkColor.Secondary
        }

    Row(
        modifier =
            Modifier
                .matchParentSize()
                .background(color),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (swipeToDelete.not()) {
            if (isPinned.not()) {
                Row {
                    Icon(
                        modifier = Modifier.padding(start = 17.dp),
                        painter = painterResource(id = R.drawable.ic_pin),
                        contentDescription = null,
                        tint = Color.White,
                    )
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = stringResource(R.string.pin),
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    )
                }
            } else {
                Text(
                    modifier = Modifier.padding(start = 17.dp),
                    text = stringResource(R.string.unpin),
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
            }
        }
        Spacer(modifier = Modifier)
        if (swipeToDelete) {
            Row {
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = stringResource(R.string.delete),
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
                Icon(
                    modifier = Modifier.padding(end = 17.dp),
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        }
    }
}

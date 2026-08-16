package dev.arkbuilders.rate.feature.onboarding.spotlight

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppButton
import dev.arkbuilders.rate.core.presentation.ui.calculateEndPadding
import dev.arkbuilders.rate.core.presentation.ui.calculateStartPadding
import dev.arkbuilders.rate.core.presentation.ui.group.ArkOutlinedButton
import kotlin.math.roundToInt

enum class TooltipPosition { Above, Below }

@Composable
fun SpotlightTooltip(
    modifier: Modifier = Modifier,
    targetRect: Rect,
    titleText: String,
    descText: String,
    buttonText: String,
    position: TooltipPosition,
    targetPadding: Dp = 12.dp,
    onClick: () -> Unit,
    onSkip: (() -> Unit)? = null,
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenHeightPx = with(density) { screenHeight.toPx() }
    val safeDrawingPadding = WindowInsets.safeDrawing.asPaddingValues()
    val safeTopPx = with(density) { safeDrawingPadding.calculateTopPadding().toPx() }
    val safeBottomBoundary =
        screenHeightPx -
            with(density) { safeDrawingPadding.calculateBottomPadding().toPx() }
    val paddingPx = with(density) { targetPadding.toPx() }

    var tooltipHeightPx by remember { mutableStateOf(0f) }

    val tooltipOffsetY =
        run {
            val aboveY = targetRect.top - tooltipHeightPx - paddingPx
            val belowY = targetRect.bottom + paddingPx

            val preferredY =
                when (position) {
                    TooltipPosition.Above -> {
                        if (aboveY >= safeTopPx) aboveY else belowY
                    }
                    TooltipPosition.Below -> {
                        if (belowY + tooltipHeightPx <= safeBottomBoundary) belowY else aboveY
                    }
                }

            preferredY.coerceIn(
                minimumValue = safeTopPx,
                maximumValue =
                    (safeBottomBoundary - tooltipHeightPx)
                        .coerceAtLeast(safeTopPx),
            )
        }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    start = safeDrawingPadding.calculateStartPadding(layoutDirection) + 16.dp,
                    end = safeDrawingPadding.calculateEndPadding(layoutDirection) + 16.dp,
                )
                .absoluteOffset { IntOffset(0, tooltipOffsetY.roundToInt()) },
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        tooltipHeightPx = it.size.height.toFloat()
                    },
            shape = RoundedCornerShape(12.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Column(
                modifier =
                    Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 20.dp,
                        bottom = 16.dp,
                    ),
            ) {
                Text(
                    text = titleText,
                    fontWeight = FontWeight.W600,
                    fontSize = 18.sp,
                    color = ArkColor.TextPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = descText,
                    color = ArkColor.TextTertiary,
                )
                Spacer(Modifier.height(24.dp))
                if (onSkip != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AppButton(
                            modifier = Modifier.weight(1f),
                            onClick = { onClick() },
                        ) {
                            Text(
                                text = stringResource(CoreRString.next),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W600,
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        ArkOutlinedButton(
                            modifier = Modifier.weight(1f),
                            text = stringResource(CoreRString.skip),
                        ) { onSkip?.invoke() }
                    }
                } else {
                    ArkOutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = buttonText,
                    ) { onClick() }
                }
            }
        }
    }
}

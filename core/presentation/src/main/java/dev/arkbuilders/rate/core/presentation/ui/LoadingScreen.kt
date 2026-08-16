package dev.arkbuilders.rate.core.presentation.ui

import androidx.annotation.IntRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        InstaSpinner(
            size = 60.dp,
            color = dev.arkbuilders.rate.core.presentation.theme.ArkColor.Secondary,
        )
    }
}

// "fork" of https://github.com/commandiron/ComposeLoading
@Composable
private fun InstaSpinner(
    modifier: Modifier = Modifier,
    durationMillis: Int = 1000,
    size: Dp = 40.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    isRefreshing: Boolean = false,
) {
    val transition = rememberInfiniteTransition()

    val alpha1 =
        transition.fractionTransition(
            initialValue = 1f,
            targetValue = 0.1f,
            durationMillis = durationMillis,
            easing = EaseInOut,
        )
    val alpha2 =
        transition.fractionTransition(
            initialValue = 1f,
            targetValue = 0.1f,
            durationMillis = durationMillis,
            offsetMillis = durationMillis / 8,
            easing = EaseInOut,
        )
    val alpha3 =
        transition.fractionTransition(
            initialValue = 1f,
            targetValue = 0.1f,
            durationMillis = durationMillis,
            offsetMillis = durationMillis * 2 / 8,
            easing = EaseInOut,
        )
    val alpha4 =
        transition.fractionTransition(
            initialValue = 1f,
            targetValue = 0.1f,
            durationMillis = durationMillis,
            offsetMillis = durationMillis * 3 / 8,
            easing = EaseInOut,
        )
    val alpha5 =
        transition.fractionTransition(
            initialValue = 1f,
            targetValue = 0.1f,
            durationMillis = durationMillis,
            offsetMillis = durationMillis * 4 / 8,
            easing = EaseInOut,
        )
    val alpha6 =
        transition.fractionTransition(
            initialValue = 1f,
            targetValue = 0.1f,
            durationMillis = durationMillis,
            offsetMillis = durationMillis * 5 / 8,
            easing = EaseInOut,
        )
    val alpha7 =
        transition.fractionTransition(
            initialValue = 1f,
            targetValue = 0.1f,
            durationMillis = durationMillis,
            offsetMillis = durationMillis * 6 / 8,
            easing = EaseInOut,
        )
    val alpha8 =
        transition.fractionTransition(
            initialValue = 1f,
            targetValue = 0.1f,
            durationMillis = durationMillis,
            offsetMillis = durationMillis * 7 / 8,
            easing = EaseInOut,
        )

    val rotateDegree =
        remember {
            Animatable(0f)
        }

    LaunchedEffect(key1 = isRefreshing) {
        if (isRefreshing) {
            rotateDegree.animateTo(
                targetValue = 360f * 2,
                animationSpec =
                    infiniteRepeatable(
                        animation =
                            tween(
                                durationMillis = durationMillis * 2,
                                easing = LinearEasing,
                            ),
                    ),
            )
        }
    }

    Canvas(
        modifier =
            modifier
                .size(size)
                .rotate(rotateDegree.value),
    ) {
        val rectSize =
            Size(width = this.size.width / 4, height = this.size.height / 30)
        for (i in 0 until 8) {
            rotate(45f * i) {
                drawRect(
                    color =
                        color.copy(
                            alpha =
                                when (i) {
                                    0 -> alpha1.value
                                    1 -> alpha2.value
                                    2 -> alpha3.value
                                    3 -> alpha4.value
                                    4 -> alpha5.value
                                    5 -> alpha6.value
                                    6 -> alpha7.value
                                    7 -> alpha8.value
                                    else -> 1.0f
                                },
                        ),
                    topLeft = center + Offset(x = rectSize.width, y = 0f),
                    size = rectSize,
                    style =
                        Stroke(
                            width = rectSize.height * 2,
                            pathEffect = PathEffect.cornerPathEffect(rectSize.height),
                        ),
                )
            }
        }
    }
}

@Composable
private fun InfiniteTransition.fractionTransition(
    initialValue: Float,
    targetValue: Float,
    @IntRange(from = 1, to = 4) fraction: Int = 1,
    durationMillis: Int,
    delayMillis: Int = 0,
    offsetMillis: Int = 0,
    repeatMode: RepeatMode = RepeatMode.Restart,
    easing: Easing = FastOutSlowInEasing,
): State<Float> {
    return animateFloat(
        initialValue = initialValue,
        targetValue = targetValue,
        animationSpec =
            infiniteRepeatable(
                animation =
                    keyframes {
                        this.durationMillis = durationMillis
                        this.delayMillis = delayMillis
                        initialValue at 0 with easing
                        when (fraction) {
                            1 -> {
                                targetValue at durationMillis with easing
                            }

                            2 -> {
                                targetValue / fraction at durationMillis / fraction with easing
                                targetValue at durationMillis with easing
                            }

                            3 -> {
                                targetValue / fraction at
                                    durationMillis / fraction with easing
                                targetValue / fraction * 2 at
                                    durationMillis / fraction * 2 with easing
                                targetValue at durationMillis with easing
                            }

                            4 -> {
                                targetValue / fraction at durationMillis / fraction with easing
                                targetValue / fraction * 2 at
                                    durationMillis / fraction * 2 with easing
                                targetValue / fraction * 3 at
                                    durationMillis / fraction * 3 with easing
                                targetValue at durationMillis with easing
                            }
                        }
                    },
                repeatMode,
                StartOffset(offsetMillis),
            ),
    )
}

private val EaseInOut = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)

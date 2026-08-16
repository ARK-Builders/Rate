package dev.arkbuilders.rate.core.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Insets produced by the app-level Scaffold. Screens use these values instead of padding the
 * navigation host, allowing scrollable content and backgrounds to remain edge-to-edge.
 */
val LocalAppScaffoldPadding = staticCompositionLocalOf { PaddingValues(0.dp) }

@Composable
fun appScaffoldContentWindowInsets(): WindowInsets {
    val padding = LocalAppScaffoldPadding.current
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    return with(density) {
        WindowInsets(
            left = padding.calculateLeftPadding(layoutDirection).roundToPx(),
            top = padding.calculateTopPadding().roundToPx(),
            right = padding.calculateRightPadding(layoutDirection).roundToPx(),
            bottom = padding.calculateBottomPadding().roundToPx(),
        )
    }
}

fun PaddingValues.calculateStartPadding(layoutDirection: LayoutDirection): Dp =
    if (layoutDirection == LayoutDirection.Ltr) {
        calculateLeftPadding(layoutDirection)
    } else {
        calculateRightPadding(layoutDirection)
    }

fun PaddingValues.calculateEndPadding(layoutDirection: LayoutDirection): Dp =
    if (layoutDirection == LayoutDirection.Ltr) {
        calculateRightPadding(layoutDirection)
    } else {
        calculateLeftPadding(layoutDirection)
    }

package dev.arkbuilders.rate.watchapp.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

val LightWearColors = Colors(
    primary = ArkColor.Primary,
    primaryVariant = ArkColor.BrandSecondary,
    secondary = ArkColor.Secondary,
    secondaryVariant = ArkColor.Teal700,
    background = Color.White,
    surface = Color.White,
    error = ArkColor.UtilityError500,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = ArkColor.TextPrimary,
    onSurface = ArkColor.TextPrimary,
    onSurfaceVariant = ArkColor.TextSecondary,
    onError = Color.White
)

@Composable
fun ArkrateTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = LightWearColors,
        content = content
    )
}
package dev.arkbuilders.rate.watchapp.presentation.options

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun WearPageIndicator(
    totalPages: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            PageIndicatorDot(
                isSelected = index == currentPage,
                modifier = Modifier.size(6.dp)
            )
        }
    }
}

@Composable
private fun PageIndicatorDot(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        ArkColor.TextSecondary
    } else {
        ArkColor.TextSecondary.copy(alpha = 0.3f)
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
    )
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun WearPageIndicatorPreview() {
    WearPageIndicator(
        totalPages = 3,
        currentPage = 1
    )
}

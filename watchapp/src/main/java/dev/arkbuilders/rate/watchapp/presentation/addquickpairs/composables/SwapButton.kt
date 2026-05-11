package dev.arkbuilders.rate.watchapp.presentation.addquickpairs.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun SwapButton(
    onSwapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left divider
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(ArkColor.BorderSecondary)
        )

        // Swap button
        Button(
            onClick = onSwapClick,
            modifier = Modifier.size(40.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = ArkColor.TextPrimary
            ),
            border = ButtonDefaults.buttonBorder(
                borderStroke = BorderStroke(1.dp, ArkColor.BorderSecondary)
            ),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountBox,
                contentDescription = "Swap currencies",
                modifier = Modifier.size(20.dp),
                tint = ArkColor.TextPrimary
            )
        }

        // Right divider
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(ArkColor.BorderSecondary)
        )
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun SwapButtonPreview() {
    SwapButton(
        onSwapClick = {}
    )
}

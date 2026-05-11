package dev.arkbuilders.rate.watchapp.presentation.options

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

enum class WearOptionButtonType {
    Default,
    Success,
    Destructive
}

enum class WearOptionButtonIcon(val imageVector: ImageVector) {
    Refresh(Icons.Outlined.Refresh),
    Pin(Icons.Outlined.Star),
    Edit(Icons.Outlined.Edit),
    Reuse(Icons.Outlined.Share),
    Search(Icons.Outlined.Search),
    Delete(Icons.Outlined.Delete)
}

@Composable
fun WearOptionButton(
    text: String,
    icon: WearOptionButtonIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonType: WearOptionButtonType = WearOptionButtonType.Default
) {
    val colors = when (buttonType) {
        WearOptionButtonType.Default -> ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
            contentColor = ArkColor.TextSecondary
        )
        WearOptionButtonType.Success -> ButtonDefaults.buttonColors(
            backgroundColor = ArkColor.UtilitySuccess50,
            contentColor = ArkColor.Primary
        )
        WearOptionButtonType.Destructive -> ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
            contentColor = ArkColor.FGErrorPrimary
        )
    }

    val borderStroke = when (buttonType) {
        WearOptionButtonType.Default -> ButtonDefaults.buttonBorder(
            borderStroke = androidx.compose.foundation.BorderStroke(1.dp, ArkColor.BorderSecondary)
        )
        WearOptionButtonType.Success -> ButtonDefaults.buttonBorder(
            borderStroke = androidx.compose.foundation.BorderStroke(1.dp, ArkColor.UtilitySuccess200)
        )
        WearOptionButtonType.Destructive -> ButtonDefaults.buttonBorder(
            borderStroke = androidx.compose.foundation.BorderStroke(1.dp, ArkColor.BorderError)
        )
    }

    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = colors,
        border = borderStroke
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon.imageVector,
                contentDescription = text,
                modifier = Modifier.size(20.dp),
                tint = when (buttonType) {
                    WearOptionButtonType.Default -> ArkColor.TextSecondary
                    WearOptionButtonType.Success -> ArkColor.Primary
                    WearOptionButtonType.Destructive -> ArkColor.FGErrorPrimary
                }
            )
            Text(
                text = text,
                modifier = Modifier.padding(start = 6.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                color = when (buttonType) {
                    WearOptionButtonType.Default -> ArkColor.TextSecondary
                    WearOptionButtonType.Success -> ArkColor.TextSecondary
                    WearOptionButtonType.Destructive -> ArkColor.FGErrorPrimary
                }
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun WearOptionButtonPreview() {
    WearOptionButton(
        text = "Update",
        icon = WearOptionButtonIcon.Refresh,
        onClick = {}
    )
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun WearOptionButtonDestructivePreview() {
    WearOptionButton(
        text = "Delete",
        icon = WearOptionButtonIcon.Delete,
        buttonType = WearOptionButtonType.Destructive,
        onClick = {}
    )
}

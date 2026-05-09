package dev.arkbuilders.rate.watchapp.presentation.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.OutlinedButton
import androidx.wear.compose.material.Text
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

enum class WearButtonStyle {
    Primary,
    Secondary,
    Outlined,
    Destructive
}

@Composable
fun WearButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: WearButtonStyle = WearButtonStyle.Primary,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    when (style) {
        WearButtonStyle.Primary -> {
            Button(
                onClick = onClick,
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = ArkColor.Primary,
                    contentColor = Color.White
                ),
                enabled = enabled
            ) {
                ButtonContent(text = text, leadingIcon = leadingIcon)
            }
        }

        WearButtonStyle.Secondary -> {
            Button(
                onClick = onClick,
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = ArkColor.BGSecondaryAlt,
                    contentColor = ArkColor.TextSecondary
                ),
                enabled = enabled
            ) {
                ButtonContent(text = text, leadingIcon = leadingIcon)
            }
        }

        WearButtonStyle.Outlined -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ArkColor.TextSecondary
                ),
                border = ButtonDefaults.buttonBorder(
                    borderStroke = BorderStroke(1.dp, ArkColor.BorderSecondary)
                ),
                enabled = enabled
            ) {
                ButtonContent(text = text, leadingIcon = leadingIcon)
            }
        }

        WearButtonStyle.Destructive -> {
            Button(
                onClick = onClick,
                modifier = modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = ArkColor.UtilityError500,
                    contentColor = Color.White
                ),
                enabled = enabled
            ) {
                ButtonContent(text = text, leadingIcon = leadingIcon)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    leadingIcon: ImageVector?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (leadingIcon != null) Arrangement.Start else Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = text,
            modifier = Modifier.padding(
                start = if (leadingIcon != null) 8.dp else 0.dp
            ),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            textAlign = if (leadingIcon != null) TextAlign.Start else TextAlign.Center
        )
    }
}

@Composable
fun WearCompactButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: WearButtonStyle = WearButtonStyle.Outlined
) {
    val colors = when (style) {
        WearButtonStyle.Primary -> ButtonDefaults.buttonColors(
            backgroundColor = ArkColor.Primary,
            contentColor = Color.White
        )

        WearButtonStyle.Destructive -> ButtonDefaults.buttonColors(
            backgroundColor = ArkColor.UtilityError500,
            contentColor = Color.White
        )

        else -> ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
            contentColor = ArkColor.TextSecondary
        )
    }

    val border = if (style == WearButtonStyle.Outlined) {
        ButtonDefaults.buttonBorder(
            borderStroke = BorderStroke(1.dp, ArkColor.BorderSecondary)
        )
    } else {
        ButtonDefaults.buttonBorder()
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        colors = colors,
        border = border,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
fun WearFab(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ArkColor.Primary,
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(52.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = CircleShape
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

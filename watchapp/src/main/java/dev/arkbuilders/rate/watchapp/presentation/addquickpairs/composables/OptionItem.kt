package dev.arkbuilders.rate.watchapp.presentation.addquickpairs.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.OutlinedButton
import androidx.wear.compose.material.Text
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun OptionItem(
    modifier: Modifier = Modifier,
    icon: Painter,
    text: String,
    onClick: () -> Unit,
    isDeleteButton: Boolean = false,
) {
    OutlinedButton(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick,
        shape = RoundedCornerShape(20),
        border = ButtonDefaults.buttonBorder(
            borderStroke = BorderStroke(
                width = 1.dp,
                color = if (isDeleteButton) Color.Red else ArkColor.Border
            ),
        ),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = if (isDeleteButton) Color.Red else Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = "",
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}


@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun OptionItemPreview(modifier: Modifier = Modifier) {
    OptionItem(
        modifier = modifier.fillMaxWidth(),
        icon = painterResource(id = CoreRDrawable.ic_download),
        text = stringResource(id = CoreRString.edit),
        onClick = {},
    )
}

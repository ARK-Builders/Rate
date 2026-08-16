package dev.arkbuilders.rate.core.presentation.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Preview(showBackground = true)
@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    text: String = "",
    placeHolderText: String = stringResource(R.string.search),
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(44.dp)
                .border(
                    1.dp,
                    ArkColor.Border,
                    RoundedCornerShape(8.dp),
                )
                .clip(RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArkBasicTextField(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp),
            value = text,
            onValueChange = onValueChange,
            readOnly = readOnly,
            textStyle =
                TextStyle.Default.copy(
                    color = ArkColor.TextPrimary,
                    fontSize = 16.sp,
                ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    tint = ArkColor.FGQuarterary,
                )
            },
            placeholder = {
                Text(
                    text = placeHolderText,
                    color = ArkColor.TextPlaceHolder,
                    fontSize = 16.sp,
                )
            },
        )
    }
}

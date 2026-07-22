package dev.arkbuilders.rate.feature.portfolio.presentation.add

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.domain.model.AmountStr
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AmountInputVisualTransformation
import dev.arkbuilders.rate.core.presentation.ui.ArkBasicTextField

@Composable
fun InputCurrency(
    pos: Int,
    amount: AmountStr,
    onAssetValueChanged: (Int, String) -> Unit,
    onAssetRemove: (Int) -> Unit,
    onCodeChange: (Int) -> Unit,
) {
    Row(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .height(44.dp)
                    .border(
                        1.dp,
                        ArkColor.Border,
                        RoundedCornerShape(8.dp),
                    )
                    .clip(RoundedCornerShape(8.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCodeChange(pos) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(start = 14.dp),
                    text = amount.code,
                    fontSize = 16.sp,
                    color = ArkColor.TextSecondary,
                )
                Icon(
                    modifier = Modifier.padding(start = 9.dp, end = 5.dp),
                    painter = painterResource(CoreRDrawable.ic_chevron),
                    contentDescription = null,
                    tint = ArkColor.FGQuinary,
                )
            }
            ArkBasicTextField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                value = amount.value,
                onValueChange = { onAssetValueChanged(pos, it) },
                textStyle =
                    TextStyle.Default.copy(
                        color = ArkColor.TextPrimary,
                        fontSize = 16.sp,
                    ),
                keyboardOptions =
                    KeyboardOptions.Default
                        .copy(keyboardType = KeyboardType.Number),
                visualTransformation = AmountInputVisualTransformation,
                placeholder = {
                    Text(
                        text = stringResource(CoreRString.input_value),
                        color = ArkColor.TextPlaceHolder,
                        fontSize = 16.sp,
                    )
                },
            )
        }

        Box(
            modifier =
                Modifier
                    .padding(start = 16.dp)
                    .size(44.dp)
                    .border(
                        1.dp,
                        ArkColor.Border,
                        RoundedCornerShape(8.dp),
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onAssetRemove(pos) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = stringResource(CoreRString.delete),
                tint = ArkColor.FGSecondary,
            )
        }
    }
}

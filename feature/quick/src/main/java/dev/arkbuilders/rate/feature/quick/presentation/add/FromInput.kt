package dev.arkbuilders.rate.feature.quick.presentation.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AmountInputVisualTransformation
import dev.arkbuilders.rate.core.presentation.ui.ArkBasicTextField
import dev.arkbuilders.rate.core.presentation.utils.ReorderHapticFeedback
import dev.arkbuilders.rate.core.presentation.utils.ReorderHapticFeedbackType
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
fun FromInput(
    code: CurrencyCode,
    amount: String,
    haptic: ReorderHapticFeedback,
    scope: ReorderableCollectionItemScope,
    onAmountChanged: (String) -> Unit,
    onCodeChange: () -> Unit,
) {
    Row(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
        Box(
            modifier =
                with(scope) {
                    Modifier
                        .width(24.dp)
                        .height(44.dp)
                        .draggableHandle(
                            onDragStarted = {
                                haptic.performHapticFeedback(ReorderHapticFeedbackType.START)
                            },
                            onDragStopped = {
                                haptic.performHapticFeedback(ReorderHapticFeedbackType.END)
                            },
                        )
                        .clearAndSetSemantics { }
                },
        ) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                painter = painterResource(R.drawable.ic_drag),
                contentDescription = null,
                tint = ArkColor.NeutralGray500,
            )
        }

        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
                    .height(44.dp)
                    .border(
                        1.dp,
                        ArkColor.Border,
                        RoundedCornerShape(8.dp),
                    )
                    .background(Color.White, RoundedCornerShape(8.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCodeChange() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(start = 14.dp),
                    text = code,
                    fontSize = 16.sp,
                    color = ArkColor.TextSecondary,
                )
                Icon(
                    modifier = Modifier.padding(start = 9.dp, end = 5.dp),
                    painter = painterResource(R.drawable.ic_chevron),
                    contentDescription = null,
                    tint = ArkColor.FGQuinary,
                )
            }
            ArkBasicTextField(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                value = amount,
                onValueChange = { onAmountChanged(it) },
                keyboardOptions =
                    KeyboardOptions.Default
                        .copy(keyboardType = KeyboardType.Number),
                visualTransformation = AmountInputVisualTransformation,
                textStyle =
                    TextStyle.Default.copy(
                        color = ArkColor.TextPrimary,
                        fontSize = 16.sp,
                    ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.input_value),
                        color = ArkColor.TextPlaceHolder,
                        fontSize = 16.sp,
                    )
                },
            )
        }
    }
}

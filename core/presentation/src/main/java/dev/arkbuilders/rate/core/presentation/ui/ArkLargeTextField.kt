package dev.arkbuilders.rate.core.presentation.ui

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun ArkLargeTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    BasicTextField(
        modifier =
            modifier
                .width(IntrinsicSize.Min)
                .defaultMinSize(minWidth = 10.dp),
        value = value,
        onValueChange = onValueChange,
        textStyle =
            LocalTextStyle.current.copy(
                fontSize = 36.sp,
                color = ArkColor.TextPrimary,
                fontWeight = FontWeight.SemiBold,
            ),
        keyboardOptions =
            KeyboardOptions.Default
                .copy(keyboardType = KeyboardType.Number),
        visualTransformation = visualTransformation,
        singleLine = true,
    )
}

// Place cursor at end on focus
@Composable
fun ArkCursorLargeTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    var currentTextFieldValue =
        remember {
            mutableStateOf<TextFieldValue?>(null)
        }
    BasicTextField(
        modifier =
            modifier
                .width(IntrinsicSize.Min)
                .defaultMinSize(minWidth = 10.dp),
        value =
            TextFieldValue(
                text = value,
                selection = currentTextFieldValue.value?.selection ?: TextRange(value.length),
            ),
        onValueChange = {
            currentTextFieldValue.value = it
            onValueChange(it.text)
        },
        textStyle =
            LocalTextStyle.current.copy(
                fontSize = 36.sp,
                color = ArkColor.TextPrimary,
                fontWeight = FontWeight.SemiBold,
            ),
        keyboardOptions =
            KeyboardOptions.Default
                .copy(keyboardType = KeyboardType.Number),
        visualTransformation = visualTransformation,
        singleLine = true,
    )
}

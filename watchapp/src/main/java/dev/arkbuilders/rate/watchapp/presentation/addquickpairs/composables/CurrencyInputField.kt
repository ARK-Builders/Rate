package dev.arkbuilders.rate.watchapp.presentation.addquickpairs.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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

@Composable
fun CurrencyInputField(
    label: String,
    currencyCode: String,
    value: String,
    onValueChange: (String) -> Unit,
    onCurrencyClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDeleteButton: Boolean = false,
    onDeleteClick: () -> Unit = {},
    showLabel: Boolean = true,
    hintText: String = "This is a hint text to help user."
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // Main input field
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (showLabel) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = ArkColor.BorderSecondary,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Currency dropdown
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .clickable { onCurrencyClick() }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = currencyCode,
                                fontSize = 14.sp,
                                color = ArkColor.TextSecondary
                            )
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                contentDescription = "Select currency",
                                modifier = Modifier.size(16.dp),
                                tint = ArkColor.FGQuinary
                            )
                        }
                    }

                    // Value input
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = ArkColor.TextPrimary,
                            textAlign = TextAlign.Start
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }
            }

            if (showLabel) {
                Text(
                    text = hintText,
                    fontSize = 14.sp,
                    color = ArkColor.TextTertiary
                )
            }
        }

        // Delete button
        if (showDeleteButton) {
            Button(
                onClick = onDeleteClick,
                modifier = Modifier.size(44.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = ArkColor.FGErrorPrimary
                ),
                border = ButtonDefaults.buttonBorder(
                    borderStroke = BorderStroke(1.dp, ArkColor.BorderError)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(20.dp),
                    tint = ArkColor.FGErrorPrimary
                )
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun CurrencyInputFieldPreview() {
    var value by remember { mutableStateOf("1") }

    CurrencyInputField(
        label = "From",
        currencyCode = "USD",
        value = value,
        onValueChange = { value = it },
        onCurrencyClick = {},
        showDeleteButton = true,
        onDeleteClick = {}
    )
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun CurrencyInputFieldNoLabelPreview() {
    var value by remember { mutableStateOf("0.92") }

    CurrencyInputField(
        label = "To",
        currencyCode = "EUR",
        value = value,
        onValueChange = { value = it },
        onCurrencyClick = {},
        showLabel = false,
        showDeleteButton = true,
        onDeleteClick = {}
    )
}

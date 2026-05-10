package dev.arkbuilders.rate.watchapp.presentation.addquickpairs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.watchapp.presentation.quickpairs.composables.CurrIcon

@Composable
fun AddQuickPairsScreen(
    modifier: Modifier = Modifier,
    viewModel: AddQuickPairsViewModel = hiltViewModel(),
    navController: NavHostController,
    onNavigateToSearch: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSuccess: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val selectedCurrency = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("selected_currency", null)
        ?.collectAsStateWithLifecycle()

    val targetField = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("target_field", null)
        ?.collectAsStateWithLifecycle()

    LaunchedEffect(selectedCurrency?.value) {
        val code = selectedCurrency?.value ?: return@LaunchedEffect
        val field = targetField?.value ?: return@LaunchedEffect

        if (field == "from") {
            viewModel.onBaseCurrencyChanged(code)
        } else if (field == "to") {
            viewModel.onTargetCurrencyChanged(code)
        }

        // Clear the result after processing
        navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selected_currency")
        navController.currentBackStackEntry?.savedStateHandle?.remove<String>("target_field")
    }

    if (state.isSaved) {
        val message = if (state.editId != null) "Updated Successfully" else "Added Successfully"
        LaunchedEffect(Unit) {
            onNavigateToSuccess(message)
        }
    }

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                text = if (state.editId != null) "Update" else "Add",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = ArkColor.TextPrimary,
                fontSize = 16.sp
            )
        }

        item {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                text = "From",
                textAlign = TextAlign.Start,
                color = ArkColor.TextSecondary,
                fontSize = 12.sp
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ArkColor.UtilitySuccess50) // Light greenish background
                    .border(1.dp, ArkColor.UtilitySuccess200, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Currency Selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNavigateToSearch("from") }
                ) {
                    CurrIcon(modifier = Modifier.size(20.dp), code = state.baseCurrency)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = state.baseCurrency,
                        color = ArkColor.TextPrimary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select Currency",
                        tint = ArkColor.Primary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Amount
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (state.baseAmount.isEmpty()) {
                        Text(
                            text = "0",
                            color = ArkColor.TextPlaceHolder,
                            fontSize = 14.sp,
                            textAlign = TextAlign.End
                        )
                    }
                    BasicTextField(
                        value = state.baseAmount,
                        onValueChange = { viewModel.onAmountInput(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = TextStyle(
                            color = ArkColor.TextPrimary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.End
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, ArkColor.BorderSecondary, CircleShape)
                    .clickable { viewModel.onSwap() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Swap",
                    tint = ArkColor.Primary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        item {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                text = "To",
                textAlign = TextAlign.Start,
                color = ArkColor.TextSecondary,
                fontSize = 12.sp
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, ArkColor.BorderSecondary, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNavigateToSearch("to") }
                ) {
                    CurrIcon(modifier = Modifier.size(20.dp), code = state.targetCurrency)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = state.targetCurrency,
                        color = ArkColor.TextPrimary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select Currency",
                        tint = ArkColor.Primary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (state.targetAmount.isEmpty()) {
                        Text(
                            text = "0",
                            color = ArkColor.TextPlaceHolder,
                            fontSize = 14.sp,
                            textAlign = TextAlign.End
                        )
                    }
                    BasicTextField(
                        value = state.targetAmount,
                        onValueChange = { viewModel.onTargetAmountInput(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = TextStyle(
                            color = ArkColor.TextPrimary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.End
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Button(
                onClick = { viewModel.savePair() },
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 12.dp)
                    .fillMaxWidth(0.8f)
                    .height(36.dp),
                colors = ButtonDefaults.primaryButtonColors(backgroundColor = ArkColor.Primary)
            ) {
                Text(if (state.editId != null) "Update Pair" else "Save Pair", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.feature.quick.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.verticalScrollDisabled
import dev.arkbuilders.rate.feature.quick.domain.model.QuickCalculation

@Composable
fun QuickOptionsBottomSheet(
    sheetState: SheetState,
    calculation: QuickCalculation,
    onPin: (QuickCalculation) -> Unit,
    onUnpin: (QuickCalculation) -> Unit,
    onEdit: (QuickCalculation) -> Unit,
    onReuse: (QuickCalculation) -> Unit,
    onDelete: (QuickCalculation) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        modifier = Modifier,
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    ) {
        Content(
            calculation = calculation,
            onPin = onPin,
            onUnpin = onUnpin,
            onEdit = onEdit,
            onReuse = onReuse,
            onDelete = onDelete,
            onDismiss = onDismiss,
        )
    }
}

@Composable
private fun Content(
    calculation: QuickCalculation,
    onPin: (QuickCalculation) -> Unit,
    onUnpin: (QuickCalculation) -> Unit,
    onEdit: (QuickCalculation) -> Unit,
    onReuse: (QuickCalculation) -> Unit,
    onDelete: (QuickCalculation) -> Unit,
    onDismiss: () -> Unit,
) {
    Box(modifier = Modifier.verticalScrollDisabled()) {
        Text(
            modifier =
                Modifier
                    .padding(start = 16.dp, top = 24.dp)
                    .align(Alignment.TopStart),
            text = stringResource(CoreRString.options),
            color = ArkColor.TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
        )

        IconButton(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp),
            onClick = { onDismiss() },
        ) {
            Icon(
                modifier = Modifier,
                painter = painterResource(id = CoreRDrawable.ic_close),
                contentDescription = stringResource(CoreRString.close),
                tint = ArkColor.FGQuinary,
            )
        }
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 76.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDismiss()
                    if (calculation.isPinned())
                        onUnpin(calculation)
                    else
                        onPin(calculation)
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, ArkColor.Border),
            ) {
                Icon(
                    modifier = Modifier,
                    painter = painterResource(id = CoreRDrawable.ic_pin),
                    contentDescription = null,
                    tint = ArkColor.TextSecondary,
                )
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text =
                        if (calculation.isPinned())
                            stringResource(CoreRString.unpin)
                        else
                            stringResource(CoreRString.pin),
                    color = ArkColor.TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDismiss()
                    onEdit(calculation)
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, ArkColor.Border),
            ) {
                Icon(
                    modifier = Modifier,
                    painter = painterResource(id = CoreRDrawable.ic_edit),
                    contentDescription = null,
                    tint = ArkColor.TextSecondary,
                )
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text = stringResource(CoreRString.edit),
                    color = ArkColor.TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDismiss()
                    onReuse(calculation)
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, ArkColor.Border),
            ) {
                Icon(
                    modifier = Modifier,
                    painter = painterResource(id = CoreRDrawable.ic_reuse),
                    contentDescription = null,
                    tint = ArkColor.TextSecondary,
                )
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text = stringResource(CoreRString.re_use),
                    color = ArkColor.TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDismiss()
                    onDelete(calculation)
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, ArkColor.BorderError),
            ) {
                Icon(
                    modifier = Modifier,
                    painter = painterResource(id = CoreRDrawable.ic_delete),
                    contentDescription = null,
                    tint = ArkColor.TextError,
                )
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text = stringResource(CoreRString.delete),
                    color = ArkColor.TextError,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

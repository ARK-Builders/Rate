package dev.arkbuilders.rate.watchapp.presentation.addquickpairs.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun WearNumpad(
    modifier: Modifier = Modifier,
    amount: String,
    onNumberClick: (Int) -> Unit,
    onDotClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display
        Text(
            text = amount.ifEmpty { "0" },
            fontWeight = FontWeight.Bold,
            color = ArkColor.TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Numpad 1-3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NumpadButton("1") { onNumberClick(1) }
            NumpadButton("2") { onNumberClick(2) }
            NumpadButton("3") { onNumberClick(3) }
        }

        // Numpad 4-6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NumpadButton("4") { onNumberClick(4) }
            NumpadButton("5") { onNumberClick(5) }
            NumpadButton("6") { onNumberClick(6) }
        }

        // Numpad 7-9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NumpadButton("7") { onNumberClick(7) }
            NumpadButton("8") { onNumberClick(8) }
            NumpadButton("9") { onNumberClick(9) }
        }

        // Bottom row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NumpadButton(".") { onDotClick() }
            NumpadButton("0") { onNumberClick(0) }
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ArkColor.BGTertiary)
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Delete", tint = ArkColor.TextPrimary)
            }
        }
        
        // Confirm Button
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(ArkColor.Primary)
                .clickable { onConfirmClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = "Confirm", tint = Color.White)
        }
    }
}

@Composable
fun NumpadButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(2.dp)
            .clip(CircleShape)
            .background(ArkColor.BGSecondaryAlt)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold, color = ArkColor.TextPrimary)
    }
}

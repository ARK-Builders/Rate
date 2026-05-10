package dev.arkbuilders.rate.watchapp.presentation.options

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import kotlinx.coroutines.delay

@Composable
fun SuccessScreen(
    modifier: Modifier = Modifier,
    message: String = "Success",
    onTimeout: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(1500L)
        onTimeout()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = ArkColor.BrandSecondary,
            modifier = Modifier
                .size(48.dp)
                .padding(bottom = 8.dp)
        )
        Text(
            text = message,
            color = ArkColor.TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

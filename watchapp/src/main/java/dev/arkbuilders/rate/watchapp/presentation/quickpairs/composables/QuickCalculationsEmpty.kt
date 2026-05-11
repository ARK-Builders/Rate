package dev.arkbuilders.rate.watchapp.presentation.quickpairs.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
@Composable
fun QuickCalculationsEmpty(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    ScalingLazyColumn(
        modifier = modifier.fillMaxSize().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 24.dp)
    ) {
        item {
            Icon(
                painter = painterResource(id = CoreRDrawable.ic_empty_quick),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
        item {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(CoreRString.quick_empty_title),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = ArkColor.TextPrimary,
                textAlign = TextAlign.Center,
            )
        }
        item {
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(CoreRString.quick_empty_desc),
                fontSize = 12.sp,
                color = ArkColor.TextTertiary,
                textAlign = TextAlign.Center,
            )
        }
        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun QuickCalculationEmptyPreview() {
    QuickCalculationsEmpty(onAddClick = {})
}

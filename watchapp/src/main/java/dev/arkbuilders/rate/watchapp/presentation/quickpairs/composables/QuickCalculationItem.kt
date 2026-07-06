package dev.arkbuilders.rate.watchapp.presentation.quickpairs.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.utils.IconUtils
import dev.arkbuilders.rate.feature.quick.domain.model.QuickCalculation
import dev.arkbuilders.rate.feature.quick.presentation.ui.QuickDateFormatter
import java.math.BigDecimal
import java.time.OffsetDateTime

@Composable
fun QuickCalculationItem(
    modifier: Modifier = Modifier,
    quick: QuickCalculation,
    onClick: () -> Unit,
) {
    val ctx = LocalContext.current
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .background(Color.White),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CurrIcon(modifier = Modifier.size(24.dp), code = quick.from)
                val target = quick.to.firstOrNull()
                if (target != null) {
                    CurrIcon(
                        modifier =
                            Modifier
                                .size(24.dp)
                                .offset(x = (-8).dp)
                                .border(1.dp, Color.White, CircleShape),
                        code = target.code,
                    )
                }
            }
            val timeText =
                if (quick.isPinned()) {
                    QuickDateFormatter.calculationRefreshedTime(ctx, quick.calculatedDate)
                } else {
                    QuickDateFormatter.calculationCalculatedTime(ctx, quick.calculatedDate)
                }
            Text(
                text = timeText,
                color = ArkColor.TextTertiary,
                fontSize = 10.sp,
            )
        }

        // Title Row: "EUR to USD"
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val targetCode = quick.to.firstOrNull()?.code ?: ""
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${quick.from} to $targetCode",
                    color = ArkColor.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                if (quick.isPinned()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Star,
                        contentDescription = "Pinned",
                        tint = ArkColor.Primary,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }
        }

        // Subtitle: Conversion Rate
        val target = quick.to.firstOrNull()
        if (target != null) {
            val baseAmountStr = CurrUtils.prepareToDisplay(quick.amount)
            val targetAmountStr = CurrUtils.prepareToDisplay(target.value)
            Text(
                text = "$baseAmountStr ${quick.from} = $targetAmountStr ${target.code}",
                color = ArkColor.TextTertiary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
fun CurrIcon(
    modifier: Modifier = Modifier,
    code: CurrencyCode,
) {
    val ctx = LocalContext.current
    Icon(
        modifier = modifier,
        painter = painterResource(id = IconUtils.iconForCurrCode(ctx, code)),
        contentDescription = code,
        tint = Color.Unspecified,
    )
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun QuickCalculationItemPreview() {
    QuickCalculationItem(
        quick =
            QuickCalculation(
                id = 1,
                from = "BTC",
                amount = BigDecimal.valueOf(1.2),
                to = listOf(Amount("USD", BigDecimal.valueOf(12.0))),
                calculatedDate = OffsetDateTime.now(),
                pinnedDate = null,
                group = Group.empty(),
            ),
        onClick = {},
    )
}

package dev.arkbuilders.rate.watchapp.presentation.quickpairs.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.utils.IconUtils
import dev.arkbuilders.rate.feature.quick.domain.model.QuickPair
import java.math.BigDecimal
import java.time.OffsetDateTime

@Composable
fun QuickPairItem(
    modifier: Modifier = Modifier,
    quick: QuickPair,
    onClick: () -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .background(Color.White)
    ) {
        // Top row: Flags and "2 mins ago"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CurrIcon(modifier = Modifier.size(24.dp), code = quick.from)
                if (quick.to.size > 1) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .offset(x = (-8).dp)
                            .border(1.dp, Color.White, CircleShape)
                            .background(ArkColor.BGTertiary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+${quick.to.size}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = ArkColor.TextTertiary,
                        )
                    }
                } else if (quick.to.isNotEmpty()) {
                    CurrIcon(
                        modifier = Modifier
                            .size(24.dp)
                            .offset(x = (-8).dp)
                            .border(1.dp, Color.White, CircleShape),
                        code = quick.to.first().code
                    )
                }
            }
            Text(
                text = "2 mins ago",
                color = ArkColor.TextTertiary,
                fontSize = 10.sp
            )
        }

        // Title Row: "EUR to USD" and Chevron
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val titleText = if (quick.to.size > 1) {
                "${quick.from} to ${quick.to.first().code}, and ${quick.to.size - 1}+"
            } else {
                "${quick.from} to ${quick.to.firstOrNull()?.code ?: ""}"
            }
            Text(
                text = titleText,
                color = ArkColor.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            if (quick.to.size > 1) {
                Icon(
                    painter = painterResource(if (isExpanded) CoreRDrawable.ic_chevron_up else CoreRDrawable.ic_chevron),
                    contentDescription = "Expand",
                    tint = ArkColor.FGSecondary,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { isExpanded = !isExpanded }
                )
            }
        }

        // Subtitle: Base amount
        val baseAmountText = if (quick.to.size == 1) {
            "${CurrUtils.prepareToDisplay(quick.amount)} ${quick.from} = ${CurrUtils.prepareToDisplay(quick.to.first().value)} ${quick.to.first().code}"
        } else {
            "${CurrUtils.prepareToDisplay(quick.amount)} ${quick.from} = ${CurrUtils.prepareToDisplay(quick.to.first().value)} ${quick.to.first().code}"
        }
        Text(
            text = baseAmountText,
            color = ArkColor.TextTertiary,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 2.dp)
        )

        // Expanded view
        if (isExpanded && quick.to.size > 1) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                quick.to.forEach { target ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CurrIcon(modifier = Modifier.size(16.dp), code = target.code)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${CurrUtils.prepareToDisplay(target.value)} ${target.code}",
                            color = ArkColor.TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
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
fun QuickPairItemPreview() {
    QuickPairItem(
        quick = QuickPair(
            id = 1,
            from = "BTC",
            amount = BigDecimal.valueOf(1.2),
            to = listOf(
                Amount("USD", BigDecimal.valueOf(12.0)),
                Amount("EUR", BigDecimal.valueOf(12.0))
            ),
            calculatedDate = OffsetDateTime.now(),
            pinnedDate = null,
            group = Group.empty()
        ),
        onClick = {}
    )
}


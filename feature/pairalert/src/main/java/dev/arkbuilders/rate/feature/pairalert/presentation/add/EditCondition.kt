package dev.arkbuilders.rate.feature.pairalert.presentation.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.ArkLargeTextField
import dev.arkbuilders.rate.core.presentation.ui.DropDownBtn

@Composable
fun EditCondition(
    state: AddPairAlertScreenState,
    navigateSearchBase: () -> Unit,
    navigateSearchTarget: () -> Unit,
    onPriceOrPercentInputChanged: (String) -> Unit,
    onIncreaseToggle: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(CoreRString.when_),
                color = ArkColor.TextTertiary,
            )
            DropDownBtn(
                modifier = Modifier.padding(start = 8.dp),
                title = state.targetCode,
            ) {
                navigateSearchTarget()
            }
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(CoreRString.price_is),
                color = ArkColor.TextTertiary,
            )
            Row(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .run {
                            if (state.oneTimeNotRecurrent && state.priceOrPercent.isLeft())
                                this
                            else
                                clickable { onIncreaseToggle() }
                        },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.padding(start = 8.dp),
                    painter =
                        painterResource(
                            id =
                                if (state.aboveNotBelow)
                                    CoreRDrawable.ic_pair_alert_inc
                                else
                                    CoreRDrawable.ic_pair_alert_dec,
                        ),
                    contentDescription = null,
                    tint =
                        if (state.aboveNotBelow)
                            ArkColor.PairAlertInc
                        else
                            ArkColor.PairAlertDec,
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text =
                        if (state.aboveNotBelow)
                            stringResource(CoreRString.above)
                        else
                            stringResource(CoreRString.below),
                    color =
                        if (state.aboveNotBelow)
                            ArkColor.PairAlertInc
                        else
                            ArkColor.PairAlertDec,
                )
            }
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            if (!state.oneTimeNotRecurrent) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(CoreRString.every),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary,
                )
            }
            ArkLargeTextField(
                modifier =
                    Modifier
                        .weight(1f, fill = false)
                        .align(Alignment.CenterVertically),
                value =
                    state.priceOrPercent.fold(
                        ifLeft = { it },
                        ifRight = { it },
                    ),
                onValueChange = { onPriceOrPercentInputChanged(it) },
            )
            if (state.priceOrPercent.isLeft()) {
                Text(
                    modifier = Modifier.align(Alignment.Top),
                    text = CurrUtils.getSymbolOrCode(state.baseCode),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = ArkColor.TextPrimary,
                )
            }
            if (state.priceOrPercent.isRight()) {
                Text(
                    modifier = Modifier.align(Alignment.Top),
                    text = "%",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ArkColor.TextPrimary,
                )
            }
        }
        Row(
            modifier =
                Modifier
                    .padding(top = 24.dp)
                    .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text =
                    stringResource(
                        CoreRString.alert_current_price,
                        CurrUtils.prepareToDisplay(state.currentPrice),
                    ),
                color = ArkColor.TextTertiary,
            )
            DropDownBtn(
                modifier = Modifier.padding(start = 16.dp),
                title = state.baseCode,
            ) {
                navigateSearchBase()
            }
        }
    }
}

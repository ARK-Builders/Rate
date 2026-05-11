@file:OptIn(ExperimentalFoundationApi::class)

package dev.arkbuilders.rate.feature.quick.presentation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.CurrIcon

@Composable
fun QuickItem(
    from: Amount,
    to: List<Amount>,
    dateText: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    ConstraintLayout(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .combinedClickable(
                    onClick = { onClick() },
                    onLongClick = { onLongClick() },
                ),
    ) {
        val (icons, content, chevron) = createRefs()
        Row(
            modifier =
                Modifier.constrainAs(icons) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start, margin = 24.dp)
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp),
            ) {
                CurrIcon(modifier = Modifier.size(40.dp), code = from.code)
            }
            if (!expanded) {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .offset((-12).dp)
                            .border(2.dp, Color.White, CircleShape),
                ) {
                    if (to.size == 1) {
                        CurrIcon(
                            modifier =
                                Modifier
                                    .size(38.dp)
                                    .align(Alignment.Center)
                                    .clip(CircleShape)
                                    .background(Color.White),
                            code = to.first().code,
                        )
                    } else {
                        Box(
                            modifier =
                                Modifier
                                    .size(40.dp)
                                    .background(ArkColor.BGTertiary, CircleShape),
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = "+ ${to.size}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = ArkColor.TextTertiary,
                            )
                        }
                    }
                }
            }
        }
        Column(
            modifier =
                Modifier
                    .constrainAs(content) {
                        start.linkTo(icons.end)
                        if (to.size > 1)
                            end.linkTo(chevron.start)
                        else
                            end.linkTo(parent.end, margin = 24.dp)
                        top.linkTo(parent.top, margin = 16.dp)
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = if (expanded) 12.dp else 0.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text =
                    "${from.code} to " +
                        to.joinToString(", ") { it.code },
                fontWeight = FontWeight.Medium,
                color = ArkColor.TextPrimary,
            )
            if (expanded) {
                Text(
                    text = "${CurrUtils.prepareToDisplay(from.value)} ${from.code} =",
                    color = ArkColor.TextTertiary,
                )
                to.forEach {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CurrIcon(modifier = Modifier.size(20.dp), code = it.code)
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "${CurrUtils.prepareToDisplay(it.value)} ${it.code}",
                            color = ArkColor.TextTertiary,
                        )
                    }
                }
            } else {
                Text(
                    text =
                        "${CurrUtils.prepareToDisplay(from.value)} ${from.code} = " +
                            "${CurrUtils.prepareToDisplay(to.first().value)} ${to.first().code}",
                    color = ArkColor.TextTertiary,
                )
            }
            Text(
                modifier = Modifier.padding(top = if (expanded) 8.dp else 0.dp),
                text = dateText,
                color = ArkColor.TextTertiary,
                fontSize = 12.sp,
            )
        }
        if (to.size > 1) {
            Box(
                modifier =
                    Modifier
                        .constrainAs(chevron) {
                            height = Dimension.fillToConstraints
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                        .clickable {
                            expanded = !expanded
                        }
                        .padding(start = 13.dp, end = 29.dp, top = 23.dp),
            ) {
                if (expanded) {
                    Icon(
                        modifier = Modifier,
                        painter = painterResource(CoreRDrawable.ic_chevron_up),
                        contentDescription = stringResource(CoreRString.collapse),
                        tint = ArkColor.FGSecondary,
                    )
                } else {
                    Icon(
                        modifier = Modifier,
                        painter = painterResource(CoreRDrawable.ic_chevron),
                        contentDescription = stringResource(CoreRString.expand),
                        tint = ArkColor.FGSecondary,
                    )
                }
            }
        }
    }
}

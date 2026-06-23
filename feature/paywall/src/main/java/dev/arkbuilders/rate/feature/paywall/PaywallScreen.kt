package dev.arkbuilders.rate.feature.paywall

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Destination<ExternalModuleGraph>
@Composable
fun PaywallScreen() {
    val viewModel: PaywallViewModel = viewModel()

    PaywallContent(
        onCloseClick = viewModel::onCloseClick,
        onRestoreClick = viewModel::onRestoreClick,
        onYearlyClick = viewModel::onYearlyPlanClick,
        onMonthlyClick = viewModel::onMonthlyPlanClick,
        onPrimaryClick = viewModel::onPrimaryClick,
        onContinueFreeClick = viewModel::onContinueFreeClick,
        onTermsClick = viewModel::onTermsClick,
        onPrivacyClick = viewModel::onPrivacyClick,
        onRestorePurchasesClick = viewModel::onRestorePurchasesClick,
    )
}

@Composable
private fun PaywallContent(
    onCloseClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onYearlyClick: () -> Unit,
    onMonthlyClick: () -> Unit,
    onPrimaryClick: () -> Unit,
    onContinueFreeClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onRestorePurchasesClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors =
                            listOf(
                                Color.White,
                                ArkColor.BGSecondaryAlt,
                                ArkColor.BrandSecondary.copy(alpha = 0.08f),
                            ),
                    ),
                )
                .statusBarsPadding()
                .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .widthIn(max = 420.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PaywallTopBar(
                onCloseClick = onCloseClick,
                onRestoreClick = onRestoreClick,
            )
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(16.dp))
                Illustration()
                Spacer(Modifier.height(22.dp))
                Text(
                    text = "Unlock live updates",
                    color = ArkColor.TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
                    text = "Get live exchange rates updated every 5-10 minutes and more with Premium.",
                    color = ArkColor.TextTertiary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(28.dp))
                BenefitsCard()
                Spacer(Modifier.height(26.dp))
                SectionDivider("CHOOSE YOUR PLAN")
                Spacer(Modifier.height(16.dp))
                PlanCard(
                    title = "Yearly",
                    price = "\$19.99 / year",
                    description = "7 days free, then \$19.99 / year",
                    monthlyPrice = "\$1.67 / month",
                    badge = "Best value",
                    selected = true,
                    onClick = onYearlyClick,
                )
                Spacer(Modifier.height(12.dp))
                PlanCard(
                    title = "Monthly",
                    price = "\$2.99 / month",
                    description = "7 days free, then \$2.99 / month",
                    monthlyPrice = null,
                    badge = null,
                    selected = false,
                    onClick = onMonthlyClick,
                )
                Spacer(Modifier.height(28.dp))
                PrimaryButton(onClick = onPrimaryClick)
                Spacer(Modifier.height(24.dp))
                TrustRow()
                Spacer(Modifier.height(28.dp))
                SecondaryButton(onClick = onContinueFreeClick)
                Spacer(Modifier.height(30.dp))
                Footer(
                    onTermsClick = onTermsClick,
                    onPrivacyClick = onPrivacyClick,
                    onRestorePurchasesClick = onRestorePurchasesClick,
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PaywallTopBar(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit,
    onRestoreClick: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(
                        role = Role.Button,
                        onClick = onCloseClick,
                    )
                    .padding(13.dp),
            painter = painterResource(CoreRDrawable.ic_close),
            contentDescription = null,
            tint = ArkColor.TextPrimary,
        )
        Spacer(Modifier.weight(1f))
        Text(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        role = Role.Button,
                        onClick = onRestoreClick,
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            text = "Restore",
            color = ArkColor.TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun Illustration() {
    Box(
        modifier =
            Modifier
                .size(width = 144.dp, height = 112.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ArkColor.BGTertiary),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Icon / Illustration",
            color = ArkColor.TextQuarterary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun BenefitsCard() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, ArkColor.BorderSecondary, RoundedCornerShape(12.dp)),
    ) {
        BenefitRow(
            iconType = BenefitIconType.Lightning,
            title = "Live rate updates",
            description = "Rates refresh every 5-10 minutes",
        )
        BenefitDivider()
        BenefitRow(
            iconType = BenefitIconType.Coins,
            title = "500+ crypto tokens",
            description = "Access 500+ cryptocurrencies",
        )
        BenefitDivider()
        BenefitRow(
            iconType = BenefitIconType.Pin,
            title = "Unlimited pinned calculations",
            description = "Pin and organize as many as you want",
        )
    }
}

@Composable
private fun BenefitRow(
    iconType: BenefitIconType,
    title: String,
    description: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BenefitIcon(iconType)
        Column(
            modifier =
                Modifier
                    .padding(start = 14.dp)
                    .weight(1f),
        ) {
            Text(
                text = title,
                color = ArkColor.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                modifier = Modifier.padding(top = 3.dp),
                text = description,
                color = ArkColor.TextTertiary,
                fontSize = 12.sp,
            )
        }
        CheckMark()
    }
}

@Composable
private fun BenefitIcon(iconType: BenefitIconType) {
    Box(
        modifier =
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(ArkColor.BGTertiary),
        contentAlignment = Alignment.Center,
    ) {
        when (iconType) {
            BenefitIconType.Pin ->
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(CoreRDrawable.ic_pin),
                    contentDescription = null,
                    tint = ArkColor.TextPrimary,
                )

            else ->
                Canvas(modifier = Modifier.size(22.dp)) {
                    val stroke =
                        Stroke(
                            width = 2.1.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    when (iconType) {
                        BenefitIconType.Lightning -> {
                            val path =
                                Path().apply {
                                    moveTo(size.width * 0.58f, 0f)
                                    lineTo(size.width * 0.24f, size.height * 0.54f)
                                    lineTo(size.width * 0.51f, size.height * 0.54f)
                                    lineTo(size.width * 0.38f, size.height)
                                    lineTo(size.width * 0.76f, size.height * 0.42f)
                                    lineTo(size.width * 0.49f, size.height * 0.42f)
                                    close()
                                }
                            drawPath(path, ArkColor.TextPrimary)
                        }

                        BenefitIconType.Coins -> {
                            drawOval(
                                color = ArkColor.TextPrimary,
                                style = stroke,
                            )
                            drawLine(
                                color = ArkColor.TextPrimary,
                                start = Offset(0f, size.height * 0.36f),
                                end = Offset(size.width, size.height * 0.36f),
                                strokeWidth = 2.1.dp.toPx(),
                                cap = StrokeCap.Round,
                            )
                            drawLine(
                                color = ArkColor.TextPrimary,
                                start = Offset(0f, size.height * 0.64f),
                                end = Offset(size.width, size.height * 0.64f),
                                strokeWidth = 2.1.dp.toPx(),
                                cap = StrokeCap.Round,
                            )
                        }

                        BenefitIconType.Pin -> Unit
                    }
                }
            }
    }
}

@Composable
private fun BenefitDivider() {
    Spacer(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ArkColor.BorderSecondary),
    )
}

@Composable
private fun CheckMark() {
    Canvas(modifier = Modifier.size(22.dp)) {
        drawLine(
            color = ArkColor.Primary,
            start = Offset(size.width * 0.22f, size.height * 0.52f),
            end = Offset(size.width * 0.42f, size.height * 0.72f),
            strokeWidth = 2.2.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = ArkColor.Primary,
            start = Offset(size.width * 0.42f, size.height * 0.72f),
            end = Offset(size.width * 0.8f, size.height * 0.28f),
            strokeWidth = 2.2.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun SectionDivider(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(
            modifier =
                Modifier
                    .height(1.dp)
                    .weight(1f)
                    .background(ArkColor.BorderSecondary),
        )
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = title,
            color = ArkColor.TextQuarterary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(
            modifier =
                Modifier
                    .height(1.dp)
                    .weight(1f)
                    .background(ArkColor.BorderSecondary),
        )
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    description: String,
    monthlyPrice: String?,
    badge: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) ArkColor.Primary else ArkColor.BorderSecondary
    val background =
        if (selected) {
            Brush.linearGradient(
                colors =
                    listOf(
                        Color.White,
                        ArkColor.Primary.copy(alpha = 0.06f),
                    ),
            )
        } else {
            Brush.linearGradient(listOf(Color.White, Color.White))
        }
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(background)
                .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                .clickable(
                    role = Role.RadioButton,
                    onClick = onClick,
                )
                .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioIndicator(selected = selected)
        Column(
            modifier =
                Modifier
                    .padding(start = 14.dp)
                    .weight(1f),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = ArkColor.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
                badge?.let {
                    Text(
                        modifier =
                            Modifier
                                .padding(start = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ArkColor.Primary.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        text = it,
                        color = ArkColor.TextBrandSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                    )
                }
            }
            Text(
                modifier = Modifier.padding(top = 6.dp, end = 8.dp),
                text = description,
                color = ArkColor.TextTertiary,
                fontSize = 12.sp,
                maxLines = 1,
            )
        }
        Column(
            modifier = Modifier.padding(start = 8.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = price,
                color = ArkColor.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                maxLines = 1,
            )
            monthlyPrice?.let {
                Text(
                    modifier = Modifier.padding(top = 6.dp),
                    text = it,
                    color = ArkColor.TextTertiary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun RadioIndicator(selected: Boolean) {
    Box(
        modifier =
            Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(
                    width = 1.5.dp,
                    color = if (selected) ArkColor.Primary else ArkColor.Border,
                    shape = CircleShape,
                ),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                modifier =
                    Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(ArkColor.Primary),
            )
        }
    }
}

@Composable
private fun PrimaryButton(onClick: () -> Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.horizontalGradient(
                        colors =
                            listOf(
                                ArkColor.Primary,
                                ArkColor.BrandUtility,
                            ),
                    ),
                )
                .clickable(
                    role = Role.Button,
                    onClick = onClick,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Try Premium Free",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = "7 days free, cancel anytime",
            color = Color.White.copy(alpha = 0.82f),
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun SecondaryButton(onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Color.White)
                .border(1.dp, ArkColor.Border, RoundedCornerShape(9.dp))
                .clickable(
                    role = Role.Button,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Continue using free version",
            color = ArkColor.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun TrustRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TrustItem(
            modifier = Modifier.weight(1f),
            icon = CoreRDrawable.ic_snackbar_done,
            title = "Secure payment",
            subtitle = "via App Store",
        )
        TrustItem(
            modifier = Modifier.weight(1f),
            icon = CoreRDrawable.ic_refresh,
            title = "Cancel anytime",
            subtitle = "in Settings",
        )
        TrustItem(
            modifier = Modifier.weight(1f),
            icon = CoreRDrawable.ic_info,
            title = "Your data is safe",
            subtitle = "and private",
        )
    }
}

@Composable
private fun TrustItem(
    modifier: Modifier = Modifier,
    icon: Int,
    title: String,
    subtitle: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(icon),
            contentDescription = null,
            tint = ArkColor.TextQuarterary,
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = title,
            color = ArkColor.TextSecondary,
            fontSize = 10.sp,
            lineHeight = 13.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = subtitle,
            color = ArkColor.TextQuarterary,
            fontSize = 10.sp,
            lineHeight = 13.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun Footer(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onRestorePurchasesClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FooterLink("Terms of Use", onTermsClick)
        FooterDot()
        FooterLink("Privacy Policy", onPrivacyClick)
        FooterDot()
        FooterLink("Restore Purchases", onRestorePurchasesClick)
    }
}

@Composable
private fun FooterLink(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        modifier =
            Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable(
                    role = Role.Button,
                    onClick = onClick,
                )
                .padding(horizontal = 6.dp, vertical = 4.dp),
        text = text,
        color = ArkColor.TextQuarterary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun FooterDot() {
    Box(
        modifier =
            Modifier
                .size(3.dp)
                .clip(CircleShape)
                .background(ArkColor.TextQuarterary.copy(alpha = 0.8f)),
    )
}

private enum class BenefitIconType {
    Lightning,
    Coins,
    Pin,
}

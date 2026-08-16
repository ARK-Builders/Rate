package dev.arkbuilders.rate.feature.onboarding.pages

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.generated.onboarding.destinations.OnboardingQuickScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppButton
import dev.arkbuilders.rate.core.presentation.utils.findActivity
import kotlinx.coroutines.launch

@Composable
@Destination<ExternalModuleGraph>
fun OnboardingScreen(navigator: DestinationsNavigator) {
    val items = remember { PageItem.items() }
    val pageState =
        rememberPagerState {
            items.size
        }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    BackHandler {
        if (pageState.currentPage + 1 > 1) {
            scope.launch {
                pageState.animateScrollToPage(
                    page = pageState.currentPage - 1,
                    animationSpec = tween(durationMillis = 300),
                )
            }
        } else {
            ctx.findActivity()?.finish()
        }
    }

    Column(
        modifier =
            Modifier.windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
                ),
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state = pageState,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) { page ->
            Item(pageItem = items[page])
        }
        Indicators(size = items.size, index = pageState.currentPage)
        Spacer(Modifier.height(36.dp))
        AppButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
            onClick = {
                if (pageState.currentPage + 1 < items.size) {
                    scope.launch {
                        pageState.animateScrollToPage(
                            page = pageState.currentPage + 1,
                            animationSpec = tween(durationMillis = 300),
                        )
                    }
                } else {
                    navigator.navigate(OnboardingQuickScreenDestination)
                }
            },
        ) {
            Text(
                text = stringResource(items[pageState.currentPage].button),
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                color = Color.White,
            )
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun Item(pageItem: PageItem) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(0.45f),
            painter = painterResource(pageItem.image),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
        )
        Box(
            modifier =
                Modifier
                    .weight(0.55f)
                    .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column {
                Text(
                    text = stringResource(pageItem.title),
                    fontWeight = FontWeight.W600,
                    fontSize = 20.sp,
                    color = ArkColor.TextPrimary,
                    textAlign = TextAlign.Center,
                )
                pageItem.desc?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(pageItem.desc),
                        fontWeight = FontWeight.W400,
                        fontSize = 14.sp,
                        color = ArkColor.TextTertiary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
fun Indicators(
    size: Int,
    index: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(size) {
            Indicator(isSelected = it == index)
        }
    }
}

@Composable
fun Indicator(isSelected: Boolean) {
    val width =
        animateDpAsState(
            targetValue = if (isSelected) 24.dp else 8.dp,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        )

    Box(
        modifier =
            Modifier
                .height(8.dp)
                .width(width.value)
                .clip(CircleShape)
                .background(
                    color = if (isSelected) ArkColor.BrandSecondary else Color(0XFFF4EBFF),
                ),
    ) {
    }
}

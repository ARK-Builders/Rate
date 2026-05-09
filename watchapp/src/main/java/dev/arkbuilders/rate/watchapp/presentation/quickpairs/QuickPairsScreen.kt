package dev.arkbuilders.rate.watchapp.presentation.quickpairs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Text
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.watchapp.presentation.quickpairs.composables.QuickPairItem
import dev.arkbuilders.rate.watchapp.presentation.quickpairs.composables.QuickPairsEmpty
import dev.arkbuilders.rate.watchapp.presentation.theme.WearFab

@Composable
fun QuickPairsScreen(
    modifier: Modifier = Modifier,
    viewModel: QuickPairsViewModel = hiltViewModel(),
    onNavigateToAdd: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToOptions: (Long) -> Unit
) {
    val quickPairsList = viewModel.quickPairs.collectAsStateWithLifecycle().value
    val listState = rememberScalingLazyListState()

    var isFabVisible by remember { mutableStateOf(true) }
    var lastScrollIndex by remember { mutableIntStateOf(0) }
    var lastScrollOffset by remember { mutableIntStateOf(0) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.centerItemIndex to listState.centerItemScrollOffset }
            .collect { (index, offset) ->
                if (index > lastScrollIndex || (index == lastScrollIndex && offset > lastScrollOffset)) {
                    isFabVisible = false
                } else if (index < lastScrollIndex || (index == lastScrollIndex && offset < lastScrollOffset)) {
                    isFabVisible = true
                }
                lastScrollIndex = index
                lastScrollOffset = offset
            }
    }

    val fabProgress by animateFloatAsState(
        targetValue = if (isFabVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "fabProgress"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            WearFab(
                onClick = { if (isFabVisible) onNavigateToAdd() },
                icon = Icons.Outlined.Add,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .graphicsLayer {
                        alpha = fabProgress
                        scaleX = fabProgress
                        scaleY = fabProgress
                    }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            if (quickPairsList.isEmpty()) {
                QuickPairsEmpty(
                    modifier = Modifier.fillMaxSize(),
                    onAddClick = onNavigateToAdd
                )
            } else {
                ScalingLazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(
                        top = 4.dp,
                        start = 4.dp,
                        end = 4.dp,
                        bottom = 40.dp
                    )
                ) {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            text = "Quick",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = ArkColor.TextPrimary
                        )
                    }
                    items(quickPairsList.size, key = { quickPairsList[it].id }) { idx ->
                        QuickPairItem(
                            quick = quickPairsList[idx],
                            onClick = { onNavigateToSearch() }
                        )
                    }
                }
            }
        }
    }
}

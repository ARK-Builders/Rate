package dev.arkbuilders.rate.watchapp.presentation.quickpairs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import dev.arkbuilders.rate.watchapp.presentation.quickpairs.composables.QuickCalculationItem
import dev.arkbuilders.rate.watchapp.presentation.quickpairs.composables.QuickCalculationsEmpty
import dev.arkbuilders.rate.watchapp.presentation.theme.WearFab

@Composable
fun QuickCalculationsScreen(
    modifier: Modifier = Modifier,
    viewModel: QuickCalculationsViewModel = hiltViewModel(),
    onNavigateToAdd: () -> Unit,
    onNavigateToOptions: (Long) -> Unit,
) {
    val quickCalculationsList = viewModel.quickCalculations.collectAsStateWithLifecycle().value
    val listState = rememberScalingLazyListState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            WearFab(
                onClick = onNavigateToAdd,
                icon = Icons.Outlined.Add,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (quickCalculationsList.isEmpty()) {
                QuickCalculationsEmpty(
                    modifier = Modifier.fillMaxSize(),
                    onAddClick = onNavigateToAdd,
                )
            } else {
                ScalingLazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(Color.White),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    item {
                        Text(
                            modifier =
                                Modifier
                                    .fillMaxWidth(),
                            text = "Quick",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = ArkColor.TextPrimary,
                        )
                    }
                    items(
                        quickCalculationsList.size,
                        key = { quickCalculationsList[it].id },
                    ) { idx ->
                        QuickCalculationItem(
                            quick = quickCalculationsList[idx],
                            onClick = { onNavigateToOptions(quickCalculationsList[idx].id) },
                        )
                    }
                }
            }
        }
    }
}

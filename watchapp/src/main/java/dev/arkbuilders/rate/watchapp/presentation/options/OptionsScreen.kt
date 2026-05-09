package dev.arkbuilders.rate.watchapp.presentation.options

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OptionsScreen(
    modifier: Modifier = Modifier,
    viewModel: OptionsViewModel = hiltViewModel(),
    onUpdateClick: () -> Unit = {},
    onPinClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onReuseClick: () -> Unit = {},
    onDeleteSuccess: () -> Unit = {}
) {
    val quickPair by viewModel.quickPair.collectAsStateWithLifecycle()
    val listState = rememberScalingLazyListState()

    Scaffold(
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            modifier = modifier.fillMaxSize()
                .background(ArkColor.BGSecondaryAlt),
            state = listState,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    text = "Options",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = ArkColor.TextPrimary
                )
            }

            item {
                WearOptionButton(
                    text = "Update",
                    icon = WearOptionButtonIcon.Refresh,
                    buttonType = WearOptionButtonType.Success,
                    onClick = onUpdateClick
                )
            }

            item {
                WearOptionButton(
                    text = "Pin",
                    icon = WearOptionButtonIcon.Pin,
                    onClick = onPinClick
                )
            }

            item {
                WearOptionButton(
                    text = "Search",
                    icon = WearOptionButtonIcon.Search,
                    onClick = onSearchClick
                )
            }

            item {
                WearOptionButton(
                    text = "Re-Use",
                    icon = WearOptionButtonIcon.Reuse,
                    onClick = onReuseClick
                )
            }

            item {
                WearOptionButton(
                    text = "Delete",
                    icon = WearOptionButtonIcon.Delete,
                    buttonType = WearOptionButtonType.Destructive,
                    onClick = {
                        viewModel.deletePair(onDeleted = onDeleteSuccess)
                    }
                )
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun OptionsScreenPreview() {
    OptionsScreen()
}

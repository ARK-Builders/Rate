package dev.arkbuilders.rate.watchapp.presentation.options

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dev.arkbuilders.rate.watchapp.presentation.theme.WearConfirmationDialog
import dev.arkbuilders.rate.watchapp.presentation.theme.WearInfoDialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OptionsScreen(
    modifier: Modifier = Modifier,
    viewModel: OptionsViewModel = hiltViewModel(),
    onUpdateClick: (Long) -> Unit = {},
    onPinClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onDeleteSuccess: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val quickPair by viewModel.quickPair.collectAsStateWithLifecycle()
    val showPinLimitDialog by viewModel.showPinLimitDialog.collectAsStateWithLifecycle()
    val listState = rememberScalingLazyListState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        WearConfirmationDialog(
            title = "Delete Pair",
            message = "Are you sure you want to delete this pair?",
            onConfirm = {
                showDeleteDialog = false
                viewModel.deletePair(onDeleted = onDeleteSuccess)
            },
            onDismiss = {
                showDeleteDialog = false
                onDismiss()
            },
            isDestructive = true
        )
    }

    if (showPinLimitDialog) {
        WearInfoDialog(
            title = "Pin Limit Reached",
            message = "You can only pin up to 4 pairs. Please unpin another pair first.",
            onDismiss = { viewModel.dismissPinLimitDialog() },
            dismissText = "Ok got it"
        )
    }

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
                    onClick = { onUpdateClick(viewModel.pairId) }
                )
            }

            item {
                val isPinned = quickPair?.isPinned() == true
                WearOptionButton(
                    text = if (isPinned) "Unpin" else "Pin",
                    icon = WearOptionButtonIcon.Pin,
                    onClick = {
                        viewModel.togglePin(onSuccess = onPinClick)
                    }
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
                    text = "Delete",
                    icon = WearOptionButtonIcon.Delete,
                    buttonType = WearOptionButtonType.Destructive,
                    onClick = {
                        showDeleteDialog = true
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

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.watchapp.R
import dev.arkbuilders.rate.watchapp.presentation.theme.WearConfirmationDialog
import dev.arkbuilders.rate.watchapp.presentation.theme.WearInfoDialog

@Composable
fun OptionsScreen(
    modifier: Modifier = Modifier,
    viewModel: OptionsViewModel = hiltViewModel(),
    onUpdateClick: (Long) -> Unit = {},
    onPinClick: (String) -> Unit = {},
    onDeleteSuccess: (String) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val quickPair by viewModel.quickPair.collectAsStateWithLifecycle()
    val showPinLimitDialog by viewModel.showPinLimitDialog.collectAsStateWithLifecycle()
    val listState = rememberScalingLazyListState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val deletedSuccessfully = stringResource(R.string.deleted_successfully)
    val pinnedSuccessfully = stringResource(R.string.pinned_successfully)
    val unpinnedSuccessfully = stringResource(R.string.unpinned_successfully)

    if (showDeleteDialog) {
        WearConfirmationDialog(
            title = stringResource(R.string.delete_pair),
            message = stringResource(R.string.are_you_sure_you_want_to_delete_this_pair),
            onConfirm = {
                showDeleteDialog = false
                viewModel.deletePair(onDeleted = {
                    onDeleteSuccess(deletedSuccessfully)
                })
            },
            onDismiss = {
                showDeleteDialog = false
                onDismiss()
            },
            isDestructive = true,
        )
    }

    if (showPinLimitDialog) {
        WearInfoDialog(
            title = stringResource(R.string.pin_limit_reached),
            message =
                stringResource(
                    R.string.you_can_only_pin_up_to_4_pairs_please_unpin_another_pair_first,
                ),
            onDismiss = { viewModel.dismissPinLimitDialog() },
            dismissText = stringResource(R.string.ok_got_it),
        )
    }

    Scaffold(
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        },
    ) {
        ScalingLazyColumn(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(ArkColor.BGSecondaryAlt),
            state = listState,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    text = stringResource(R.string.options),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = ArkColor.TextPrimary,
                )
            }

            item {
                WearOptionButton(
                    text = stringResource(R.string.update),
                    icon = WearOptionButtonIcon.Refresh,
                    onClick = { onUpdateClick(viewModel.pairId) },
                )
            }

            item {
                val isPinned = quickPair?.isPinned() == true
                WearOptionButton(
                    text =
                        if (isPinned)
                            stringResource(
                                R.string.unpin,
                            )
                        else
                            stringResource(R.string.pin),
                    icon = WearOptionButtonIcon.Pin,
                    onClick = {
                        viewModel.togglePin(onSuccess = { pinned ->
                            if (pinned) {
                                onPinClick(pinnedSuccessfully)
                            } else {
                                onPinClick(unpinnedSuccessfully)
                            }
                        })
                    },
                )
            }

            item {
                WearOptionButton(
                    text = stringResource(R.string.delete),
                    icon = WearOptionButtonIcon.Delete,
                    buttonType = WearOptionButtonType.Destructive,
                    onClick = {
                        showDeleteDialog = true
                    },
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

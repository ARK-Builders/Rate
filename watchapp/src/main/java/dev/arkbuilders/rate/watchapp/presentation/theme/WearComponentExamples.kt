package dev.arkbuilders.rate.watchapp.presentation.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import dev.arkbuilders.rate.watchapp.presentation.options.WearOptionsHomeScreen
import dev.arkbuilders.rate.watchapp.presentation.options.WearPageIndicator

/**
 * Examples of how to use the WearOS components created for the ARK Rate app.
 * These components follow the Figma design system and WearOS best practices.
 */

@Composable
fun WearButtonExamples(modifier: Modifier = Modifier) {
    val listState = rememberScalingLazyListState()

    Scaffold(
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            modifier = modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                WearButton(
                    text = "Primary Button",
                    onClick = {},
                    style = WearButtonStyle.Primary,
                    leadingIcon = Icons.Outlined.Add
                )
            }

            item {
                WearButton(
                    text = "Secondary Button",
                    onClick = {},
                    style = WearButtonStyle.Secondary,
                    leadingIcon = Icons.Outlined.Edit
                )
            }

            item {
                WearButton(
                    text = "Outlined Button",
                    onClick = {},
                    style = WearButtonStyle.Outlined,
                    leadingIcon = Icons.Outlined.Refresh
                )
            }

            item {
                WearButton(
                    text = "Destructive Button",
                    onClick = {},
                    style = WearButtonStyle.Destructive
                )
            }

            item {
                WearPageIndicator(
                    totalPages = 5,
                    currentPage = 2
                )
            }
        }
    }
}

@Composable
fun WearDialogExamples(modifier: Modifier = Modifier) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WearButton(
            text = "Show Confirmation Dialog",
            onClick = { showConfirmDialog = true }
        )

        WearButton(
            text = "Show Info Dialog",
            onClick = { showInfoDialog = true }
        )

        if (showConfirmDialog) {
            WearConfirmationDialog(
                title = "Delete Item",
                message = "Are you sure you want to delete this item?",
                onConfirm = {
                    showConfirmDialog = false
                    // Handle confirmation
                },
                onDismiss = { showConfirmDialog = false },
                isDestructive = true
            )
        }

        if (showInfoDialog) {
            WearInfoDialog(
                title = "Success",
                message = "Operation completed successfully!",
                onDismiss = { showInfoDialog = false }
            )
        }
    }
}

// Preview showing the main Options screen from Figma
@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun WearOptionsHomeScreenExample() {
    WearOptionsHomeScreen(
        currentPage = 1,
        totalPages = 3,
        onUpdateClick = { /* Handle update */ },
        onPinClick = { /* Handle pin */ },
        onEditClick = { /* Handle edit */ },
        onReuseClick = { /* Handle reuse */ },
        onDeleteClick = { /* Handle delete */ }
    )
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun WearButtonExamplesPreview() {
    WearButtonExamples()
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun WearDialogExamplesPreview() {
    WearDialogExamples()
}

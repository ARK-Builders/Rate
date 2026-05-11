package dev.arkbuilders.rate.watchapp.presentation.options

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import dev.arkbuilders.rate.core.presentation.theme.ArkColor

@Composable
fun WearOptionsHomeScreen(
    modifier: Modifier = Modifier,
    currentPage: Int = 1,
    totalPages: Int = 3,
    onUpdateClick: () -> Unit = {},
    onPinClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onReuseClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val listState = rememberScalingLazyListState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .clip(CircleShape),
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title section
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

            // Page indicator
            item {
                WearPageIndicator(
                    totalPages = totalPages,
                    currentPage = currentPage,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            // Main slot - Option buttons
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WearOptionButton(
                        text = "Update",
                        icon = WearOptionButtonIcon.Refresh,
                        onClick = onUpdateClick
                    )

                    WearOptionButton(
                        text = "Pin",
                        icon = WearOptionButtonIcon.Pin,
                        onClick = onPinClick
                    )

                    WearOptionButton(
                        text = "Edit",
                        icon = WearOptionButtonIcon.Edit,
                        onClick = onEditClick
                    )

                    WearOptionButton(
                        text = "Re-Use",
                        icon = WearOptionButtonIcon.Reuse,
                        onClick = onReuseClick
                    )

                    WearOptionButton(
                        text = "Delete",
                        icon = WearOptionButtonIcon.Delete,
                        buttonType = WearOptionButtonType.Destructive,
                        onClick = onDeleteClick
                    )
                }
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun WearOptionsHomeScreenPreview() {
    WearOptionsHomeScreen()
}

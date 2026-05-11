@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.feature.quick.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.feature.quick.domain.model.QuickCalculation

// bug: callbacks from swipe called multiply times
@Composable
fun PinnedQuickSwipeItem(
    content: @Composable () -> Unit,
    calculation: QuickCalculation,
    onUnpin: (QuickCalculation) -> Unit,
    onDelete: () -> Unit,
) {
    val dismissState =
        rememberSwipeToDismissBoxState(
            confirmValueChange = {
                when (it) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        onUnpin(calculation)
                        true
                    }

                    SwipeToDismissBoxValue.EndToStart -> {
                        onDelete()
                        true
                    }

                    SwipeToDismissBoxValue.Settled -> false
                }
            },
        )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            DismissBackground(
                state = dismissState,
                isPinned = calculation.isPinned(),
            )
        },
        content = { content() },
    )
}

@Composable
fun QuickSwipeItem(
    content: @Composable () -> Unit,
    calculation: QuickCalculation,
    onPin: (QuickCalculation) -> Unit,
    onDelete: () -> Unit,
) {
    val dismissState =
        rememberSwipeToDismissBoxState(
            confirmValueChange = {
                when (it) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        onPin(calculation)
                        true
                    }

                    SwipeToDismissBoxValue.EndToStart -> {
                        onDelete()
                        true
                    }

                    SwipeToDismissBoxValue.Settled -> false
                }
            },
        )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            DismissBackground(
                state = dismissState,
                isPinned = calculation.isPinned(),
            )
        },
        content = { content() },
    )
}

@Composable
private fun DismissBackground(
    state: SwipeToDismissBoxState,
    isPinned: Boolean,
) {
    val color =
        when (state.dismissDirection) {
            SwipeToDismissBoxValue.EndToStart -> ArkColor.UtilityError500
            else -> if (isPinned) ArkColor.FGQuarterary else ArkColor.Secondary
        }

    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (state.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
            if (isPinned.not()) {
                Row {
                    Icon(
                        modifier = Modifier.padding(start = 17.dp),
                        painter = painterResource(id = R.drawable.ic_pin),
                        contentDescription = null,
                        tint = Color.White,
                    )
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = stringResource(R.string.pin),
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    )
                }
            } else {
                Text(
                    modifier = Modifier.padding(start = 17.dp),
                    text = stringResource(R.string.unpin),
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
            }
        }
        Spacer(modifier = Modifier)
        if (state.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
            Row {
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = stringResource(R.string.delete),
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
                Icon(
                    modifier = Modifier.padding(end = 17.dp),
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        }
    }
}

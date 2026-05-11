@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package dev.arkbuilders.rate.feature.quick.presentation.add

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.generated.search.destinations.SearchCurrencyScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.presentation.R
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppButton
import dev.arkbuilders.rate.core.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.core.presentation.ui.DropDownWithIcon
import dev.arkbuilders.rate.core.presentation.ui.GroupCreateDialog
import dev.arkbuilders.rate.core.presentation.ui.GroupSelectPopup
import dev.arkbuilders.rate.core.presentation.utils.ReorderHapticFeedbackType
import dev.arkbuilders.rate.core.presentation.utils.rememberReorderHapticFeedback
import dev.arkbuilders.rate.feature.quick.di.QuickComponentHolder
import dev.arkbuilders.rate.feature.search.presentation.SearchNavResult
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
@Destination<ExternalModuleGraph>
fun AddQuickScreen(
    quickCalculationId: Long? = null,
    newCode: CurrencyCode? = null,
    reuseNotEdit: Boolean = true,
    groupId: Long? = null,
    navigator: DestinationsNavigator,
    // return back new calculation id
    resultNavigator: ResultBackNavigator<Long>,
    resultRecipient: ResultRecipient<SearchCurrencyScreenDestination, SearchNavResult>,
) {
    val ctx = LocalContext.current
    val quickComponent =
        remember {
            QuickComponentHolder.provide(ctx)
        }
    val viewModel: AddQuickViewModel =
        viewModel(
            factory =
                quickComponent.addQuickVMFactory()
                    .create(quickCalculationId, newCode, reuseNotEdit, groupId),
        )

    resultRecipient.onNavResult { result ->
        when (result) {
            NavResult.Canceled -> {}
            is NavResult.Value -> {
                viewModel.onNavResult(result.value)
            }
        }
    }

    BackHandler {
        viewModel.onBackClick()
    }

    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        handleAddQuickSideEffect(effect, navigator, resultNavigator)
    }
    Scaffold(
        topBar = {
            val title =
                if (reuseNotEdit)
                    R.string.quick_add_new_calculation
                else
                    R.string.quick_edit_calc
            AppTopBarBack(
                title = stringResource(title),
                onBackClick = { viewModel.onBackClick() },
            )
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            Content(
                state = state,
                onAmountChanged = viewModel::onAssetAmountChange,
                onNewCurrencyClick = viewModel::onAddCode,
                onCurrencyRemove = viewModel::onCurrencyRemove,
                onGroupSelect = viewModel::onGroupSelect,
                onGroupCreate = viewModel::onGroupCreate,
                onCodeChange = viewModel::onSetCode,
                onSwapClick = viewModel::onSwapClick,
                onCurrenciesSwap = viewModel::onCurrenciesSwap,
                onAddAsset = viewModel::onAddQuickCalculation,
            )
        }
    }
}

@Composable
private fun Content(
    state: AddQuickScreenState,
    onAmountChanged: (String) -> Unit,
    onNewCurrencyClick: () -> Unit,
    onCurrencyRemove: (Int) -> Unit,
    onGroupSelect: (Group) -> Unit,
    onGroupCreate: (String) -> Unit,
    onCodeChange: (Int) -> Unit,
    onSwapClick: () -> Unit,
    onCurrenciesSwap: (from: Int, to: Int) -> Unit,
    onAddAsset: () -> Unit,
) {
    val ctx = LocalContext.current
    var showNewGroupDialog by remember { mutableStateOf(false) }
    var showGroupsPopup by remember { mutableStateOf(false) }
    var addGroupBtnWidth by remember { mutableStateOf(0) }

    if (showNewGroupDialog) {
        GroupCreateDialog(
            validateGroupNameUseCase =
                QuickComponentHolder.provide(ctx)
                    .validateGroupNameUseCase(),
            onDismiss = { showNewGroupDialog = false },
        ) {
            onGroupCreate(it)
        }
    }

    val haptic = rememberReorderHapticFeedback()
    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            val fromIndex = state.currencies.indexOfFirst { it.code == from.key }
            val toIndex = state.currencies.indexOfFirst { it.code == to.key }
            onCurrenciesSwap(fromIndex, toIndex)
            haptic.performHapticFeedback(ReorderHapticFeedbackType.MOVE)
        }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier =
                Modifier
                    .weight(1f),
            state = lazyListState,
        ) {
            currencies(
                state,
                reorderableLazyColumnState,
                haptic,
                onAmountChanged,
                onCurrencyRemove,
                onCodeChange,
                onSwapClick,
            )
            item {
                Button(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, ArkColor.Border),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = ArkColor.FGSecondary,
                        ),
                    onClick = { onNewCurrencyClick() },
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Icon(
                        modifier = Modifier.padding(start = 20.dp),
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = null,
                    )
                    Text(
                        modifier =
                            Modifier.padding(
                                start = 8.dp,
                                top = 10.dp,
                                bottom = 10.dp,
                                end = 18.dp,
                            ),
                        text = stringResource(R.string.new_currency),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    )
                }
                DropDownWithIcon(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                            .onPlaced {
                                addGroupBtnWidth = it.size.width
                            },
                    onClick = { showGroupsPopup = true },
                    title = state.group.name,
                    icon = painterResource(id = R.drawable.ic_group),
                )
                if (showGroupsPopup) {
                    Box(
                        modifier =
                            Modifier.padding(
                                start = 16.dp,
                                top = 4.dp,
                            ),
                    ) {
                        Popup(
                            offset = IntOffset(0, 0),
                            properties = PopupProperties(focusable = true),
                            onDismissRequest = { showGroupsPopup = false },
                        ) {
                            GroupSelectPopup(
                                groups = state.availableGroups,
                                widthPx = addGroupBtnWidth,
                                onGroupSelect = { onGroupSelect(it) },
                                onNewGroupClick = { showNewGroupDialog = true },
                                onDismiss = { showGroupsPopup = false },
                            )
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
        }
        Column {
            HorizontalDivider(thickness = 1.dp, color = ArkColor.BorderSecondary)
            AppButton(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                onClick = {
                    onAddAsset()
                },
                enabled = state.finishEnabled,
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun Preview() {
    Content(
        state = AddQuickScreenState(),
        onAmountChanged = {},
        onNewCurrencyClick = {},
        onCurrencyRemove = {},
        onGroupSelect = {},
        onCodeChange = {},
        onSwapClick = {},
        onCurrenciesSwap = { _, _ -> },
        onAddAsset = {},
        onGroupCreate = {},
    )
}

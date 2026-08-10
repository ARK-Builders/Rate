package dev.arkbuilders.rate.feature.quick.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.generated.quick.destinations.AddQuickScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.result.onResult
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.core.presentation.ui.CurrencyInfoItem
import dev.arkbuilders.rate.core.presentation.ui.GroupViewPager
import dev.arkbuilders.rate.core.presentation.ui.ListHeader
import dev.arkbuilders.rate.core.presentation.ui.LoadingScreen
import dev.arkbuilders.rate.core.presentation.ui.RateSnackbarHost
import dev.arkbuilders.rate.core.presentation.ui.SearchTextField
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupOptionsBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupRenameBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupReorderBottomSheet
import dev.arkbuilders.rate.feature.quick.di.QuickComponentHolder
import dev.arkbuilders.rate.feature.quick.domain.model.PinnedQuickCalculation
import dev.arkbuilders.rate.feature.quick.domain.model.QuickCalculation
import dev.arkbuilders.rate.feature.quick.presentation.QuickExternalNavigator
import dev.arkbuilders.rate.feature.quick.presentation.ui.PinnedQuickSwipeItem
import dev.arkbuilders.rate.feature.quick.presentation.ui.QuickDateFormatter
import dev.arkbuilders.rate.feature.quick.presentation.ui.QuickOptionsBottomSheet
import dev.arkbuilders.rate.feature.quick.presentation.ui.QuickSwipeItem
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import dev.arkbuilders.rate.core.presentation.R as CoreR

@OptIn(ExperimentalMaterial3Api::class)
@Destination<ExternalModuleGraph>
@Composable
fun QuickScreen(
    navigator: DestinationsNavigator,
    // expect new calculation id
    resultRecipient: ResultRecipient<AddQuickScreenDestination, Long>,
    externalNavigator: QuickExternalNavigator,
) {
    val ctx = LocalContext.current
    val component =
        remember {
            QuickComponentHolder.provide(ctx)
        }
    val viewModel: QuickViewModel =
        viewModel(
            factory = component.quickVMFactory().create(),
        )

    resultRecipient.onResult(
        onCancelled = viewModel::onNavResultCancelled,
        onValue = viewModel::onNavResultValue,
    )

    BackHandler {
        viewModel.onBackClick()
    }

    val state by viewModel.collectAsState()
    val pagerState = rememberPagerState { state.pages.size }
    val snackState = remember { SnackbarHostState() }

    val isEmpty = state.pages.isEmpty()

    val scope = rememberCoroutineScope()
    val calculationOptionsSheetState = rememberModalBottomSheetState()
    val editGroupReorderSheetState = rememberModalBottomSheetState()
    val editGroupOptionsSheetState = rememberModalBottomSheetState()
    val editGroupRenameSheetState = rememberModalBottomSheetState()

    fun getCurrentGroup() = state.pages.getOrNull(pagerState.currentPage)?.group

    if (state.showUnlimitedPinDialog) {
        UnlimitedPinPremiumDialog(
            onDismiss = viewModel::onDismissUnlimitedPinDialog,
            onTryPremiumClick = viewModel::onTryPremiumClick,
        )
    }

    HandleQuickSideEffects(
        viewModel = viewModel,
        state = state,
        pagerState = pagerState,
        snackState = snackState,
        ctx = ctx,
        navigator = navigator,
        externalNavigator = externalNavigator,
    )

    Scaffold(
        floatingActionButton = {
            if (state.initialized.not())
                return@Scaffold

            if (isEmpty)
                return@Scaffold

            FloatingActionButton(
                contentColor = Color.White,
                containerColor = ArkColor.Secondary,
                shape = CircleShape,
                onClick = {
                    navigator.navigate(
                        AddQuickScreenDestination(
                            groupId = getCurrentGroup()?.id,
                        ),
                    )
                },
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(CoreRString.add))
            }
        },
        snackbarHost = {
            RateSnackbarHost(snackState)
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            when {
                state.initialized.not() -> LoadingScreen()
                isEmpty -> QuickEmpty(navigator)
                else ->
                    Content(
                        state = state,
                        pagerState = pagerState,
                        onEditGroups = viewModel::onShowGroupsReorder,
                        onFilterChanged = viewModel::onFilterChanged,
                        onDelete = viewModel::onDelete,
                        onClick = {
                            viewModel.onReuse(it)
                        },
                        onLongClick = {
                            viewModel.onShowGroupOptions(it)
                        },
                        onPinRequested = viewModel::onPinRequested,
                        onUnpin = viewModel::onUnpin,
                        onNewCode = {
                            navigator
                                .navigate(
                                    AddQuickScreenDestination(
                                        newCode = it,
                                        groupId = getCurrentGroup()?.id,
                                    ),
                                )
                        },
                    )
            }
        }
        state.calculationOptionsData?.let {
            QuickOptionsBottomSheet(
                calculationOptionsSheetState,
                calculation = it.calculation,
                onPin = { calculation -> viewModel.onPinRequested(calculation) },
                onUnpin = viewModel::onUnpin,
                onEdit = viewModel::onEdit,
                onReuse = viewModel::onReuse,
                onDelete = viewModel::onDelete,
                onDismiss = {
                    scope.launch {
                        calculationOptionsSheetState.hide()
                        viewModel.onHideOptions()
                    }
                },
            )
        }
        state.editGroupReorderSheetState?.let {
            EditGroupReorderBottomSheet(
                sheetState = editGroupReorderSheetState,
                state = it,
                onSwap = { from, to -> viewModel.onSwapGroups(from, to) },
                onOptionsClick = { viewModel.onShowGroupOptions(it) },
                onDismiss = {
                    scope.launch {
                        editGroupReorderSheetState.hide()
                        viewModel.onDismissGroupsReorder()
                    }
                },
            )
        }
        state.editGroupOptionsSheetState?.let {
            EditGroupOptionsBottomSheet(
                sheetState = editGroupOptionsSheetState,
                state = it,
                onRename = { viewModel.onShowGroupRename(it.group) },
                onDelete = { viewModel.onGroupDelete(it.group) },
                onDismiss = {
                    scope.launch {
                        editGroupOptionsSheetState.hide()
                        viewModel.onDismissGroupOptions()
                    }
                },
            )
        }
        val validateGroupNameUseCase =
            remember {
                QuickComponentHolder.provide(ctx).validateGroupNameUseCase()
            }
        state.editGroupRenameSheetState?.let { renameState ->
            EditGroupRenameBottomSheet(
                sheetState = editGroupRenameSheetState,
                state = renameState,
                validateGroupNameUseCase = validateGroupNameUseCase,
                onDone = { viewModel.onGroupRename(renameState.group, it) },
                onDismiss = {
                    scope.launch {
                        editGroupRenameSheetState.hide()
                        viewModel.onDismissGroupRename()
                    }
                },
            )
        }
    }
}

@Composable
private fun Content(
    state: QuickScreenState,
    pagerState: PagerState,
    onEditGroups: () -> Unit,
    onFilterChanged: (String) -> Unit,
    onDelete: (QuickCalculation) -> Unit,
    onClick: (QuickCalculation) -> Unit,
    onLongClick: (QuickCalculation) -> Unit = {},
    onPinRequested: (QuickCalculation) -> Boolean,
    onUnpin: (QuickCalculation) -> Unit,
    onNewCode: (CurrencyCode) -> Unit,
) {
    val groups = state.pages.map { it.group }
    Column {
        SearchTextField(
            modifier =
                Modifier.padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp,
                ),
            text = state.filter,
        ) {
            onFilterChanged(it)
        }
        if (state.filter.isNotEmpty()) {
            QuickSearchPage(
                topResultsFiltered = state.topResultsFiltered,
                onNewCode = onNewCode,
            )
        } else {
            if (state.pages.size == 1) {
                GroupPage(
                    frequent = state.frequent,
                    currencies = state.currencies,
                    pinned = state.pages.first().pinned,
                    notPinned = state.pages.first().notPinned,
                    onDelete = onDelete,
                    onClick = onClick,
                    onLongClick = onLongClick,
                    onPinRequested = onPinRequested,
                    onUnpin = onUnpin,
                    onNewCode = onNewCode,
                )
            } else {
                GroupViewPager(
                    pagerState = pagerState,
                    groups = groups,
                    onEditGroups = onEditGroups,
                ) { index ->
                    GroupPage(
                        frequent = state.frequent,
                        currencies = state.currencies,
                        pinned = state.pages[index].pinned,
                        notPinned = state.pages[index].notPinned,
                        onDelete = onDelete,
                        onClick = onClick,
                        onLongClick = onLongClick,
                        onPinRequested = onPinRequested,
                        onUnpin = onUnpin,
                        onNewCode = onNewCode,
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupPage(
    frequent: List<CurrencyInfo>,
    currencies: List<CurrencyInfo>,
    pinned: List<PinnedQuickCalculation>,
    notPinned: List<QuickCalculation>,
    onDelete: (QuickCalculation) -> Unit,
    onPinRequested: (QuickCalculation) -> Boolean,
    onUnpin: (QuickCalculation) -> Unit,
    onClick: (QuickCalculation) -> Unit = {},
    onLongClick: (QuickCalculation) -> Unit = {},
    onNewCode: (CurrencyCode) -> Unit,
) {
    val ctx = LocalContext.current
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (pinned.isNotEmpty()) {
            item {
                ListHeader(text = stringResource(CoreRString.quick_pinned_calculations))
            }
            items(pinned, key = { it.calculation.id }) {
                PinnedQuickSwipeItem(
                    content = {
                        QuickItem(
                            from = Amount(it.calculation.from, it.calculation.amount),
                            to = it.actualTo,
                            dateText =
                                QuickDateFormatter.calculationRefreshedTime(
                                    ctx,
                                    it.refreshDate,
                                ),
                            onClick = { onClick(it.calculation) },
                            onLongClick = { onLongClick(it.calculation) },
                        )
                    },
                    calculation = it.calculation,
                    onDelete = { onDelete(it.calculation) },
                    onUnpin = onUnpin,
                )
                AppHorDiv16()
            }
        }
        if (notPinned.isNotEmpty()) {
            item {
                ListHeader(text = stringResource(CoreRString.quick_calculations))
            }
            items(notPinned, key = { it.id }) {
                QuickSwipeItem(
                    content = {
                        QuickItem(
                            from = Amount(it.from, it.amount),
                            to = it.to,
                            dateText =
                                QuickDateFormatter.calculationCalculatedTime(
                                    ctx,
                                    it.calculatedDate,
                                ),
                            onClick = { onClick(it) },
                            onLongClick = { onLongClick(it) },
                        )
                    },
                    calculation = it,
                    onDelete = { onDelete(it) },
                    onPinRequested = onPinRequested,
                )
                AppHorDiv16()
            }
        }
        if (frequent.isNotEmpty()) {
            item {
                ListHeader(text = stringResource(CoreRString.frequent_currencies))
            }
            items(frequent) { name ->
                CurrencyInfoItem(name) { onNewCode(it.code) }
            }
        }
        item {
            ListHeader(text = stringResource(CoreRString.all_currencies))
        }
        items(currencies, key = { it.code }) { name ->
            CurrencyInfoItem(name) { onNewCode(it.code) }
        }
    }
}

@Composable
private fun UnlimitedPinPremiumDialog(
    onDismiss: () -> Unit,
    onTryPremiumClick: () -> Unit,
) {
    val dialogShape = RoundedCornerShape(28.dp)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(dialogShape)
                    .background(Color.White)
                    .border(
                        border = BorderStroke(1.dp, ArkColor.BorderSecondary),
                        shape = dialogShape,
                    )
                    .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .size(84.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        ArkColor.BrandSecondary.copy(alpha = 0.16f),
                                        ArkColor.Teal500.copy(alpha = 0.16f),
                                    ),
                                ),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(
                                    border =
                                        BorderStroke(
                                            width = 1.dp,
                                            color = ArkColor.BorderSecondary,
                                        ),
                                    shape = CircleShape,
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            modifier = Modifier.size(32.dp),
                            painter = painterResource(CoreR.drawable.ic_premium),
                            contentDescription = null,
                            tint = Color.Unspecified,
                        )
                    }
                }

                IconButton(
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .size(40.dp),
                    onClick = onDismiss,
                ) {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        painter = painterResource(CoreR.drawable.ic_close),
                        contentDescription = stringResource(CoreRString.close),
                        tint = ArkColor.FGQuinary,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                text = "Unlimited pin in Premium",
                color = ArkColor.TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
                text =
                    "Pin as many quick calculations as you " +
                        "need and keep your favorites one tap away.",
                color = ArkColor.TextTertiary,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
            )
            Button(
                modifier =
                    Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                        .height(48.dp),
                onClick = onTryPremiumClick,
                colors = ButtonDefaults.buttonColors(containerColor = ArkColor.Primary),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = "Try Premium",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewItem() {
    QuickItem(
        from = Amount("BTC", 1.0.toBigDecimal()),
        to = listOf(Amount("USD", 30.0.toBigDecimal())),
        dateText = "Calculated on",
        onClick = {},
        onLongClick = {},
    )
}

@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.feature.portfolio.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.generated.portfolio.destinations.AddAssetScreenDestination
import com.ramcosta.composedestinations.generated.portfolio.destinations.EditAssetScreenDestination
import com.ramcosta.composedestinations.generated.search.destinations.SearchCurrencyScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.result.onResult
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppHorDiv16
import dev.arkbuilders.rate.core.presentation.ui.AppSwipeToDismiss
import dev.arkbuilders.rate.core.presentation.ui.GroupViewPager
import dev.arkbuilders.rate.core.presentation.ui.LargeNumberText
import dev.arkbuilders.rate.core.presentation.ui.LargeNumberTooltipBox
import dev.arkbuilders.rate.core.presentation.ui.LoadingScreen
import dev.arkbuilders.rate.core.presentation.ui.NoResult
import dev.arkbuilders.rate.core.presentation.ui.RateSnackbarHost
import dev.arkbuilders.rate.core.presentation.ui.SearchTextField
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupOptionsBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupRenameBottomSheet
import dev.arkbuilders.rate.core.presentation.ui.group.EditGroupReorderBottomSheet
import dev.arkbuilders.rate.feature.portfolio.di.PortfolioComponentHolder
import dev.arkbuilders.rate.feature.portfolio.domain.model.Asset
import dev.arkbuilders.rate.feature.portfolio.presentation.model.AddAssetsNavResult
import dev.arkbuilders.rate.feature.search.presentation.SearchNavResult
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import java.math.BigDecimal

@Destination<ExternalModuleGraph>
@Composable
fun PortfolioScreen(
    navigator: DestinationsNavigator,
    addResultRecipient: ResultRecipient<AddAssetScreenDestination, AddAssetsNavResult>,
    searchResultRecipient: ResultRecipient<SearchCurrencyScreenDestination, SearchNavResult>,
) {
    val ctx = LocalContext.current
    val component =
        remember {
            PortfolioComponentHolder.provide(ctx)
        }

    val viewModel: PortfolioViewModel =
        viewModel(factory = component.assetsVMFactory())

    addResultRecipient.onResult {
        viewModel.onReturnFromAddScreen(it)
    }
    searchResultRecipient.onResult {
        viewModel.onChangeBaseCurrency(it.code)
    }

    BackHandler {
        viewModel.onBackClick()
    }

    val state by viewModel.collectAsState()
    val snackState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isEmpty = state.pages.isEmpty()
    val pagerState = rememberPagerState { state.pages.size }
    val editGroupReorderSheetState = rememberModalBottomSheetState()
    val editGroupOptionsSheetState = rememberModalBottomSheetState()
    val editGroupRenameSheetState = rememberModalBottomSheetState()
    val changeBaseCurrencyTitle = stringResource(CoreRString.change_base_currency)

    fun getCurrentGroup() = state.pages.getOrNull(pagerState.currentPage)?.group

    HandlePortfolioSideEffect(
        viewModel,
        navigator,
        state,
        pagerState,
        snackState,
        ctx,
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
                    navigator.navigate(AddAssetScreenDestination(groupId = getCurrentGroup()?.id))
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
                isEmpty -> PortfolioEmpty(navigator)
                else ->
                    Content(
                        state = state,
                        pagerState = pagerState,
                        onEditGroups = viewModel::onShowGroupsReorder,
                        onClick = { display ->
                            navigator
                                .navigate(EditAssetScreenDestination(display.asset.id))
                        },
                        onChangeBaseCurrency = {
                            navigator.navigate(
                                SearchCurrencyScreenDestination(
                                    title = changeBaseCurrencyTitle,
                                ),
                            )
                        },
                        onFilterChange = viewModel::onFilterChange,
                        onDelete = viewModel::onAssetRemove,
                    )
            }
        }
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
            PortfolioComponentHolder.provide(ctx).validateGroupNameUseCase()
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

@Composable
private fun Content(
    state: PortfolioScreenState,
    pagerState: PagerState,
    onEditGroups: () -> Unit,
    onClick: (PortfolioDisplayAsset) -> Unit,
    onChangeBaseCurrency: () -> Unit,
    onFilterChange: (String) -> Unit,
    onDelete: (Asset) -> Unit,
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
            onValueChange = onFilterChange,
        )
        if (state.pages.size == 1) {
            GroupPage(
                filter = state.filter,
                baseCode = state.baseCode,
                amounts = state.pages.first().assets,
                onClick = onClick,
                onChangeBaseCurrency = onChangeBaseCurrency,
                onDelete = onDelete,
            )
        } else {
            GroupViewPager(
                pagerState = pagerState,
                groups = groups,
                onEditGroups = onEditGroups,
            ) { index ->
                GroupPage(
                    filter = state.filter,
                    baseCode = state.baseCode,
                    amounts = state.pages[index].assets,
                    onClick = onClick,
                    onChangeBaseCurrency = onChangeBaseCurrency,
                    onDelete = onDelete,
                )
            }
        }
    }
}

@Composable
private fun GroupPage(
    filter: String,
    baseCode: CurrencyCode,
    amounts: List<PortfolioDisplayAsset>,
    onClick: (PortfolioDisplayAsset) -> Unit = {},
    onChangeBaseCurrency: () -> Unit,
    onDelete: (Asset) -> Unit,
) {
    val total =
        amounts.fold(BigDecimal.ZERO) { acc, amount ->
            acc + amount.baseAmount.value
        }
    val filtered =
        amounts.filter { it.asset.code.contains(filter, ignoreCase = true) }
    if (filtered.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (filter.isEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Box(Modifier.fillMaxWidth()) {
                        Row(
                            modifier =
                                Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 14.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onChangeBaseCurrency() }
                                    .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier,
                                text = baseCode,
                                fontSize = 12.6.sp,
                                fontWeight = FontWeight.W500,
                                color = ArkColor.TextTertiary,
                            )
                            Icon(
                                modifier =
                                    Modifier
                                        .padding(start = 8.dp)
                                        .width(10.5.dp)
                                        .height(6.dp),
                                painter = painterResource(CoreRDrawable.ic_chevron),
                                contentDescription = null,
                                tint = ArkColor.TextTertiary,
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        modifier = Modifier,
                        text = stringResource(CoreRString.portfolio_total_assets),
                        color = ArkColor.TextTertiary,
                        fontWeight = FontWeight.Medium,
                    )
                    Row(
                        modifier =
                            Modifier
                                .padding(top = 8.dp, start = 24.dp, end = 24.dp),
                    ) {
                        LargeNumberTooltipBox(number = total, code = baseCode) {
                            LargeNumberText(
                                number = total,
                                color = ArkColor.TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 36.sp,
                            )
                        }
                        Text(
                            modifier =
                                Modifier
                                    .padding(
                                        start = 2.dp,
                                        top = 2.dp,
                                    )
                                    .weight(1f, fill = false),
                            text = CurrUtils.getSymbolOrCode(baseCode),
                            color = ArkColor.TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                        )
                    }
                    AppHorDiv16(Modifier.padding(top = 32.dp))
                }
            }
            items(filtered, key = { it.asset.id }) {
                AppSwipeToDismiss(
                    content = { CurrencyItem(it, onClick = onClick) },
                    onDelete = { onDelete(it.asset) },
                )
                AppHorDiv16()
            }
        }
    } else {
        NoResult()
    }
}

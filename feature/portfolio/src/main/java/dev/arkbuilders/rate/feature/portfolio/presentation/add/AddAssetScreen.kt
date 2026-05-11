package dev.arkbuilders.rate.feature.portfolio.presentation.add

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.presentation.CoreRDrawable
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppButton
import dev.arkbuilders.rate.core.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.core.presentation.ui.DropDownWithIcon
import dev.arkbuilders.rate.core.presentation.ui.GroupCreateDialog
import dev.arkbuilders.rate.core.presentation.ui.GroupSelectPopup
import dev.arkbuilders.rate.feature.portfolio.di.PortfolioComponentHolder
import dev.arkbuilders.rate.feature.portfolio.presentation.model.AddAssetsNavResult
import dev.arkbuilders.rate.feature.search.presentation.SearchNavResult
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import dev.arkbuilders.rate.core.presentation.R as CoreR

@Destination<ExternalModuleGraph>
@Composable
fun AddAssetScreen(
    groupId: Long? = null,
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<AddAssetsNavResult>,
    resultRecipient: ResultRecipient<SearchCurrencyScreenDestination, SearchNavResult>,
) {
    val ctx = LocalContext.current
    val component =
        remember {
            PortfolioComponentHolder.provide(ctx)
        }
    val viewModel: AddAssetViewModel =
        viewModel(factory = component.addCurrencyVMFactory().create(groupId))

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
        handleAddAssetSideEffect(effect, navigator, resultNavigator)
    }

    Scaffold(
        topBar = {
            AppTopBarBack(
                title = stringResource(CoreRString.portfolio_add_new_assets),
                onBackClick = { viewModel.onBackClick() },
            )
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            Content(
                state = state,
                onAssetValueChanged = viewModel::onAssetValueChange,
                onNewCurrencyClick = viewModel::onAddCode,
                onAssetRemove = viewModel::onAssetRemove,
                onGroupSelect = viewModel::onGroupSelect,
                onGroupCreate = viewModel::onGroupCreate,
                onCodeChange = viewModel::onSetCode,
                onAddAsset = viewModel::onAddAsset,
            )
        }
    }
}

@Composable
private fun Content(
    state: AddAssetState,
    onAssetValueChanged: (Int, String) -> Unit,
    onNewCurrencyClick: () -> Unit,
    onAssetRemove: (Int) -> Unit,
    onGroupSelect: (Group) -> Unit,
    onGroupCreate: (String) -> Unit,
    onCodeChange: (Int) -> Unit,
    onAddAsset: () -> Unit,
) {
    val ctx = LocalContext.current
    var showNewGroupDialog by remember { mutableStateOf(false) }
    var showGroupsPopup by remember { mutableStateOf(false) }
    var addGroupBtnWidth by remember { mutableStateOf(0) }

    if (showNewGroupDialog) {
        GroupCreateDialog(
            title = stringResource(CoreRString.portfolio_name_dialog_title),
            desc = stringResource(CoreRString.portfolio_name_dialog_desc),
            inputTitle = stringResource(CoreRString.portfolio_name_dialog_portfolio_name),
            inputPlaceholder = stringResource(CoreRString.portfolio_name_dialog_placeholder),
            validateGroupNameUseCase =
                PortfolioComponentHolder.provide(ctx)
                    .validateGroupNameUseCase(),
            onDismiss = { showNewGroupDialog = false },
            onConfirmClick = { onGroupCreate(it) },
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            Currencies(state, onAssetValueChanged, onAssetRemove, onCodeChange)
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
                    painter = painterResource(id = CoreRDrawable.ic_add),
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
                    text = stringResource(CoreRString.portfolio_new_asset),
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
                icon = painterResource(id = CoreR.drawable.ic_group),
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
                            newGroupTitle = stringResource(CoreRString.portfolio_new_portfolio),
                            onGroupSelect = { onGroupSelect(it) },
                            onNewGroupClick = { showNewGroupDialog = true },
                            onDismiss = { showGroupsPopup = false },
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
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
            ) {
                Text(text = stringResource(CoreRString.portfolio_add_new_assets))
            }
        }
    }
}

@Composable
private fun Currencies(
    state: AddAssetState,
    onAssetValueChanged: (Int, String) -> Unit,
    onAssetRemove: (Int) -> Unit,
    onCodeChange: (Int) -> Unit,
) {
    state.currencies.forEachIndexed { index, amount ->
        InputCurrency(
            index,
            amount,
            onAssetValueChanged,
            onAssetRemove,
            onCodeChange,
        )
    }
}

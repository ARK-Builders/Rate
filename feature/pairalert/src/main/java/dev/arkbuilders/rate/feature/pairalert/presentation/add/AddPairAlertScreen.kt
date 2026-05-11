@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.feature.pairalert.presentation.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppButton
import dev.arkbuilders.rate.core.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.core.presentation.ui.DropDownWithIcon
import dev.arkbuilders.rate.core.presentation.ui.GroupCreateDialog
import dev.arkbuilders.rate.core.presentation.ui.GroupSelectPopup
import dev.arkbuilders.rate.feature.pairalert.di.PairAlertComponentHolder
import dev.arkbuilders.rate.feature.search.presentation.SearchNavResult
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import dev.arkbuilders.rate.core.presentation.R as CoreR

@Destination<ExternalModuleGraph>
@Composable
fun AddPairAlertScreen(
    pairAlertId: Long? = null,
    groupId: Long? = null,
    navigator: DestinationsNavigator,
    // return back new pair id
    resultNavigator: ResultBackNavigator<Long>,
    resultRecipient: ResultRecipient<SearchCurrencyScreenDestination, SearchNavResult>,
) {
    val ctx = LocalContext.current
    val component =
        remember {
            PairAlertComponentHolder.provide(ctx)
        }
    val viewModel: AddPairAlertViewModel =
        viewModel(
            factory = component.addPairAlertVMFactory().create(pairAlertId, groupId),
        )

    resultRecipient.onNavResult { result ->
        when (result) {
            NavResult.Canceled -> {}
            is NavResult.Value -> {
                viewModel.onNavResult(result.value)
            }
        }
    }

    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        handleAddPairAlertSideEffect(effect, navigator, resultNavigator)
    }

    Scaffold(
        topBar = {
            AppTopBarBack(
                title =
                    if (state.editExisting)
                        stringResource(CoreRString.alert_edit_alert)
                    else
                        stringResource(CoreRString.add_new_alert),
                onBackClick = { navigator.popBackStack() },
            )
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            Content(
                state = state,
                navigateSearchBase = viewModel::onNavigateSearchBase,
                navigateSearchTarget = viewModel::onNavigateSearchTarget,
                onGroupSelect = viewModel::onGroupSelect,
                onGroupCreate = viewModel::onGroupCreate,
                onPriceOrPercentChanged = viewModel::onPriceOrPercentChanged,
                onOneTimeChanged = viewModel::onOneTimeChanged,
                onPriceOrPercentInputChanged = viewModel::onPriceOrPercentInputChanged,
                onIncreaseToggle = viewModel::onIncreaseToggle,
                onSaveClick = viewModel::onSaveClick,
            )
        }
    }
}

@Composable
private fun Content(
    state: AddPairAlertScreenState,
    navigateSearchBase: () -> Unit,
    navigateSearchTarget: () -> Unit,
    onGroupSelect: (Group) -> Unit,
    onGroupCreate: (String) -> Unit,
    onPriceOrPercentChanged: (Boolean) -> Unit,
    onOneTimeChanged: (Boolean) -> Unit,
    onPriceOrPercentInputChanged: (String) -> Unit,
    onIncreaseToggle: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val ctx = LocalContext.current
    var showNewGroupDialog by remember { mutableStateOf(false) }
    var showGroupsPopup by remember { mutableStateOf(false) }
    var addGroupBtnWidth by remember { mutableStateOf(0) }

    if (showNewGroupDialog) {
        GroupCreateDialog(
            validateGroupNameUseCase =
                PairAlertComponentHolder.provide(ctx)
                    .validateGroupNameUseCase(),
            onDismiss = { showNewGroupDialog = false },
        ) {
            onGroupCreate(it)
        }
    }

    Column {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            PriceOrPercent(state, onPriceOrPercentChanged)
            EditCondition(
                state = state,
                navigateSearchBase = navigateSearchBase,
                navigateSearchTarget = navigateSearchTarget,
                onPriceOrPercentInputChanged = onPriceOrPercentInputChanged,
                onIncreaseToggle = onIncreaseToggle,
            )
            OneTimeOrRecurrent(
                state.priceOrPercent.isLeft(),
                state.oneTimeNotRecurrent,
                onOneTimeChanged,
            )
            DropDownWithIcon(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                        .onPlaced {
                            addGroupBtnWidth = it.size.width
                        },
                onClick = { showGroupsPopup = !showGroupsPopup },
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
                            onGroupSelect = onGroupSelect,
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
                onClick = onSaveClick,
                enabled = state.finishEnabled,
            ) {
                Text(
                    text =
                        if (state.editExisting)
                            stringResource(CoreRString.save)
                        else
                            stringResource(CoreRString.create_alert),
                )
            }
        }
    }
}

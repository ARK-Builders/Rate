@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.feature.portfolio.presentation.edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AmountInputVisualTransformation
import dev.arkbuilders.rate.core.presentation.ui.AppHorDiv
import dev.arkbuilders.rate.core.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.core.presentation.ui.ArkCursorLargeTextField
import dev.arkbuilders.rate.core.presentation.ui.InfoDialog
import dev.arkbuilders.rate.core.presentation.ui.LoadingScreen
import dev.arkbuilders.rate.core.presentation.ui.calculateEndPadding
import dev.arkbuilders.rate.core.presentation.ui.calculateStartPadding
import dev.arkbuilders.rate.feature.portfolio.di.PortfolioComponentHolder
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Destination<ExternalModuleGraph>
@Composable
fun EditAssetScreen(
    assetId: Long,
    navigator: DestinationsNavigator,
) {
    val ctx = LocalContext.current
    val component =
        remember {
            PortfolioComponentHolder.provide(ctx)
        }

    val viewModel: EditAssetViewModel =
        viewModel(
            factory = component.editAssetVMFactory().create(assetId),
        )

    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            EditAssetScreenEffect.NavigateBack -> navigator.popBackStack()
        }
    }

    BackHandler {
        viewModel.onBackClick()
    }

    Scaffold(
        topBar = {
            AppTopBarBack(
                title = stringResource(CoreRString.asset_detail),
                onBackClick = { viewModel.onBackClick() },
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { contentPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.initialized) {
                Content(
                    navigator = navigator,
                    info = state.info,
                    value = state.value,
                    contentPadding = contentPadding,
                    onValueChange = viewModel::onValueChange,
                )
            } else {
                LoadingScreen(
                    Modifier
                        .padding(contentPadding)
                        .consumeWindowInsets(contentPadding),
                )
            }
        }
    }
}

@Composable
private fun Content(
    navigator: DestinationsNavigator,
    info: CurrencyInfo,
    value: String,
    contentPadding: PaddingValues,
    onValueChange: (String) -> Unit,
) {
    var showMarketCapitalizationDialog by remember { mutableStateOf(false) }
    var showValueOfCirculatingDialog by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val layoutDirection = LocalLayoutDirection.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    if (showMarketCapitalizationDialog) {
        InfoDialog(
            title = stringResource(id = CoreRString.info_dialog_market_capitalization),
            desc = stringResource(id = CoreRString.info_dialog_market_capitalization_description),
            onDismiss = { showMarketCapitalizationDialog = false },
        )
    }

    if (showValueOfCirculatingDialog) {
        InfoDialog(
            title = stringResource(id = CoreRString.info_dialog_value_of_circulating),
            desc = stringResource(id = CoreRString.info_dialog_value_of_circulating_description),
            onDismiss = { showValueOfCirculatingDialog = false },
        )
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .consumeWindowInsets(contentPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = contentPadding.calculateStartPadding(layoutDirection) + 16.dp,
                    top = contentPadding.calculateTopPadding(),
                    end = contentPadding.calculateEndPadding(layoutDirection) + 16.dp,
                    bottom = contentPadding.calculateBottomPadding(),
                ),
    ) {
        val title =
            if (info.name.isNotEmpty()) {
                "${info.name} (${info.code})"
            } else {
                info.code
            }
        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = title,
            color = ArkColor.TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
        )
        AppHorDiv(modifier = Modifier.padding(top = 21.dp))
        Row(
            Modifier.padding(top = 32.dp),
            verticalAlignment = Alignment.Top,
        ) {
            ArkCursorLargeTextField(
                modifier =
                    Modifier
                        .weight(1f, fill = false)
                        .align(Alignment.CenterVertically)
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                keyboardController?.show()
                            }
                        },
                value = value,
                onValueChange = { onValueChange(it) },
                visualTransformation = AmountInputVisualTransformation,
            )
            Text(
                modifier =
                    Modifier
                        .padding(start = 2.dp, top = 2.dp)
                        .align(Alignment.Top),
                text = CurrUtils.getSymbolOrCode(info.code),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = ArkColor.TextPrimary,
            )
        }
    }
}

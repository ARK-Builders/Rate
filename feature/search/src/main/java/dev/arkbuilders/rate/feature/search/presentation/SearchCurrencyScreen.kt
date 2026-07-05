@file:OptIn(ExperimentalMaterial3Api::class)

package dev.arkbuilders.rate.feature.search.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.result.ResultBackNavigator
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.AppHorDiv
import dev.arkbuilders.rate.core.presentation.ui.AppTopBarBack
import dev.arkbuilders.rate.core.presentation.ui.CurrIcon
import dev.arkbuilders.rate.core.presentation.ui.InfoDialog
import dev.arkbuilders.rate.core.presentation.ui.ListHeader
import dev.arkbuilders.rate.core.presentation.ui.LoadingScreen
import dev.arkbuilders.rate.core.presentation.ui.NoResult
import dev.arkbuilders.rate.core.presentation.ui.SearchTextField
import dev.arkbuilders.rate.feature.search.di.SearchComponentHolder
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Destination<ExternalModuleGraph>
@Composable
fun SearchCurrencyScreen(
    title: String? = null,
    navKey: String? = null,
    navPos: Int? = null,
    prohibitedCodes: Array<CurrencyCode>? = null,
    resultNavigator: ResultBackNavigator<SearchNavResult>,
    externalNavigator: SearchExternalNavigator,
) {
    val ctx = LocalContext.current
    val component =
        remember {
            SearchComponentHolder.provide(ctx)
        }
    val viewModel: SearchViewModel =
        viewModel(
            factory =
                component.searchVMFactory()
                    .create(navKey, navPos, prohibitedCodes?.toList()),
        )
    val state by viewModel.collectAsState()

    BackHandler {
        viewModel.onBackClick()
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is SearchScreenEffect.NavigateBackWithResult ->
                resultNavigator.navigateBack(effect.result)

            SearchScreenEffect.NavigateBack -> resultNavigator.navigateBack()

            SearchScreenEffect.NavigateToPaywall -> externalNavigator.navigateToPaywall()
        }
    }

    if (state.showCodeProhibitedDialog) {
        InfoDialog(
            title = stringResource(CoreRString.search_currency_already_selected),
            desc = stringResource(CoreRString.search_currency_already_selected_desc),
            onDismiss = viewModel::onCodeProhibitedDialogDismiss,
        )
    }

    Scaffold(
        topBar = {
            AppTopBarBack(
                title = title ?: stringResource(CoreRString.search_currency),
                onBackClick = { viewModel.onBackClick() },
            )
        },
    ) {
        Column(modifier = Modifier.padding(it)) {
            if (state.initialized) {
                Input(state.filter, viewModel::onInputChange)
                Results(
                    filter = state.filter,
                    prohibitedCodes = state.prohibitedCodes,
                    frequent = state.frequent,
                    all = state.all,
                    topResultsFiltered = state.topResultsFiltered,
                    onClick = viewModel::onClick,
                    onTryPremiumClick = viewModel::onTryPremiumClick,
                )
            } else {
                LoadingScreen()
            }
        }
    }
}

@Composable
private fun Input(
    input: String,
    onInputChange: (String) -> Unit,
) {
    SearchTextField(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        text = input,
        onValueChange = { onInputChange(it) },
    )
    AppHorDiv()
}

@Composable
private fun Results(
    filter: String,
    prohibitedCodes: List<CurrencyCode>,
    frequent: List<CurrencyInfo>,
    all: List<CurrencyInfo>,
    topResultsFiltered: List<CurrencyInfo>,
    onClick: (CurrencyInfo) -> Unit,
    onTryPremiumClick: () -> Unit,
) {
    when {
        filter.isNotEmpty() -> {
            if (topResultsFiltered.isNotEmpty()) {
                LazyColumn {
                    item { ListHeader(stringResource(CoreRString.top_results)) }
                    items(topResultsFiltered) { model ->
                        SearchCurrencyInfoItem(
                            model,
                            model.code in prohibitedCodes,
                        ) { onClick(it) }
                    }
                }
            } else {
                NoResult()
            }
        }

        else -> {
            LazyColumn {
                item { CryptoPremiumBanner(onTryPremiumClick) }
                if (frequent.isNotEmpty()) {
                    item { ListHeader(stringResource(CoreRString.frequent_currencies)) }
                    items(frequent) { model ->
                        SearchCurrencyInfoItem(
                            model,
                            model.code in prohibitedCodes,
                        ) { onClick(it) }
                    }
                }
                item { ListHeader(stringResource(CoreRString.all_currencies)) }
                items(all) { model ->
                    SearchCurrencyInfoItem(
                        model,
                        model.code in prohibitedCodes,
                    ) { onClick(it) }
                }
            }
        }
    }
}

@Composable
private fun CryptoPremiumBanner(onTryPremiumClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .padding(start = 16.dp, end = 16.dp, top = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFFF1E8FF),
                            Color(0xFFE8ECFF),
                        ),
                    ),
                )
                .border(
                    border = BorderStroke(1.dp, Color(0xFFD8C7FF)),
                    shape = RoundedCornerShape(12.dp),
                ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CryptoTokenStack()
            Text(
                modifier =
                    Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .weight(1f),
                text = "500+ Crypto Tokens",
                color = Color(0xFF3B1688),
                fontSize = 14.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                fontWeight = FontWeight.SemiBold,
            )
            Button(
                modifier = Modifier.height(36.dp),
                onClick = onTryPremiumClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D3DF5)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) {
                Text(
                    text = "Try Premium",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun CryptoTokenStack() {
    val iconCodes = listOf("BTC", "ETH", "SOL")

    Box(
        modifier =
            Modifier
                .width(88.dp)
                .height(32.dp),
    ) {
        repeat(4) { index ->
            Box(
                modifier =
                    Modifier
                        .offset(x = (index * 20).dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(
                            border = BorderStroke(1.dp, ArkColor.BorderSecondary),
                            shape = CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                iconCodes.getOrNull(index)?.let { code ->
                    CurrIcon(
                        modifier = Modifier.size(24.dp),
                        code = code,
                    )
                }
            }
        }
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE7DDFF))
                    .border(
                        border = BorderStroke(1.dp, Color.White),
                        shape = CircleShape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+500",
                color = Color(0xFF6D3DF5),
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

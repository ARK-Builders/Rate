package dev.arkbuilders.rate.watchapp.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Text
import dev.arkbuilders.rate.core.presentation.theme.ArkColor
import dev.arkbuilders.rate.core.presentation.ui.SearchTextField
import dev.arkbuilders.rate.watchapp.presentation.quickpairs.composables.CurrIcon

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onCurrencyClick: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    ScalingLazyColumn(
        modifier = modifier.fillMaxSize().background(Color.White),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            SearchTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                text = state.query,
                onValueChange = { viewModel.onQueryChange(it) }
            )
        }

        items(state.filteredCurrencies.size) { index ->
            val currency = state.filteredCurrencies[index]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCurrencyClick(currency.code) }
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                CurrIcon(modifier = Modifier.size(24.dp), code = currency.code)
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = currency.code,
                        color = ArkColor.TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = currency.name,
                        color = ArkColor.TextTertiary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

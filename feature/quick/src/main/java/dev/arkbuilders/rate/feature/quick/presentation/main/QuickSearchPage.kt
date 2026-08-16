package dev.arkbuilders.rate.feature.quick.presentation.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.ui.CurrencyInfoItem
import dev.arkbuilders.rate.core.presentation.ui.ListHeader
import dev.arkbuilders.rate.core.presentation.ui.NoResult

@Composable
fun QuickSearchPage(
    topResultsFiltered: List<CurrencyInfo>,
    onNewCode: (CurrencyCode) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    if (topResultsFiltered.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
        ) {
            item {
                ListHeader(text = stringResource(CoreRString.top_results))
            }
            items(topResultsFiltered) { name ->
                CurrencyInfoItem(name) { onNewCode(it.code) }
            }
        }
    } else {
        NoResult(
            Modifier
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding),
        )
    }
}

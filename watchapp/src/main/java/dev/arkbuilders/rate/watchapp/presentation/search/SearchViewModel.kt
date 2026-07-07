package dev.arkbuilders.rate.watchapp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.usecase.SearchUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(
    val query: String = "",
    val allCurrencies: List<CurrencyInfo> = emptyList(),
    val filteredCurrencies: List<CurrencyInfo> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val currencyRepo: CurrencyRepo,
    private val searchUseCase: SearchUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val all = currencyRepo.getCurrencyInfo()
            _state.update {
                it.copy(
                    allCurrencies = all,
                    filteredCurrencies = all,
                    isLoading = false,
                )
            }
        }
    }

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
        filter()
    }

    private fun filter() {
        val currentState = _state.value
        val filtered =
            searchUseCase(
                currentState.allCurrencies,
                emptyList(),
                currentState.query,
            )
        _state.update { it.copy(filteredCurrencies = filtered) }
    }
}

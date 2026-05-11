package dev.arkbuilders.rate.watchapp.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val currencyRepo: CurrencyRepo,
    ): ViewModel() {

    init {
        viewModelScope.launch {
            currencyRepo.initialize()
            launch {
                currencyRepo.getCurrencyRates()
            }
        }
    }
}

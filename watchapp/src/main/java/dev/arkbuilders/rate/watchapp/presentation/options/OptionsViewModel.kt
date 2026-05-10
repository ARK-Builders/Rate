package dev.arkbuilders.rate.watchapp.presentation.options

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arkbuilders.rate.feature.quick.domain.model.QuickPair
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OptionsViewModel @Inject constructor(
    private val quickRepo: QuickRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pairId: Long = checkNotNull(savedStateHandle["id"])

    private val _quickPair = MutableStateFlow<QuickPair?>(null)
    val quickPair: StateFlow<QuickPair?> = _quickPair.asStateFlow()

    init {
        viewModelScope.launch {
            // Find the pair from the repo
            // QuickRepo doesn't have a getPair(id) that returns a flow of one item in the current interface
            // We can collect the list and find it.
            quickRepo.allFlow().collect { pairs ->
                _quickPair.value = pairs.find { it.id == pairId }
            }
        }
    }

    fun deletePair(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _quickPair.value?.let { p ->
                quickRepo.delete(p.id)
                onDeleted()
            }
        }
    }
}

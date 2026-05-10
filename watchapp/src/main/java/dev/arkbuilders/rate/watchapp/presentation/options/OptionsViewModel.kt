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
import dev.arkbuilders.rate.watchapp.watchface.WatchRefreshManager
import javax.inject.Inject

import android.app.Application

@HiltViewModel
class OptionsViewModel @Inject constructor(
    private val application: Application,
    private val quickRepo: QuickRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val pairId: Long = checkNotNull(savedStateHandle["id"])

    private val _quickPair = MutableStateFlow<QuickPair?>(null)
    val quickPair: StateFlow<QuickPair?> = _quickPair.asStateFlow()

    private val _showPinLimitDialog = MutableStateFlow(false)
    val showPinLimitDialog: StateFlow<Boolean> = _showPinLimitDialog.asStateFlow()

    init {
        viewModelScope.launch {
            quickRepo.allFlow().collect { pairs ->
                _quickPair.value = pairs.find { it.id == pairId }
            }
        }
    }

    fun togglePin(onSuccess: (Boolean) -> Unit) {
        viewModelScope.launch {
            val pair = _quickPair.value ?: return@launch
            if (pair.isPinned()) {
                // Unpin
                val updated = pair.copy(pinnedDate = null)
                quickRepo.insert(updated)
                WatchRefreshManager.refreshComplications(application)
                onSuccess(false)
            } else {
                // Check limit
                val pinnedCount = quickRepo.getAll().count { it.isPinned() }
                if (pinnedCount >= 4) {
                    _showPinLimitDialog.value = true
                } else {
                    // Pin
                    val updated = pair.copy(pinnedDate = java.time.OffsetDateTime.now())
                    quickRepo.insert(updated)
                    WatchRefreshManager.refreshComplications(application)
                    onSuccess(true)
                }
            }
        }
    }

    fun dismissPinLimitDialog() {
        _showPinLimitDialog.value = false
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

package dev.arkbuilders.rate.watchapp.presentation.quickpairs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.feature.quick.domain.model.QuickPair
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class QuickPairsViewModel @Inject constructor(
    private val quickRepo: QuickRepo,
    private val convertUseCase: ConvertWithRateUseCase,
) : ViewModel() {

    val quickPairs: StateFlow<List<QuickPair>> = quickRepo.allFlow()
        .map { pairs ->
            pairs.map { pair ->
                val actualTo = pair.to.map { toAmount ->
                    val (convertedAmt, _) = convertUseCase.invoke(pair.from, pair.amount, toAmount.code)
                    convertedAmt
                }
                pair.copy(to = actualTo)
            }.sortedByDescending { it.calculatedDate }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deletePair(pair: QuickPair) {
        viewModelScope.launch {
            quickRepo.delete(pair.id)
        }
    }
}

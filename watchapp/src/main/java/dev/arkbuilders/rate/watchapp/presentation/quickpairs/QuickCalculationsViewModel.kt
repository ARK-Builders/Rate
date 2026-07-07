package dev.arkbuilders.rate.watchapp.presentation.quickpairs

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arkbuilders.rate.core.domain.model.TimestampType
import dev.arkbuilders.rate.core.domain.repo.TimestampRepo
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.feature.quick.domain.model.QuickCalculation
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class QuickCalculationsViewModel @Inject constructor(
    private val application: Application,
    private val quickRepo: QuickRepo,
    private val timestampRepo: TimestampRepo,
    private val convertUseCase: ConvertWithRateUseCase,
) : ViewModel() {
    val quickCalculations: StateFlow<List<QuickCalculation>> =
        combine(
            quickRepo.allFlow(),
            timestampRepo.timestampFlow(TimestampType.FetchRates),
        ) { pairs, refreshDate ->
            pairs.map { pair ->
                if (pair.isPinned()) {
                    val actualTo =
                        pair.to.map { toAmount ->
                            val (convertedAmt, _) =
                                convertUseCase.invoke(
                                    pair.from,
                                    pair.amount,
                                    toAmount.code,
                                )
                            convertedAmt
                        }
                    pair.copy(
                        to = actualTo,
                        calculatedDate = refreshDate ?: pair.calculatedDate,
                    )
                } else {
                    pair
                }
            }.sortedWith(
                compareByDescending<QuickCalculation> { it.isPinned() }
                    .thenByDescending { it.calculatedDate },
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )
}

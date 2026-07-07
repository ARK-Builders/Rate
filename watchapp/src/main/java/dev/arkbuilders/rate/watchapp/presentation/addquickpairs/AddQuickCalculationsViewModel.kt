package dev.arkbuilders.rate.watchapp.presentation.addquickpairs

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.toBigDecimalArk
import dev.arkbuilders.rate.core.domain.toDoubleArk
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.core.domain.usecase.GetGroupByIdOrCreateDefaultUseCase
import dev.arkbuilders.rate.feature.quick.domain.model.QuickCalculation
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import dev.arkbuilders.rate.watchapp.watchface.WatchRefreshManager
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

data class AddQuickState(
    val baseCurrency: String = "USD",
    val baseAmount: String = "",
    val targetCurrency: String = "EUR",
    val targetAmount: String = "",
    val isSaved: Boolean = false,
    val editId: Long? = null,
    val pinnedDate: OffsetDateTime? = null,
)

@HiltViewModel
class AddQuickCalculationsViewModel @Inject constructor(
    private val application: Application,
    private val quickRepo: QuickRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    private val getGroupByIdOrCreateDefaultUseCase: GetGroupByIdOrCreateDefaultUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _state = MutableStateFlow(AddQuickState())
    val state: StateFlow<AddQuickState> = _state.asStateFlow()

    init {
        val idStr: String? = savedStateHandle["id"]
        val id = idStr?.toLongOrNull()
        if (id != null) {
            viewModelScope.launch {
                quickRepo.allFlow().collect { pairs ->
                    pairs.find { it.id == id }?.let { pair ->
                        _state.value =
                            _state.value.copy(
                                baseCurrency = pair.from,
                                baseAmount = CurrUtils.roundOff(pair.amount),
                                targetCurrency = pair.to.firstOrNull()?.code ?: "EUR",
                                targetAmount =
                                    CurrUtils.roundOff(
                                        pair.to.firstOrNull()?.value ?: java.math.BigDecimal.ZERO,
                                    ),
                                editId = id,
                                pinnedDate = pair.pinnedDate,
                            )
                    }
                }
            }
        } else {
            calculate(fromBase = true)
        }
    }

    fun onBaseCurrencyChanged(code: String) {
        _state.value = _state.value.copy(baseCurrency = code)
        calculate(fromBase = true)
    }

    fun onTargetCurrencyChanged(code: String) {
        _state.value = _state.value.copy(targetCurrency = code)
        calculate(fromBase = true)
    }

    fun onAmountInput(input: String) {
        val newAmount = CurrUtils.validateInput(_state.value.baseAmount, input)
        _state.value = _state.value.copy(baseAmount = newAmount)
        calculate(fromBase = true)
    }

    fun onTargetAmountInput(input: String) {
        val newAmount = CurrUtils.validateInput(_state.value.targetAmount, input)
        _state.value = _state.value.copy(targetAmount = newAmount)
        calculate(fromBase = false)
    }

    fun onSwap() {
        val currentState = _state.value
        _state.value =
            currentState.copy(
                baseCurrency = currentState.targetCurrency,
                targetCurrency = currentState.baseCurrency,
                baseAmount = currentState.targetAmount,
                targetAmount = currentState.baseAmount,
            )
        calculate(fromBase = true)
    }

    private fun calculate(fromBase: Boolean) {
        viewModelScope.launch {
            val s = _state.value
            if (fromBase) {
                if (s.baseAmount.isEmpty() || s.baseAmount.toDoubleArk() == 0.0) {
                    _state.value = s.copy(targetAmount = "")
                    return@launch
                }
                val (amount, _) =
                    convertUseCase.invoke(
                        Amount(s.baseCurrency, s.baseAmount.toBigDecimalArk()),
                        s.targetCurrency,
                    )
                _state.value = _state.value.copy(targetAmount = CurrUtils.roundOff(amount.value))
            } else {
                if (s.targetAmount.isEmpty() || s.targetAmount.toDoubleArk() == 0.0) {
                    _state.value = s.copy(baseAmount = "")
                    return@launch
                }
                val (amount, _) =
                    convertUseCase.invoke(
                        Amount(s.targetCurrency, s.targetAmount.toBigDecimalArk()),
                        s.baseCurrency,
                    )
                _state.value = _state.value.copy(baseAmount = CurrUtils.roundOff(amount.value))
            }
        }
    }

    fun savePair() {
        viewModelScope.launch {
            val s = _state.value
            val group = getGroupByIdOrCreateDefaultUseCase(null, GroupFeatureType.Quick)
            val quick =
                QuickCalculation(
                    id = s.editId ?: 0,
                    from = s.baseCurrency,
                    amount = s.baseAmount.toBigDecimalArk(),
                    to = listOf(Amount(s.targetCurrency, s.targetAmount.toBigDecimalArk())),
                    calculatedDate = OffsetDateTime.now(),
                    pinnedDate = s.pinnedDate,
                    group = group,
                )
            quickRepo.insert(quick)
            if (quick.isPinned()) {
                WatchRefreshManager.refreshComplications(application)
            }
            _state.value = s.copy(isSaved = true)
        }
    }
}

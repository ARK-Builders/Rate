package dev.arkbuilders.rate.watchapp.presentation.addquickpairs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arkbuilders.rate.core.domain.CurrUtils
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.core.domain.usecase.GetGroupByIdOrCreateDefaultUseCase
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.feature.quick.domain.model.QuickPair
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import dev.arkbuilders.rate.core.domain.toBigDecimalArk
import dev.arkbuilders.rate.core.domain.toDoubleArk
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

data class AddQuickState(
    val baseCurrency: String = "USD",
    val targetCurrency: String = "EUR",
    val baseAmount: String = "",
    val targetAmount: String = "",
    val isSaved: Boolean = false
)

@HiltViewModel
class AddQuickPairsViewModel @Inject constructor(
    private val quickRepo: QuickRepo,
    private val convertUseCase: ConvertWithRateUseCase,
    private val getGroupByIdOrCreateDefaultUseCase: GetGroupByIdOrCreateDefaultUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddQuickState())
    val state: StateFlow<AddQuickState> = _state.asStateFlow()

    init {
        calculate()
    }

    fun onBaseCurrencyChanged(code: String) {
        _state.value = _state.value.copy(baseCurrency = code)
        calculate()
    }

    fun onTargetCurrencyChanged(code: String) {
        _state.value = _state.value.copy(targetCurrency = code)
        calculate()
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
        _state.value = currentState.copy(
            baseCurrency = currentState.targetCurrency,
            targetCurrency = currentState.baseCurrency
        )
        calculate(fromBase = true)
    }

    private fun calculate(fromBase: Boolean = true) {
        viewModelScope.launch {
            val s = _state.value
            val sourceAmount = if (fromBase) s.baseAmount else s.targetAmount
            val sourceCurrency = if (fromBase) s.baseCurrency else s.targetCurrency
            val destCurrency = if (fromBase) s.targetCurrency else s.baseCurrency

            if (sourceAmount.isEmpty() || sourceAmount.toDoubleArk() == 0.0) {
                if (fromBase) {
                    _state.value = s.copy(targetAmount = "")
                } else {
                    _state.value = s.copy(baseAmount = "")
                }
                return@launch
            }
            
            val (amount, _) = convertUseCase.invoke(
                Amount(sourceCurrency, sourceAmount.toBigDecimalArk()), 
                destCurrency
            )
            val roundValue = CurrUtils.roundOff(amount.value)
            if (fromBase) {
                _state.value = _state.value.copy(targetAmount = roundValue)
            } else {
                _state.value = _state.value.copy(baseAmount = roundValue)
            }
        }
    }

    fun savePair() {
        viewModelScope.launch {
            val s = _state.value
            val group = getGroupByIdOrCreateDefaultUseCase(null, GroupFeatureType.Quick)
            val quick = QuickPair(
                id = 0,
                from = s.baseCurrency,
                amount = s.baseAmount.toBigDecimalArk(),
                to = listOf(Amount(s.targetCurrency, s.targetAmount.toBigDecimalArk())),
                calculatedDate = OffsetDateTime.now(),
                pinnedDate = null,
                group = group
            )
            quickRepo.insert(quick)
            _state.value = s.copy(isSaved = true)
        }
    }
}

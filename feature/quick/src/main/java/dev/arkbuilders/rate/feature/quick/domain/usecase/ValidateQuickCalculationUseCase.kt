package dev.arkbuilders.rate.feature.quick.domain.usecase

import dev.arkbuilders.rate.core.domain.model.AmountStr
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.toDoubleArk
import javax.inject.Inject

class ValidateQuickCalculationUseCase @Inject constructor(
    private val analyticsManager: AnalyticsManager,
) {
    operator fun invoke(
        currencies: List<AmountStr>,
        sendAnalyticsEvent: Boolean = false,
    ): Boolean {
        val from = currencies.firstOrNull()
        val isValid =
            from != null &&
                currencies.size > 1 &&
                from.value.toDoubleArk() != 0.0

        if (isValid.not() && sendAnalyticsEvent) {
            analyticsManager.logEvent("add_quick_invalid_calculation_attempt")
        }

        return isValid
    }
}

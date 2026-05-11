package dev.arkbuilders.rate.core.data.mapper

import dev.arkbuilders.rate.core.data.network.dto.FiatRateResponse
import dev.arkbuilders.rate.core.domain.divideArk
import dev.arkbuilders.rate.core.domain.model.CurrencyRate
import dev.arkbuilders.rate.core.domain.model.CurrencyType
import java.math.BigDecimal
import javax.inject.Inject
class FiatRateResponseMapper @Inject constructor() {
    fun map(response: FiatRateResponse): List<CurrencyRate> =
        response.rates.map { (code, rate) ->
            CurrencyRate(
                CurrencyType.FIAT,
                code.uppercase(),
                BigDecimal.ONE.divideArk(BigDecimal.valueOf(rate)),
            )
        }
}

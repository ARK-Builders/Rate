package dev.arkbuilders.rate.core.data.mapper

import dev.arkbuilders.rate.core.data.network.dto.CryptoRateResponse
import dev.arkbuilders.rate.core.domain.model.CurrencyRate
import dev.arkbuilders.rate.core.domain.model.CurrencyType
import java.math.BigDecimal
import javax.inject.Inject
class CryptoRateResponseMapper @Inject constructor() {
    fun map(response: List<CryptoRateResponse>) =
        response.map {
            CurrencyRate(
                CurrencyType.CRYPTO,
                it.symbol.uppercase(),
                BigDecimal.valueOf(it.current_price),
            )
        }
}

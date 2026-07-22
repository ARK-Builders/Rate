package dev.arkbuilders.rate.core.domain.repo

import arrow.core.Either
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo
import dev.arkbuilders.rate.core.domain.model.CurrencyRate

interface CurrencyRepo {
    suspend fun initialize()

    suspend fun updateRates(): Either<Throwable, List<CurrencyRate>>

    suspend fun getCurrencyRates(): List<CurrencyRate>

    suspend fun getCurrencyInfo(): List<CurrencyInfo>

    suspend fun infoByCode(code: CurrencyCode): CurrencyInfo

    suspend fun getCodeToCurrencyRate(): Map<CurrencyCode, CurrencyRate> =
        getCurrencyRates().associateBy { it.code }
}

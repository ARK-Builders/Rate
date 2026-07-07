package dev.arkbuilders.rate.core.data.repo.currency

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dev.arkbuilders.rate.core.data.mapper.FiatRateResponseMapper
import dev.arkbuilders.rate.core.data.network.api.FiatAPI
import dev.arkbuilders.rate.core.domain.model.CurrencyRate
import dev.arkbuilders.rate.core.domain.model.CurrencyType
import javax.inject.Inject

class FiatCurrencyDataSource @Inject constructor(
    private val fiatAPI: FiatAPI,
    private val fiatRateResponseMapper: FiatRateResponseMapper,
) : CurrencyDataSource {
    override val currencyType = CurrencyType.FIAT

    override suspend fun fetchRemote(): Either<Throwable, List<CurrencyRate>> {
        return try {
            val response = fiatAPI.get()
            fiatRateResponseMapper.map(response).right()
        } catch (e: Throwable) {
            e.left()
        }
    }
}

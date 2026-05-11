package dev.arkbuilders.rate.core.data.repo.currency

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dev.arkbuilders.rate.core.domain.AppConfig
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo
import dev.arkbuilders.rate.core.domain.model.CurrencyRate
import dev.arkbuilders.rate.core.domain.model.TimestampType
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.NetworkStatus
import dev.arkbuilders.rate.core.domain.repo.TimestampRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.OffsetDateTime
import javax.inject.Inject

class CurrencyRepoImpl @Inject constructor(
    private val fiatDataSource: FiatCurrencyDataSource,
    private val cryptoDataSource: CryptoCurrencyDataSource,
    private val localCurrencyDataSource: LocalCurrencyDataSource,
    private val fallbackRatesProvider: FallbackRatesProvider,
    private val currencyInfoDataSource: CurrencyInfoDataSource,
    private val timestampRepo: TimestampRepo,
    private val networkStatus: NetworkStatus,
) : CurrencyRepo {
    private val updateRatesMutex = Mutex()
    private val loadInfoMutex = Mutex()
    private var infoMap: Map<CurrencyCode, CurrencyInfo> = emptyMap()

    override suspend fun initialize() {
        loadInfo()
    }

    override suspend fun getCurrencyRates(): List<CurrencyRate> =
        withContext(Dispatchers.IO) {
            val local = localCurrencyDataSource.getAll()
            if (local.isNotEmpty()) {
                launch(Job()) { updateRates() }
                return@withContext local
            } else {
                val remoteRates = updateRates()
                if (remoteRates.isRight())
                    return@withContext remoteRates.getOrNull()!!

                val fallbackRates = useFallbackRates()
                return@withContext fallbackRates
            }
        }

    override suspend fun getCurrencyInfo(): List<CurrencyInfo> {
        val rates = getCurrencyRates()

        infoMap.ifEmpty { loadInfo() }

        val ratesInfo =
            rates.map { rate ->
                infoMap[rate.code] ?: CurrencyInfo.emptyWithCode(rate.code)
            }

        return ratesInfo.sortedBy { it.code }
    }

    override suspend fun infoByCode(code: CurrencyCode): CurrencyInfo {
        infoMap.ifEmpty { loadInfo() }

        return infoMap[code] ?: CurrencyInfo.emptyWithCode(code)
    }

    private suspend fun updateRates(): Either<Throwable, List<CurrencyRate>> =
        updateRatesMutex.withLock {
            val updatedDate =
                timestampRepo
                    .getTimestamp(TimestampType.FetchRates)

            if ((networkStatus.isOnline() && hasUpdateIntervalPassed(updatedDate)).not()) {
                return IllegalStateException("Skip rate updates").left()
            }

            val crypto = cryptoDataSource.fetchRemote().onLeft { return@withLock it.left() }
            val fiat = fiatDataSource.fetchRemote().onLeft { return@withLock it.left() }
            val rates = crypto.getOrNull()!! + fiat.getOrNull()!!
            localCurrencyDataSource.insert(rates)
            timestampRepo.rememberTimestamp(TimestampType.FetchRates)
            return@withLock rates.right()
        }

    private suspend fun useFallbackRates(): List<CurrencyRate> {
        val (rates, fetchDate) = fallbackRatesProvider.provideRatesAndFetchDate()
        localCurrencyDataSource.insert(rates)
        timestampRepo.rememberTimestamp(TimestampType.FetchRates, fetchDate)
        return rates
    }

    private suspend fun loadInfo() =
        loadInfoMutex.withLock {
            if (infoMap.isNotEmpty()) return@withLock

            infoMap = currencyInfoDataSource.provide()
        }

    private fun hasUpdateIntervalPassed(updatedDate: OffsetDateTime?) =
        updatedDate == null ||
            Duration.between(updatedDate, OffsetDateTime.now())
                .toMillis() > updateInterval

    private val updateInterval =
        Duration.ofHours(AppConfig.CURRENCY_RATES_UPDATE_INTERVAL_HOURS).toMillis()
}

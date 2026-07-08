package dev.arkbuilders.rate.core.data.network.remote

import dev.arkbuilders.rate.core.data.network.client.NetworkConfig
import dev.arkbuilders.rate.core.data.network.dto.CryptoRateResponse
import dev.arkbuilders.rate.core.data.network.dto.FiatRateResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatesApiClient @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun getCryptoRates(): List<CryptoRateResponse> =
        client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = NetworkConfig.HOST
                encodedPath = NetworkConfig.CRYPTO_RATES_PATH
            }
        }.body()

    suspend fun getFiatRates(): FiatRateResponse =
        client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = NetworkConfig.HOST
                encodedPath = NetworkConfig.FIAT_RATES_PATH
            }
        }.body()

    suspend fun getUpdatedAt(): String =
        client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = NetworkConfig.HOST
                encodedPath = NetworkConfig.UPDATED_AT_PATH
            }
        }.bodyAsText()
}

@file:Suppress("ktlint")

package dev.arkbuilders.rate.core.data.network.api

import dev.arkbuilders.rate.core.data.network.dto.CryptoRateResponse
import retrofit2.http.GET

interface CryptoAPI {
    @GET("/ARK-Builders/ARK-Rate/refs/heads/data/currency-icons-and-rates/core/data/src/main/assets/crypto-rates.json")
    suspend fun getCryptoRates(): List<CryptoRateResponse>
}

package dev.arkbuilders.rate.core.data.network.dto

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CryptoRateResponse(
    val symbol: String,
    @SerialName("current_price")
    val currentPrice: Double,
)

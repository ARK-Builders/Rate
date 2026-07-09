package dev.arkbuilders.rate.core.data.network.dto

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class FiatRateResponse(
    val timestamp: Long,
    val rates: Map<String, Double>,
)

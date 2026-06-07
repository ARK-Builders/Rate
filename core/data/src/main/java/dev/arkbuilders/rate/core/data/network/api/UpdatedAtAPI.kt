@file:Suppress("ktlint")

package dev.arkbuilders.rate.core.data.network.api

import okhttp3.ResponseBody
import retrofit2.http.GET

interface UpdatedAtAPI {
    @GET("/ARK-Builders/ARK-Rate/refs/heads/data/currency-icons-and-rates/core/data/src/main/assets/updatedAt")
    suspend fun get(): ResponseBody
}

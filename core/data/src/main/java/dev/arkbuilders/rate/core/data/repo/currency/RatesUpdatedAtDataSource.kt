package dev.arkbuilders.rate.core.data.repo.currency

import android.content.Context
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import dev.arkbuilders.rate.core.data.network.api.UpdatedAtAPI
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class RatesUpdatedAtDataSource(
    private val ctx: Context,
    private val updatedAtAPI: UpdatedAtAPI,
) {
    suspend fun fetchRemote(): Either<Throwable, OffsetDateTime> =
        try {
            parseUnixTimestamp(updatedAtAPI.get().string()).right()
        } catch (e: Throwable) {
            e.left()
        }

    fun fetchBundled(): OffsetDateTime {
        val updatedAt =
            ctx.assets.open(UPDATED_AT_FILE).bufferedReader().use {
                it.readText()
            }
        return parseUnixTimestamp(updatedAt)
    }

    private fun parseUnixTimestamp(value: String): OffsetDateTime {
        val timestamp = value.trim().toLong()
        return Instant.ofEpochSecond(timestamp).atOffset(ZoneOffset.UTC)
    }

    companion object {
        private const val UPDATED_AT_FILE = "updatedAt"
    }
}

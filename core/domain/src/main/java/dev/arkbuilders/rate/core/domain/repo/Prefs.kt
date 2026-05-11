package dev.arkbuilders.rate.core.domain.repo

import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

sealed class PreferenceKey<out T>(val defaultValue: T) {
    data object CollectCrashReports : PreferenceKey<Boolean>(true)

    data object CollectAnalytics : PreferenceKey<Boolean>(true)

    data object BaseCurrencyCode : PreferenceKey<CurrencyCode>("USD")

    data object IsOnboardingCompleted : PreferenceKey<Boolean>(false)

    data object IsOnboardingQuickCalculationCompleted : PreferenceKey<Boolean>(false)

    data object AppLaunchCount : PreferenceKey<Long>(0)

    data object FirstInstallVersionCode : PreferenceKey<Int?>(null)

    data object CurrentVersionCode : PreferenceKey<Int?>(null)

    data object InAppReviewAttemptCount : PreferenceKey<Int>(0)

    data object LastInAppReviewTimestamp : PreferenceKey<String?>(null)
}

interface Prefs {
    suspend fun <T> get(key: PreferenceKey<T>): T

    suspend fun <T> set(
        key: PreferenceKey<T>,
        value: T,
    )

    fun <T> flow(key: PreferenceKey<T>): Flow<T>

    suspend fun incrementAppLaunchCount()

    suspend fun getLastInAppReviewTimestamp(): OffsetDateTime?

    suspend fun setLastInAppReviewTimestamp(date: OffsetDateTime)
}

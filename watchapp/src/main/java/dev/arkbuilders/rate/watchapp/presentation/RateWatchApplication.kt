package dev.arkbuilders.rate.watchapp.presentation

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

import dev.arkbuilders.rate.core.domain.BuildConfigFields
import dev.arkbuilders.rate.core.domain.BuildConfigFieldsProvider
import dev.arkbuilders.rate.watchapp.BuildConfig
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject

@HiltAndroidApp
class RateWatchApplication: Application() {
    @Inject
    lateinit var buildConfigFieldsProvider: BuildConfigFieldsProvider

    override fun onCreate() {
        super.onCreate()
        initBuildConfigFields()
    }

    private fun initBuildConfigFields() {
        val fallbackCryptoRatesFetchDate =
            Instant.ofEpochMilli(BuildConfig.CRYPTO_LAST_MODIFIED).atOffset(ZoneOffset.UTC)
        val fallbackFiatRatesFetchDate =
            Instant.ofEpochMilli(BuildConfig.FIAT_LAST_MODIFIED).atOffset(ZoneOffset.UTC)

        buildConfigFieldsProvider.init(
            BuildConfigFields(
                buildType = BuildConfig.BUILD_TYPE,
                versionCode = BuildConfig.VERSION_CODE,
                versionName = BuildConfig.VERSION_NAME,
                isGooglePlayBuild = false, // Default to false for watch app for now
                fallbackCryptoRatesFetchDate = fallbackCryptoRatesFetchDate,
                fallbackFiatRatesFetchDate = fallbackFiatRatesFetchDate,
                availableIconCodes = BuildConfig.ICON_CODES.toSet(),
            ),
        )
    }
}

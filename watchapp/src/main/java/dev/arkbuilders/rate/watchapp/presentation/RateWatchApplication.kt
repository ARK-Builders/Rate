package dev.arkbuilders.rate.watchapp.presentation

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.arkbuilders.rate.core.domain.BuildConfigFields
import dev.arkbuilders.rate.core.domain.BuildConfigFieldsProvider
import dev.arkbuilders.rate.watchapp.BuildConfig
import javax.inject.Inject

@HiltAndroidApp
class RateWatchApplication : Application() {
    @Inject
    lateinit var buildConfigFieldsProvider: BuildConfigFieldsProvider

    override fun onCreate() {
        super.onCreate()
        initBuildConfigFields()
    }

    private fun initBuildConfigFields() {
        buildConfigFieldsProvider.init(
            BuildConfigFields(
                buildType = BuildConfig.BUILD_TYPE,
                versionCode = BuildConfig.VERSION_CODE,
                versionName = BuildConfig.VERSION_NAME,
                // Default to false for watch app for now
                isGooglePlayBuild = false,
                availableIconCodes = BuildConfig.ICON_CODES.toSet(),
            ),
        )
    }
}

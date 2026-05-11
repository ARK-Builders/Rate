package dev.arkbuilders.rate.presentation

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dev.arkbuilders.rate.BuildConfig
import dev.arkbuilders.rate.core.di.CoreComponent
import dev.arkbuilders.rate.core.di.CoreComponentProvider
import dev.arkbuilders.rate.core.di.DaggerCoreComponent
import dev.arkbuilders.rate.core.domain.AppConfig
import dev.arkbuilders.rate.core.domain.BuildConfigFields
import dev.arkbuilders.rate.core.domain.repo.PreferenceKey
import dev.arkbuilders.rate.feature.quickwidget.worker.QuickCalculationsWidgetRefreshWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

class App : Application(), Configuration.Provider, CoreComponentProvider {
    lateinit var coreComponent: CoreComponent

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        coreComponent = DaggerCoreComponent.factory().create(this, applicationContext)
        initBuildConfigFields()
        instance = this

        initCrashlytics()
        initWorker(
            QuickCalculationsWidgetRefreshWorker::class.java,
            QuickCalculationsWidgetRefreshWorker.NAME,
        )
    }

    private fun initBuildConfigFields() {
        val fallbackCryptoRatesFetchDate =
            Instant.ofEpochMilli(BuildConfig.CRYPTO_LAST_MODIFIED).atOffset(ZoneOffset.UTC)
        val fallbackFiatRatesFetchDate =
            Instant.ofEpochMilli(BuildConfig.FIAT_LAST_MODIFIED).atOffset(ZoneOffset.UTC)
        coreComponent.buildConfigFieldsProvider().init(
            BuildConfigFields(
                buildType = BuildConfig.BUILD_TYPE,
                versionCode = BuildConfig.VERSION_CODE,
                versionName = BuildConfig.VERSION_NAME,
                isGooglePlayBuild = BuildConfig.GOOGLE_PLAY_BUILD,
                fallbackCryptoRatesFetchDate = fallbackCryptoRatesFetchDate,
                fallbackFiatRatesFetchDate = fallbackFiatRatesFetchDate,
                availableIconCodes = BuildConfig.ICON_CODES.toSet(),
            ),
        )
    }

    private fun initCrashlytics() =
        CoroutineScope(Dispatchers.IO).launch {
            // Google Play will collect crashes in any case, so we will also send them to Firebase
            val collect =
                if (BuildConfig.GOOGLE_PLAY_BUILD)
                    true
                else
                    coreComponent.prefs().get(PreferenceKey.CollectCrashReports)

            Firebase.crashlytics.setCrashlyticsCollectionEnabled(collect)
        }

    private fun initWorker(
        workerClass: Class<out ListenableWorker?>,
        workerName: String,
    ) {
        val workManager = WorkManager.getInstance(this)
        val constraints =
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val workRequest =
            PeriodicWorkRequest.Builder(
                workerClass,
                AppConfig.CURRENCY_RATES_UPDATE_INTERVAL_HOURS,
                TimeUnit.HOURS,
            ).setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            workerName,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest,
        )
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(AppWorkerFactory())
            .build()

    override fun provideCoreComponent() = coreComponent

    companion object {
        lateinit var instance: App
    }
}

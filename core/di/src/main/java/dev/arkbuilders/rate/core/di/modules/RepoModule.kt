package dev.arkbuilders.rate.core.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dev.arkbuilders.rate.core.data.network.NetworkStatusImpl
import dev.arkbuilders.rate.core.data.preferences.PrefsImpl
import dev.arkbuilders.rate.core.data.repo.AnalyticsManagerImpl
import dev.arkbuilders.rate.core.data.repo.BuildConfigFieldsProviderImpl
import dev.arkbuilders.rate.core.data.repo.CodeUseStatRepoImpl
import dev.arkbuilders.rate.core.data.repo.GooglePlayInAppReviewManagerImpl
import dev.arkbuilders.rate.core.data.repo.GroupRepoImpl
import dev.arkbuilders.rate.core.data.repo.TimestampRepoImpl
import dev.arkbuilders.rate.core.data.repo.currency.CryptoCurrencyDataSource
import dev.arkbuilders.rate.core.data.repo.currency.CurrencyInfoDataSource
import dev.arkbuilders.rate.core.data.repo.currency.CurrencyRepoImpl
import dev.arkbuilders.rate.core.data.repo.currency.FallbackRatesProvider
import dev.arkbuilders.rate.core.data.repo.currency.FiatCurrencyDataSource
import dev.arkbuilders.rate.core.data.repo.currency.LocalCurrencyDataSource
import dev.arkbuilders.rate.core.data.repo.currency.RatesUpdatedAtDataSource
import dev.arkbuilders.rate.core.db.dao.CodeUseStatDao
import dev.arkbuilders.rate.core.db.dao.GroupDao
import dev.arkbuilders.rate.core.db.dao.TimestampDao
import dev.arkbuilders.rate.core.domain.BuildConfigFieldsProvider
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.core.domain.repo.CodeUseStatRepo
import dev.arkbuilders.rate.core.domain.repo.CurrencyRepo
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.core.domain.repo.InAppReviewManager
import dev.arkbuilders.rate.core.domain.repo.NetworkStatus
import dev.arkbuilders.rate.core.domain.repo.Prefs
import dev.arkbuilders.rate.core.domain.repo.TimestampRepo
import dev.arkbuilders.rate.core.domain.usecase.DefaultGroupNameProvider
import dev.arkbuilders.rate.core.presentation.utils.DefaultGroupNameProviderImpl
import javax.inject.Singleton

@Module
class RepoModule {
    @Singleton
    @Provides
    fun currencyRepo(
        fiatCurrencyDataSource: FiatCurrencyDataSource,
        cryptoCurrencyDataSource: CryptoCurrencyDataSource,
        localCurrencyDataSource: LocalCurrencyDataSource,
        currencyInfoDataSource: CurrencyInfoDataSource,
        timestampRepo: TimestampRepo,
        networkStatus: NetworkStatus,
        fallbackRatesProvider: FallbackRatesProvider,
        ratesUpdatedAtDataSource: RatesUpdatedAtDataSource,
    ): CurrencyRepo =
        CurrencyRepoImpl(
            fiatCurrencyDataSource,
            cryptoCurrencyDataSource,
            localCurrencyDataSource,
            fallbackRatesProvider,
            currencyInfoDataSource,
            timestampRepo,
            networkStatus,
            ratesUpdatedAtDataSource,
        )

    @Singleton
    @Provides
    fun groupRepo(groupDao: GroupDao): GroupRepo = GroupRepoImpl(groupDao)

    @Singleton
    @Provides
    fun prefs(context: Context): Prefs = PrefsImpl(context)

    @Singleton
    @Provides
    fun codeUseStatRepo(codeUseStatDao: CodeUseStatDao): CodeUseStatRepo =
        CodeUseStatRepoImpl(codeUseStatDao)

    @Singleton
    @Provides
    fun analyticsManager(prefs: Prefs): AnalyticsManager = AnalyticsManagerImpl(prefs)

    @Singleton
    @Provides
    fun timestampRepo(timestampDao: TimestampDao): TimestampRepo = TimestampRepoImpl(timestampDao)

    @Singleton
    @Provides
    fun networkStatus(context: Context): NetworkStatus = NetworkStatusImpl(context)

    @Singleton
    @Provides
    fun buildConfigFieldsProvider(): BuildConfigFieldsProvider = BuildConfigFieldsProviderImpl()

    @Singleton
    @Provides
    fun defaultGroupNameProvider(context: Context): DefaultGroupNameProvider =
        DefaultGroupNameProviderImpl(context)

    @Singleton
    @Provides
    fun inAppReviewManager(
        analyticsManager: AnalyticsManager,
        buildConfigFieldsProvider: BuildConfigFieldsProvider,
    ): InAppReviewManager =
        GooglePlayInAppReviewManagerImpl(
            analyticsManager,
            buildConfigFieldsProvider.provide(),
        )
}

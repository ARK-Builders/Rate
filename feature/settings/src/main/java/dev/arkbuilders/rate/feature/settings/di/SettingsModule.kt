package dev.arkbuilders.rate.feature.settings.di

import dagger.Module
import dagger.Provides
import dev.arkbuilders.rate.feature.settings.data.AppLanguageRepoImpl
import dev.arkbuilders.rate.feature.settings.domain.repository.AppLanguageRepo

@Module
class SettingsModule {
    @SettingsScope
    @Provides
    fun languageRepo(impl: AppLanguageRepoImpl): AppLanguageRepo = impl
}

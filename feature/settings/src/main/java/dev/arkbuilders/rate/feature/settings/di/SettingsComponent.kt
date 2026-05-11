package dev.arkbuilders.rate.feature.settings.di

import dagger.Component
import dev.arkbuilders.rate.core.di.CoreComponent
import dev.arkbuilders.rate.core.domain.BuildConfigFieldsProvider
import dev.arkbuilders.rate.core.domain.repo.TimestampRepo
import dev.arkbuilders.rate.feature.settings.presentation.SettingsViewModelFactory

@SettingsScope
@Component(dependencies = [CoreComponent::class], modules = [SettingsModule::class])
interface SettingsComponent {
    fun settingsVMFactory(): SettingsViewModelFactory

    fun buildConfigFieldsProvider(): BuildConfigFieldsProvider

    fun timestampRepo(): TimestampRepo
}

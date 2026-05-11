package dev.arkbuilders.rate.feature.settings.domain.repository

import dev.arkbuilders.rate.feature.settings.domain.model.AppLanguage

interface AppLanguageRepo {
    fun getLanguage(): AppLanguage

    suspend fun setLanguage(language: AppLanguage)
}

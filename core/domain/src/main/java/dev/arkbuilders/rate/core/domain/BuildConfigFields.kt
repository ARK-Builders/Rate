package dev.arkbuilders.rate.core.domain

import dev.arkbuilders.rate.core.domain.model.CurrencyCode

data class BuildConfigFields(
    val buildType: String,
    val versionCode: Int,
    val versionName: String,
    val isGooglePlayBuild: Boolean,
    val availableIconCodes: Set<CurrencyCode>,
)

interface BuildConfigFieldsProvider {
    fun init(fields: BuildConfigFields)

    fun provide(): BuildConfigFields
}

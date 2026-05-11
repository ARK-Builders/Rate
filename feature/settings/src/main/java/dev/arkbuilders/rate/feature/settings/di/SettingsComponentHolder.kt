package dev.arkbuilders.rate.feature.settings.di

import android.content.Context
import dev.arkbuilders.rate.core.di.CoreComponentProvider

object SettingsComponentHolder {
    private var component: SettingsComponent? = null

    fun provide(ctx: Context): SettingsComponent {
        component ?: let {
            val app = ctx.applicationContext
            val coreComponent = (app as CoreComponentProvider).provideCoreComponent()
            component =
                DaggerSettingsComponent.builder().coreComponent(coreComponent).build()
        }
        return component!!
    }
}

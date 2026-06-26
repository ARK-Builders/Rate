package dev.arkbuilders.rate.feature.paywall.di

import android.content.Context
import dev.arkbuilders.rate.core.di.CoreComponentProvider

object PaywallComponentHolder {
    private var component: PaywallComponent? = null

    fun provide(ctx: Context): PaywallComponent {
        component ?: let {
            val app = ctx.applicationContext
            val coreComponent = (app as CoreComponentProvider).provideCoreComponent()
            component =
                DaggerPaywallComponent.builder().coreComponent(coreComponent).build()
        }
        return component!!
    }
}

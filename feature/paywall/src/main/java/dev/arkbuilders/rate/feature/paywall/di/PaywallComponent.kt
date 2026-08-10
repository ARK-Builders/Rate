package dev.arkbuilders.rate.feature.paywall.di

import dagger.Component
import dev.arkbuilders.rate.core.di.CoreComponent
import dev.arkbuilders.rate.core.domain.repo.PremiumManager
import dev.arkbuilders.rate.feature.paywall.PaywallViewModelFactory

@PaywallScope
@Component(dependencies = [CoreComponent::class], modules = [])
interface PaywallComponent {
    fun paywallVMFactory(): PaywallViewModelFactory

    fun premiumManager(): PremiumManager
}

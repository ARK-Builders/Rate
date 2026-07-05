package dev.arkbuilders.rate.core.domain.repo

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface PremiumManager {
    val premiumState: StateFlow<Boolean>

    suspend fun refresh()

    suspend fun purchase(
        activity: Activity,
        productId: String,
    ): Boolean

    fun isPremium(): Boolean = premiumState.value

    companion object {
        const val TEST_YEARLY_PRODUCT_ID = "test_product_yearly"
        const val TEST_MONTHLY_PRODUCT_ID = "test_product_monthly"
    }
}

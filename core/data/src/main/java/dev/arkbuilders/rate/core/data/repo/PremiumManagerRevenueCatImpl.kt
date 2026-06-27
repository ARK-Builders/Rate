package dev.arkbuilders.rate.core.data.repo

import android.app.Activity
import android.content.Context
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.ProductType
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitGetProducts
import com.revenuecat.purchases.awaitPurchase
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import dev.arkbuilders.rate.core.domain.BuildConfigFieldsProvider
import dev.arkbuilders.rate.core.domain.repo.PremiumManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class PremiumManagerRevenueCatImpl(
    private val context: Context,
    private val buildConfigFieldsProvider: BuildConfigFieldsProvider,
) : PremiumManager {
    private val _premiumState = MutableStateFlow(false)

    override val premiumState: StateFlow<Boolean> = _premiumState.asStateFlow()

    init {
        configureRevenueCatIfNeeded()
        setCustomerInfoListener()
    }

    override suspend fun refresh() {
        if (Purchases.isConfigured.not()) {
            Timber.d("RevenueCat refresh skipped: SDK is not configured")
            updatePremium(isPremium = false)
            return
        }

        runCatching {
            Purchases.sharedInstance.awaitCustomerInfo()
        }.onSuccess { customerInfo ->
            updateCustomerInfo(customerInfo)
        }.onFailure { error ->
            Timber.d("RevenueCat customer info refresh failed: ${error.message}")
        }
    }

    override suspend fun purchase(
        activity: Activity,
        productId: String,
    ): Boolean {
        if (Purchases.isConfigured.not()) {
            Timber.d("RevenueCat purchase skipped: SDK is not configured")
            return false
        }

        val product =
            runCatching {
                Purchases.sharedInstance
                    .awaitGetProducts(
                        productIds = listOf(productId),
                        type = ProductType.SUBS,
                    ).firstOrNull()
            }.onFailure { error ->
                Timber.d("RevenueCat product fetch failed: ${error.message}")
            }.getOrNull()
                ?: return false

        return runCatching {
            Purchases.sharedInstance.awaitPurchase(
                PurchaseParams.Builder(activity, product).build(),
            )
        }.onSuccess { result ->
            updateCustomerInfo(result.customerInfo)
        }.onFailure { error ->
            Timber.d("RevenueCat purchase failed: ${error.message}")
        }.isSuccess
    }

    private fun configureRevenueCatIfNeeded() {
        if (Purchases.isConfigured)
            return

        val buildConfigFields = buildConfigFieldsProvider.provide()
        val apiKey = buildConfigFields.revenueCatApiKey
        if (apiKey.isBlank()) {
            Timber.d("RevenueCat configure skipped: API key is empty")
            return
        }

        Purchases.logLevel =
            if (buildConfigFields.buildType == BUILD_TYPE_DEBUG)
                LogLevel.DEBUG
            else
                LogLevel.INFO

        Purchases.configure(
            PurchasesConfiguration.Builder(context, apiKey).build(),
        )
    }

    private fun setCustomerInfoListener() {
        if (Purchases.isConfigured.not())
            return

        Purchases.sharedInstance.updatedCustomerInfoListener =
            UpdatedCustomerInfoListener { customerInfo ->
                updateCustomerInfo(customerInfo)
            }
    }

    private fun updateCustomerInfo(customerInfo: CustomerInfo) {
        updatePremium(
            isPremium =
                customerInfo
                    .entitlements[PREMIUM_ENTITLEMENT_ID]
                    ?.isActive == true,
        )
    }

    private fun updatePremium(isPremium: Boolean) {
        _premiumState.value = isPremium
    }

    private companion object {
        const val PREMIUM_ENTITLEMENT_ID = "premium"
        const val BUILD_TYPE_DEBUG = "debug"
    }
}

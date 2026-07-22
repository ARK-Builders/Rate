package dev.arkbuilders.rate.core.data.network.client

internal object NetworkConfig {
    const val HOST = "raw.githubusercontent.com"

    private const val RATES_ASSETS_PATH =
        "/ARK-Builders/ARK-Rate/refs/heads/data/currency-icons-and-rates" +
            "/core/data/src/main/assets"

    const val CRYPTO_RATES_PATH = "$RATES_ASSETS_PATH/crypto-rates.json"
    const val FIAT_RATES_PATH = "$RATES_ASSETS_PATH/fiat-rates.json"
    const val UPDATED_AT_PATH = "$RATES_ASSETS_PATH/updatedAt"
}

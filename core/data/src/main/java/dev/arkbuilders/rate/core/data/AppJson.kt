package dev.arkbuilders.rate.core.data

import kotlinx.serialization.json.Json

internal val appJson =
    Json {
        ignoreUnknownKeys = true
    }

package dev.arkbuilders.rate.core.data.network.client

import dev.arkbuilders.rate.core.domain.BuildConfigFieldsProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.gson.gson
import timber.log.Timber
import javax.inject.Inject

class KtorHttpClientFactory @Inject constructor(
    private val buildConfigFieldsProvider: BuildConfigFieldsProvider,
) {
    fun create(): HttpClient {
        val buildConfigFields = buildConfigFieldsProvider.provide()

        return HttpClient(OkHttp) {
            expectSuccess = true

            install(ContentNegotiation) {
                gson()
            }

            if (buildConfigFields.buildType == DEBUG_BUILD_TYPE) {
                install(Logging) {
                    logger =
                        object : Logger {
                            override fun log(message: String) {
                                Timber.d(message)
                            }
                        }
                    level = LogLevel.ALL
                }
            }
        }
    }

    companion object {
        private const val DEBUG_BUILD_TYPE = "debug"
    }
}

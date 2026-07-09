package dev.arkbuilders.rate.core.data.network.client

import dev.arkbuilders.rate.core.data.appJson
import dev.arkbuilders.rate.core.domain.BuildConfigFieldsProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
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
                json(appJson)
            }

            if (buildConfigFields.buildType == DEBUG_BUILD_TYPE) {
                install(Logging) {
                    logger =
                        object : Logger {
                            override fun log(message: String) {
                                Timber.d(message)
                            }
                        }
                    level = LogLevel.INFO
                }
            }
        }
    }

    companion object {
        private const val DEBUG_BUILD_TYPE = "debug"
    }
}

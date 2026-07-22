package dev.arkbuilders.rate.core.di.modules

import dagger.Module
import dagger.Provides
import dev.arkbuilders.rate.core.data.network.client.KtorHttpClientFactory
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
class ApiModule {
    @Singleton
    @Provides
    fun httpClient(factory: KtorHttpClientFactory): HttpClient = factory.create()
}

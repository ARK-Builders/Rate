package dev.arkbuilders.rate.watchapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.arkbuilders.rate.core.data.network.client.KtorHttpClientFactory
import dev.arkbuilders.rate.core.domain.BuildConfigFields
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {
    @Singleton
    @Provides
    fun httpClient(
        buildConfigFields: BuildConfigFields,
    ): HttpClient = KtorHttpClientFactory(buildConfigFields).create()
}

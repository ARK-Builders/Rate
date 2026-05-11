package dev.arkbuilders.rate.watchapp.di

import android.content.Context
import android.webkit.WebSettings
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.arkbuilders.rate.core.data.network.OkHttpClientBuilder
import dev.arkbuilders.rate.core.data.network.api.CryptoAPI
import dev.arkbuilders.rate.core.data.network.api.FiatAPI
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Singleton
    @Provides
    fun clientBuilder(@ApplicationContext context: Context): OkHttpClient {
        val client =
            OkHttpClient.Builder()
                .addNetworkInterceptor { chain ->
                    chain.proceed(
                        chain.request()
                            .newBuilder()
                            .build(),
                    )
                }
                .build()

        return client
    }

    @Singleton
    @Provides
    fun cryptoAPI(clientBuilder: OkHttpClient): CryptoAPI {
        val httpClient = clientBuilder
        val gson = GsonBuilder().create()

        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient)
            .build()
            .create(CryptoAPI::class.java)
    }

    @Singleton
    @Provides
    fun fiatAPI(clientBuilder: OkHttpClient): FiatAPI {
        val httpClient = clientBuilder
        val gson = GsonBuilder().create()

        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient)
            .build()
            .create(FiatAPI::class.java)
    }
}

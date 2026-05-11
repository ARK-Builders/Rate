package dev.arkbuilders.rate.core.di.modules

import android.content.Context
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dev.arkbuilders.rate.core.data.network.OkHttpClientBuilder
import dev.arkbuilders.rate.core.data.network.api.CryptoAPI
import dev.arkbuilders.rate.core.data.network.api.FiatAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApiModule {
    @Singleton
    @Provides
    fun okHttpClientBuilder(context: Context): OkHttpClientBuilder {
        return OkHttpClientBuilder(context)
    }

    @Singleton
    @Provides
    fun cryptoAPI(clientBuilder: OkHttpClientBuilder): CryptoAPI {
        val httpClient = clientBuilder.build()
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
    fun fiatAPI(clientBuilder: OkHttpClientBuilder): FiatAPI {
        val httpClient = clientBuilder.build()
        val gson = GsonBuilder().create()

        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient)
            .build()
            .create(FiatAPI::class.java)
    }
}

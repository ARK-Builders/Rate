package dev.arkbuilders.rate.core.data.repo.currency

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.arkbuilders.rate.core.data.R
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.CurrencyInfo
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CurrencyInfoDataSource @Inject constructor(
    private val ctx: Context,
) {
    suspend fun provide(): Map<CurrencyCode, CurrencyInfo> =
        withContext(Dispatchers.IO) {
            val nameDef: Deferred<Map<String, String>> =
                async {
                    val codeNameText =
                        ctx.resources.openRawResource(R.raw.code_name).bufferedReader().use {
                            it.readText()
                        }
                    val codeNameType = object : TypeToken<Map<String, String>>() {}.type
                    Gson().fromJson(codeNameText, codeNameType)
                }

            val countryDef: Deferred<Map<String, List<String>>> =
                async {
                    val codeCountryText =
                        ctx.resources.openRawResource(R.raw.code_country).bufferedReader().use {
                            it.readText()
                        }
                    val codeCountryType = object : TypeToken<Map<String, List<String>>>() {}.type
                    Gson().fromJson(codeCountryText, codeCountryType)
                }

            val codeToName = nameDef.await()
            val codeToCountry = countryDef.await()

            return@withContext codeToName.mapValues { (code, name) ->
                CurrencyInfo(code, name, codeToCountry[code] ?: emptyList())
            }
        }
}

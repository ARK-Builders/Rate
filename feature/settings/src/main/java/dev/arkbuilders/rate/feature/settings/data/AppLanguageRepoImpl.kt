package dev.arkbuilders.rate.feature.settings.data

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dev.arkbuilders.rate.feature.settings.domain.model.AppLanguage
import dev.arkbuilders.rate.feature.settings.domain.repository.AppLanguageRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class AppLanguageRepoImpl @Inject constructor(
    private val context: Context,
) : AppLanguageRepo {
    override fun getLanguage(): AppLanguage {
        val locale =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.getSystemService(LocaleManager::class.java)
                    ?.applicationLocales
                    ?.get(0)
            } else {
                AppCompatDelegate.getApplicationLocales().get(0)
            }
        return locale?.let { mapLocaleToAppLanguage(it) } ?: AppLanguage.SYSTEM
    }

    override suspend fun setLanguage(language: AppLanguage) =
        withContext(Dispatchers.Main) {
            if (language == AppLanguage.SYSTEM) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.getSystemService(LocaleManager::class.java).applicationLocales =
                        LocaleList.getEmptyLocaleList()
                } else {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
                }
                return@withContext
            }

            val code = language.code
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.getSystemService(LocaleManager::class.java)
                    .applicationLocales = LocaleList.forLanguageTags(code)
            } else {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
            }
        }

    private fun mapLocaleToAppLanguage(locale: Locale): AppLanguage {
        AppLanguage
            .entries
            .find { it.code.equals(locale.language, ignoreCase = true) }
            ?.let { return it }

        return AppLanguage.SYSTEM
    }
}

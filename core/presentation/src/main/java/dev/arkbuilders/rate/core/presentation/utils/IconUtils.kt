package dev.arkbuilders.rate.core.presentation.utils

import android.content.Context
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.presentation.R

object IconUtils {
    fun iconForCurrCode(
        ctx: Context,
        code: CurrencyCode,
    ): Int {
        var lowercaseCode = code.lowercase()
        // try is reserved work, but we have Turkish lira
        if (lowercaseCode == "try") {
            lowercaseCode = "curr_try"
        }

        val resourceName =
            if (lowercaseCode.firstOrNull()?.isLetter() == false) {
                "coin_$lowercaseCode"
            } else {
                lowercaseCode
            }

        val drawableID =
            ctx.resources
                .getIdentifier(
                    resourceName,
                    "drawable",
                    ctx.packageName,
                )

        return if (drawableID > 0)
            drawableID
        else
            R.drawable.ic_earth
    }
}

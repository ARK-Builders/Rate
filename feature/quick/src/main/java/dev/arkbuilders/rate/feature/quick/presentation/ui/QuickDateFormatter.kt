package dev.arkbuilders.rate.feature.quick.presentation.ui

import android.content.Context
import dev.arkbuilders.rate.core.presentation.CoreRString
import dev.arkbuilders.rate.core.presentation.utils.DateFormatUtils
import java.time.Duration
import java.time.OffsetDateTime

object QuickDateFormatter {
    fun calculationCalculatedTime(
        ctx: Context,
        date: OffsetDateTime,
    ): String {
        val now = OffsetDateTime.now()
        val dur = Duration.between(date, now)

        return when {
            dur.toDays() >= 7 ->
                ctx.getString(
                    CoreRString.quick_calculated_on,
                    DateFormatUtils.formatDateOnly(date),
                )

            dur.toDays() == 0L &&
                dur.toHours() == 0L &&
                dur.toMinutes() == 0L &&
                dur.seconds <= 5L -> ctx.getString(CoreRString.quick_calculated_just_now)

            else ->
                ctx.getString(
                    CoreRString.quick_calculated_ago,
                    DateFormatUtils.formatElapsedTime(ctx, now, date),
                )
        }
    }

    fun calculationRefreshedTime(
        ctx: Context,
        date: OffsetDateTime,
    ): String {
        val now = OffsetDateTime.now()
        val dur = Duration.between(date, now)

        return when {
            dur.toDays() == 0L &&
                dur.toHours() == 0L &&
                dur.toMinutes() == 0L &&
                dur.seconds <= 5L -> ctx.getString(CoreRString.quick_last_refreshed_now)

            else ->
                ctx.getString(
                    CoreRString.quick_last_refreshed_ago,
                    DateFormatUtils.formatElapsedTime(ctx, now, date),
                )
        }
    }
}

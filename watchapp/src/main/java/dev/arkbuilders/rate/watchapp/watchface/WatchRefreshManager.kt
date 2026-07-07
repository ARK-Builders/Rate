package dev.arkbuilders.rate.watchapp.watchface

import android.content.ComponentName
import android.content.Context
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester

object WatchRefreshManager {
    fun refreshComplications(context: Context) {
        try {
            val requester =
                ComplicationDataSourceUpdateRequester.create(
                    context,
                    ComponentName(context, QuickCalculationComplicationService::class.java),
                )
            requester.requestUpdateAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

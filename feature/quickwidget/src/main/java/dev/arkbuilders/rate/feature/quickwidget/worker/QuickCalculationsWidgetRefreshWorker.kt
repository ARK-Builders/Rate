package dev.arkbuilders.rate.feature.quickwidget.worker

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.arkbuilders.rate.feature.quickwidget.presentation.QuickCalculationsWidgetReceiver

class QuickCalculationsWidgetRefreshWorker(
    params: WorkerParameters,
    private val context: Context,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val intent =
            Intent(applicationContext, QuickCalculationsWidgetReceiver::class.java).apply {
                action = QuickCalculationsWidgetReceiver.PINNED_CALCULATIONS_REFRESH
            }
        applicationContext.sendBroadcast(intent)
        return Result.success()
    }

    companion object {
        const val NAME = "RatesRefreshWorker"
    }
}

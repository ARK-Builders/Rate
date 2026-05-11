package dev.arkbuilders.rate.feature.quickwidget.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

class QuickCalculationsWidgetRefreshWorkerFactory : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            QuickCalculationsWidgetRefreshWorker::class.java.name ->
                QuickCalculationsWidgetRefreshWorker(
                    params = workerParameters,
                    context = appContext,
                )

            else -> null
        }
    }
}

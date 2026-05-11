package dev.arkbuilders.rate.presentation

import androidx.work.DelegatingWorkerFactory
import dev.arkbuilders.rate.feature.quickwidget.worker.QuickCalculationsWidgetRefreshWorkerFactory

class AppWorkerFactory : DelegatingWorkerFactory() {
    init {
        addFactory(
            QuickCalculationsWidgetRefreshWorkerFactory(),
        )
    }
}

package dev.arkbuilders.rate.feature.quickwidget.presentation.action

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import dev.arkbuilders.rate.feature.quickwidget.di.QuickWidgetComponentHolder

class OpenAppAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        QuickWidgetComponentHolder.provide(context).analyticsManager()
            .logEvent("quick_widget_open_app_clicked")

        context.startActivity(
            Intent().apply {
                setClassName(context, "dev.arkbuilders.rate.presentation.MainActivity")
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }
}

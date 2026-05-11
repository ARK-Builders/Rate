package dev.arkbuilders.rate.feature.quickwidget.presentation

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import dev.arkbuilders.rate.feature.quickwidget.di.QuickWidgetComponentHolder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import timber.log.Timber

class QuickCalculationsWidgetReceiver : GlanceAppWidgetReceiver() {
    private val coroutineScope = MainScope()

    override val glanceAppWidget: GlanceAppWidget = QuickCalculationsWidget()

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        super.onReceive(context, intent)
        val action = intent.action
        Timber.d(action)
        when (action) {
            AppWidgetManager.ACTION_APPWIDGET_ENABLED, PINNED_CALCULATIONS_REFRESH ->
                updateAll(context)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        updateAll(context)
    }

    private fun updateAll(context: Context) {
        coroutineScope.launch {
            GlanceAppWidgetManager(context)
                .getGlanceIds(QuickCalculationsWidget::class.java)
                .forEach { glanceId ->
                    updateWidgetNewGroup(
                        context = context,
                        glanceId = glanceId,
                        findNewIndex = { currentIndex, _ ->
                            currentIndex ?: 0
                        },
                    )
                }
        }
    }

    companion object {
        val currentGroupIdKey = longPreferencesKey("currentGroupIdKey")
        const val PINNED_CALCULATIONS_REFRESH = "PINNED_CALCULATIONS_REFRESH"

        suspend fun updateWidgetNewGroup(
            context: Context,
            glanceId: GlanceId,
            findNewIndex: (currentIndex: Int?, lastIndex: Int) -> Int,
        ) {
            val quickRepo = QuickWidgetComponentHolder.provide(context).quickRepo()
            val allGroups = quickRepo.getAllGroups()
            updateAppWidgetState(context, glanceId) { prefs ->
                val currentId = prefs[currentGroupIdKey]
                var currentIndex: Int? =
                    allGroups.indexOfFirst { group ->
                        group.id == currentId
                    }
                if (currentIndex == -1)
                    currentIndex = null

                val newIndex = findNewIndex(currentIndex, allGroups.lastIndex)
                val newGroup = allGroups.getOrNull(newIndex)
                if (newGroup != null) {
                    prefs[currentGroupIdKey] = newGroup.id
                } else {
                    prefs.remove(currentGroupIdKey)
                }
            }
            QuickCalculationsWidget().update(context, glanceId)
        }
    }
}

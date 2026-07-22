package dev.arkbuilders.rate.core.data.repo

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager

class AnalyticsManagerImpl : AnalyticsManager {
    override fun trackScreen(name: String) {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, name)
        }
    }

    override fun logEvent(event: String) {
        Firebase.analytics.logEvent(event) {}
    }
}

package dev.arkbuilders.rate.feature.quick.domain.usecase

import android.app.Activity
import dev.arkbuilders.rate.core.domain.BuildConfigFields
import dev.arkbuilders.rate.core.domain.repo.InAppReviewManager
import dev.arkbuilders.rate.core.domain.repo.PreferenceKey
import dev.arkbuilders.rate.core.domain.repo.Prefs
import timber.log.Timber
import java.time.Duration
import java.time.OffsetDateTime
import javax.inject.Inject

class LaunchInAppReviewUseCase @Inject constructor(
    private val inAppReviewManager: InAppReviewManager,
    private val prefs: Prefs,
    private val buildConfigFields: BuildConfigFields,
) {
    companion object {
        private const val MIN_APP_LAUNCHES_FOR_REVIEW = 5
        private const val MAX_ATTEMPTS = 10
        private const val REVIEW_COOLDOWN_DAYS = 7L
    }

    suspend operator fun invoke(activity: Activity) {
        if (buildConfigFields.isGooglePlayBuild.not()) {
            Timber.d("In-app review skipped: not Google Play build")
            return
        }

        Timber.d("In-app review attempt started")

        val launchCount = prefs.get(PreferenceKey.AppLaunchCount)
        if (launchCount < MIN_APP_LAUNCHES_FOR_REVIEW) {
            Timber.d("In-app review skipped: launch count < $MIN_APP_LAUNCHES_FOR_REVIEW")
            return
        }

        val attemptCount = prefs.get(PreferenceKey.InAppReviewAttemptCount)
        if (attemptCount >= MAX_ATTEMPTS) {
            Timber.d("In-app review skipped: attempt count >= $MAX_ATTEMPTS")
            return
        }

        val now = OffsetDateTime.now()
        val lastShown = prefs.getLastInAppReviewTimestamp()
        if (lastShown != null && Duration.between(lastShown, now).toDays() < REVIEW_COOLDOWN_DAYS) {
            Timber.d(
                "In-app review skipped: cooldown $REVIEW_COOLDOWN_DAYS days not passed",
            )
            return
        }

        val shown = inAppReviewManager.launchReview(activity)
        if (shown) {
            prefs.set(PreferenceKey.InAppReviewAttemptCount, attemptCount + 1)
            prefs.setLastInAppReviewTimestamp(now)
        }
    }
}

package dev.arkbuilders.rate.feature.quick.domain.usecase

import dev.arkbuilders.rate.core.domain.repo.AnalyticsManager
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import javax.inject.Inject

class CleanupInvalidQuickCalculationsUseCase @Inject constructor(
    private val quickRepo: QuickRepo,
    private val analyticsManager: AnalyticsManager,
) {
    suspend operator fun invoke() {
        quickRepo.getAll()
            .filter { it.to.isEmpty() }
            .forEach { calculation ->
                if (quickRepo.delete(calculation.id)) {
                    analyticsManager.logEvent("quick_invalid_calculation_removed")
                }
            }
    }
}

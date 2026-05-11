package dev.arkbuilders.rate.feature.quick.domain.usecase

import dev.arkbuilders.rate.core.domain.usecase.ConvertWithRateUseCase
import dev.arkbuilders.rate.feature.quick.domain.model.PinnedQuickCalculation
import dev.arkbuilders.rate.feature.quick.domain.model.QuickCalculation
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import java.time.OffsetDateTime

class GetSortedPinnedQuickCalculationsUseCase(
    private val quickRepo: QuickRepo,
    private val convertUseCase: ConvertWithRateUseCase,
) {
    suspend operator fun invoke() =
        quickRepo.getAll()
            .filter { it.isPinned() }
            .map {
                mapCalculationToPinned(it)
            }
            .sortedByDescending { it.calculation.pinnedDate }

    private suspend fun mapCalculationToPinned(calc: QuickCalculation): PinnedQuickCalculation {
        val actualTo =
            calc.to.map { to ->
                val (amount, _) = convertUseCase.invoke(calc.from, calc.amount, to.code)
                amount
            }
        return PinnedQuickCalculation(
            calc,
            actualTo,
            OffsetDateTime.now(),
        )
    }
}

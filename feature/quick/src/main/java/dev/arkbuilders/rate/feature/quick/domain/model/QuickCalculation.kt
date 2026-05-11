package dev.arkbuilders.rate.feature.quick.domain.model

import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import dev.arkbuilders.rate.core.domain.model.Group
import java.math.BigDecimal
import java.time.OffsetDateTime

data class QuickCalculation(
    val id: Long,
    val from: CurrencyCode,
    val amount: BigDecimal,
    val to: List<Amount>,
    val calculatedDate: OffsetDateTime,
    val pinnedDate: OffsetDateTime?,
    val group: Group,
) {
    fun isPinned() = pinnedDate != null

    companion object {
        fun empty() =
            QuickCalculation(
                id = 0,
                from = "USD",
                amount = BigDecimal(10),
                to = listOf(),
                calculatedDate = OffsetDateTime.now(),
                pinnedDate = null,
                group = Group.empty(),
            )
    }
}

data class PinnedQuickCalculation(
    val calculation: QuickCalculation,
    val actualTo: List<Amount>,
    val refreshDate: OffsetDateTime,
)

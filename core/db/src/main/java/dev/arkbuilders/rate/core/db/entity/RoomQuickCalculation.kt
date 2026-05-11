package dev.arkbuilders.rate.core.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.model.CurrencyCode
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomGroup::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class RoomQuickCalculation(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val from: CurrencyCode,
    val amount: BigDecimal,
    val to: List<Amount>,
    val calculatedDate: OffsetDateTime,
    val pinnedDate: OffsetDateTime?,
    val groupId: Long,
)

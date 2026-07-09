package dev.arkbuilders.rate.core.db.typeconverters

import androidx.room.TypeConverter
import dev.arkbuilders.rate.core.domain.model.Amount
import dev.arkbuilders.rate.core.domain.model.AmountStr
import dev.arkbuilders.rate.core.domain.model.toAmount
import dev.arkbuilders.rate.core.domain.model.toStrAmount
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ListAmountTypeConverter {
    @TypeConverter
    fun fromListAmount(list: List<Amount>): String {
        val listStr = list.map { it.toStrAmount().toSerializableAmountStr() }
        return json.encodeToString(listStr)
    }

    @TypeConverter
    fun toListAmount(list: String): List<Amount> {
        return json.decodeFromString<List<SerializableAmountStr>>(list)
            .map { it.toAmountStr().toAmount() }
    }

    private val json =
        Json {
            ignoreUnknownKeys = true
        }

    @Serializable
    private data class SerializableAmountStr(
        val code: String,
        val value: String,
    )

    private fun AmountStr.toSerializableAmountStr() = SerializableAmountStr(code, value)

    private fun SerializableAmountStr.toAmountStr() = AmountStr(code, value)
}

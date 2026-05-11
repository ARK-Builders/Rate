package dev.arkbuilders.rate.core.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.arkbuilders.rate.core.db.entity.RoomQuickCalculation
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickCalculationDao {
    @Upsert
    suspend fun insert(quickCurrency: RoomQuickCalculation): Long

    @Query("SELECT * FROM RoomQuickCalculation WHERE id = :id")
    suspend fun getById(id: Long): RoomQuickCalculation?

    @Query("SELECT * FROM RoomQuickCalculation")
    suspend fun getAll(): List<RoomQuickCalculation>

    @Query("SELECT * FROM RoomQuickCalculation")
    fun allFlow(): Flow<List<RoomQuickCalculation>>

    @Query("DELETE FROM RoomQuickCalculation WHERE id = :id")
    suspend fun delete(id: Long): Int
}

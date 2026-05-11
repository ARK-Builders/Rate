package dev.arkbuilders.rate.feature.quick.domain.repo

import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.feature.quick.domain.model.QuickCalculation
import kotlinx.coroutines.flow.Flow

interface QuickRepo {
    suspend fun insert(quick: QuickCalculation): Long

    suspend fun getById(id: Long): QuickCalculation?

    suspend fun getAll(): List<QuickCalculation>

    suspend fun getAllGroups(): List<Group> = getAll().groupBy { it.group }.map { it.key }

    fun allFlow(): Flow<List<QuickCalculation>>

    suspend fun delete(id: Long): Boolean
}

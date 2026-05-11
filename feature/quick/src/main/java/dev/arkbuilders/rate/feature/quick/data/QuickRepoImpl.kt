package dev.arkbuilders.rate.feature.quick.data

import dev.arkbuilders.rate.core.db.dao.QuickCalculationDao
import dev.arkbuilders.rate.core.db.entity.RoomQuickCalculation
import dev.arkbuilders.rate.core.domain.model.Group
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import dev.arkbuilders.rate.core.domain.repo.GroupRepo
import dev.arkbuilders.rate.feature.quick.domain.model.QuickCalculation
import dev.arkbuilders.rate.feature.quick.domain.repo.QuickRepo
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuickRepoImpl @Inject constructor(
    private val dao: QuickCalculationDao,
    private val groupRepo: GroupRepo,
) : QuickRepo {
    override suspend fun insert(quick: QuickCalculation) = dao.insert(quick.toRoom())

    override suspend fun getById(id: Long): QuickCalculation? =
        dao.getById(
            id,
        )?.toQuickCalculation(groupRepo)

    override suspend fun getAll(): List<QuickCalculation> {
        val groups = groupRepo.getAllSorted(GroupFeatureType.Quick)
        return dao.getAll().map { roomCalculation ->
            val group = groups.find { it.id == roomCalculation.groupId }!!
            roomCalculation.toQuickCalculation(group)
        }
    }

    override fun allFlow() =
        dao.allFlow().map { list ->
            val groups = groupRepo.getAllSorted(GroupFeatureType.Quick)
            list.map { roomCalculation ->
                val group = groups.find { it.id == roomCalculation.groupId }!!
                roomCalculation.toQuickCalculation(group)
            }
        }

    override suspend fun delete(id: Long) = dao.delete(id) > 0
}

private fun QuickCalculation.toRoom() =
    RoomQuickCalculation(
        id,
        from,
        amount,
        to,
        calculatedDate,
        pinnedDate,
        group.id,
    )

private fun RoomQuickCalculation.toQuickCalculation(group: Group) =
    QuickCalculation(id, from, amount, to, calculatedDate, pinnedDate, group)

private suspend fun RoomQuickCalculation.toQuickCalculation(groupRepo: GroupRepo) =
    QuickCalculation(id, from, amount, to, calculatedDate, pinnedDate, groupRepo.getById(groupId))

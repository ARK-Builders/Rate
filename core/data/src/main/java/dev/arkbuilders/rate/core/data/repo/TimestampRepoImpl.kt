package dev.arkbuilders.rate.core.data.repo

import dev.arkbuilders.rate.core.db.dao.TimestampDao
import dev.arkbuilders.rate.core.db.entity.RoomFetchTimestamp
import dev.arkbuilders.rate.core.domain.model.TimestampType
import dev.arkbuilders.rate.core.domain.repo.TimestampRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class TimestampRepoImpl @Inject constructor(private val dao: TimestampDao) : TimestampRepo {
    override suspend fun rememberTimestamp(type: TimestampType) =
        dao.insert(
            RoomFetchTimestamp(
                type.name,
                OffsetDateTime.now(),
            ),
        )

    override suspend fun rememberTimestamp(
        type: TimestampType,
        manualDate: OffsetDateTime,
    ) = dao.insert(
        RoomFetchTimestamp(
            type.name,
            manualDate,
        ),
    )

    override suspend fun getTimestamp(type: TimestampType) =
        dao.getTimestamp(
            type.name,
        )?.timestamp

    override fun timestampFlow(type: TimestampType): Flow<OffsetDateTime?> =
        dao.timestampFlow(type.name).map {
            it?.timestamp
        }
}

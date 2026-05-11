package dev.arkbuilders.rate.core.db

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.arkbuilders.rate.core.db.dao.CodeUseStatDao
import dev.arkbuilders.rate.core.db.dao.CurrencyRateDao
import dev.arkbuilders.rate.core.db.dao.GroupDao
import dev.arkbuilders.rate.core.db.dao.PairAlertDao
import dev.arkbuilders.rate.core.db.dao.PortfolioDao
import dev.arkbuilders.rate.core.db.dao.QuickCalculationDao
import dev.arkbuilders.rate.core.db.dao.TimestampDao
import dev.arkbuilders.rate.core.db.entity.RoomAsset
import dev.arkbuilders.rate.core.db.entity.RoomCodeUseStat
import dev.arkbuilders.rate.core.db.entity.RoomCurrencyRate
import dev.arkbuilders.rate.core.db.entity.RoomFetchTimestamp
import dev.arkbuilders.rate.core.db.entity.RoomGroup
import dev.arkbuilders.rate.core.db.entity.RoomPairAlert
import dev.arkbuilders.rate.core.db.entity.RoomQuickCalculation
import dev.arkbuilders.rate.core.db.migration.MIGRATION_14_15
import dev.arkbuilders.rate.core.db.migration.MIGRATION_15_16
import dev.arkbuilders.rate.core.db.typeconverters.BigDecimalTypeConverter
import dev.arkbuilders.rate.core.db.typeconverters.ListAmountTypeConverter
import dev.arkbuilders.rate.core.db.typeconverters.OffsetDateTimeTypeConverter

@androidx.room.Database(
    entities = [
        RoomAsset::class,
        RoomCurrencyRate::class,
        RoomFetchTimestamp::class,
        RoomPairAlert::class,
        RoomQuickCalculation::class,
        RoomCodeUseStat::class,
        RoomGroup::class,
    ],
    version = 16,
    exportSchema = true,
)
@TypeConverters(
    BigDecimalTypeConverter::class,
    ListAmountTypeConverter::class,
    OffsetDateTimeTypeConverter::class,
)
abstract class Database : RoomDatabase() {
    abstract fun assetsDao(): PortfolioDao

    abstract fun rateDao(): CurrencyRateDao

    abstract fun fetchTimestampDao(): TimestampDao

    abstract fun pairAlertDao(): PairAlertDao

    abstract fun quickDao(): QuickCalculationDao

    abstract fun codeUseStatDao(): CodeUseStatDao

    abstract fun groupDao(): GroupDao

    companion object {
        const val DB_NAME = "arkrate.db"

        fun build(app: Application) =
            Room.databaseBuilder(app, Database::class.java, DB_NAME)
                .addMigrations(MIGRATION_14_15, MIGRATION_15_16)
                .build()
    }
}

@file:Suppress("ktlint")

package dev.arkbuilders.rate.core.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATION_15_16 =
    object : Migration(15, 16) {
        override fun migrate(db: SupportSQLiteDatabase) {
            renameQuickPairTableToQuickCalculation(db)
        }
    }

private fun renameQuickPairTableToQuickCalculation(db: SupportSQLiteDatabase) {
    db.execSQL(
        "ALTER TABLE RoomQuickPair RENAME TO RoomQuickCalculation",
    )
}

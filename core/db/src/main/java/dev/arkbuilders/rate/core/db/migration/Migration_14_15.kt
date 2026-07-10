@file:Suppress("ktlint")

package dev.arkbuilders.rate.core.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.arkbuilders.rate.core.db.typeconverters.OffsetDateTimeTypeConverter
import dev.arkbuilders.rate.core.domain.model.GroupFeatureType
import java.time.OffsetDateTime

internal val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(db: SupportSQLiteDatabase) {
        createGroupTable(db)
        addGroupIdColumnsToPairs(db)
        replaceNullGroupsWithDefault(db)
        extractGroupsAndInsertIntoGroupTable(db)
        setGroupIdInPairs(db)
        createFinalPairTables(db)
        migrateDataAndCleanup(db)
    }
}

private fun createGroupTable(db: SupportSQLiteDatabase) {
    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS RoomGroup (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            name TEXT NOT NULL,
            orderIndex INTEGER NOT NULL,
            creationTime TEXT NOT NULL,
            featureType TEXT NOT NULL
        )
        """
    )
}

private fun addGroupIdColumnsToPairs(db: SupportSQLiteDatabase) {
    db.execSQL("ALTER TABLE RoomAsset ADD COLUMN groupId INTEGER")
    db.execSQL("ALTER TABLE RoomQuickPair ADD COLUMN groupId INTEGER")
    db.execSQL("ALTER TABLE RoomPairAlert ADD COLUMN groupId INTEGER")
}

private fun replaceNullGroupsWithDefault(db: SupportSQLiteDatabase) {
    db.execSQL("UPDATE RoomAsset SET `group` = 'Default' WHERE `group` IS NULL")
    db.execSQL("UPDATE RoomQuickPair SET `group` = 'Default' WHERE `group` IS NULL")
    db.execSQL("UPDATE RoomPairAlert SET `group` = 'Default' WHERE `group` IS NULL")
}

private fun extractGroupsAndInsertIntoGroupTable(db: SupportSQLiteDatabase) {
    fun extractGroups(table: String): Set<String> {
        val groups = mutableSetOf<String>()
        val cursor = db.query("SELECT DISTINCT `group` FROM $table")
        while (cursor.moveToNext()) {
            groups.add(cursor.getString(0))
        }
        cursor.close()
        return groups
    }

    fun insertGroups(groups: Set<String>, featureType: GroupFeatureType) {
        var orderIndex = 0
        val creationTime = OffsetDateTimeTypeConverter.fromOffsetDateTime(OffsetDateTime.now())
        groups.forEach { group ->
            db.execSQL(
                "INSERT INTO RoomGroup (name, orderIndex, creationTime, featureType) VALUES (?, ?, ?, ?)",
                arrayOf<Any>(group, orderIndex++, creationTime, featureType.name),
            )
        }
    }

    extractGroups("RoomAsset").apply {
        insertGroups(this, GroupFeatureType.Portfolio)
    }
    extractGroups("RoomQuickPair").apply {
        insertGroups(this, GroupFeatureType.Quick)
    }
    extractGroups("RoomPairAlert").apply {
        insertGroups(this, GroupFeatureType.PairAlert)
    }
}

private fun setGroupIdInPairs(db: SupportSQLiteDatabase) {
    db.execSQL(
        """
        UPDATE RoomAsset
        SET groupId = (
            SELECT id FROM RoomGroup
            WHERE RoomGroup.name = RoomAsset.`group` AND RoomGroup.featureType = 'Portfolio'
        )
        """
    )

    db.execSQL(
        """
        UPDATE RoomQuickPair
        SET groupId = (
            SELECT id FROM RoomGroup
            WHERE RoomGroup.name = RoomQuickPair.`group` AND RoomGroup.featureType = 'Quick'
        )
        """
    )

    db.execSQL(
        """
        UPDATE RoomPairAlert
        SET groupId = (
            SELECT id FROM RoomGroup
            WHERE RoomGroup.name = RoomPairAlert.`group` AND RoomGroup.featureType = 'PairAlert'
        )
        """
    )
}

private fun createFinalPairTables(db: SupportSQLiteDatabase) {
    db.execSQL(
        """
        CREATE TABLE RoomAsset_new (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            code TEXT NOT NULL,
            amount TEXT NOT NULL,
            groupId INTEGER NOT NULL,
            FOREIGN KEY (groupId) REFERENCES RoomGroup(id) ON DELETE CASCADE
        )
        """
    )

    db.execSQL(
        """
        CREATE TABLE RoomQuickPair_new (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `from` TEXT NOT NULL,
            amount TEXT NOT NULL,
            `to` TEXT NOT NULL,
            calculatedDate TEXT NOT NULL,
            pinnedDate TEXT,
            groupId INTEGER NOT NULL,
            FOREIGN KEY (groupId) REFERENCES RoomGroup(id) ON DELETE CASCADE
        )
        """
    )

    db.execSQL(
        """
        CREATE TABLE RoomPairAlert_new (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            targetCode TEXT NOT NULL,
            baseCode TEXT NOT NULL,
            targetPrice TEXT NOT NULL,
            startPrice TEXT NOT NULL,
            alertPercent REAL,
            oneTimeNotRecurrent INTEGER NOT NULL,
            enabled INTEGER NOT NULL,
            lastDateTriggered TEXT,
            groupId INTEGER NOT NULL,
            FOREIGN KEY (groupId) REFERENCES RoomGroup(id) ON DELETE CASCADE
        )
        """
    )
}

private fun migrateDataAndCleanup(db: SupportSQLiteDatabase) {
    db.execSQL("INSERT INTO RoomAsset_new (id, code, amount, groupId) SELECT id, code, amount, groupId FROM RoomAsset")
    db.execSQL("INSERT INTO RoomQuickPair_new (id, `from`, amount, `to`, calculatedDate, pinnedDate, groupId) SELECT id, `from`, amount, `to`, calculatedDate, pinnedDate, groupId FROM RoomQuickPair")
    db.execSQL("INSERT INTO RoomPairAlert_new (id, targetCode, baseCode, targetPrice, startPrice, alertPercent, oneTimeNotRecurrent, enabled, lastDateTriggered, groupId) SELECT id, targetCode, baseCode, targetPrice, startPrice, alertPercent, oneTimeNotRecurrent, enabled, lastDateTriggered, groupId FROM RoomPairAlert")

    db.execSQL("DROP TABLE RoomAsset")
    db.execSQL("DROP TABLE RoomQuickPair")
    db.execSQL("DROP TABLE RoomPairAlert")

    db.execSQL("ALTER TABLE RoomAsset_new RENAME TO RoomAsset")
    db.execSQL("ALTER TABLE RoomQuickPair_new RENAME TO RoomQuickPair")
    db.execSQL("ALTER TABLE RoomPairAlert_new RENAME TO RoomPairAlert")
}

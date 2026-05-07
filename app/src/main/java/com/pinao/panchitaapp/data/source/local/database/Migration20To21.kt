package com.pinao.panchitaapp.data.source.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val Migration20To21 = object : Migration(20, 21) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Drop revenue from category table
        // SQLite doesn't support DROP COLUMN on older versions, so we recreate the table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `category_new` (
                `category_id` TEXT NOT NULL, 
                `store_id` TEXT NOT NULL, 
                `name` TEXT NOT NULL, 
                `is_synced` INTEGER NOT NULL DEFAULT 0, 
                PRIMARY KEY(`category_id`)
            )
            """.trimIndent()
        )
        db.execSQL("INSERT INTO `category_new` (`category_id`, `store_id`, `name`, `is_synced`) SELECT `category_id`, `store_id`, `name`, `is_synced` FROM `category`")
        db.execSQL("DROP TABLE `category`")
        db.execSQL("ALTER TABLE `category_new` RENAME TO `category`")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_category_category_id` ON `category` (`category_id`)")

        // Add revenue to product table
        db.execSQL("ALTER TABLE `product` ADD COLUMN `revenue` REAL NOT NULL DEFAULT 0.0")
    }
}

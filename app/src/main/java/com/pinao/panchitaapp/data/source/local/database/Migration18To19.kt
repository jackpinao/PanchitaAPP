package com.pinao.panchitaapp.data.source.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration18To19 : Migration(18, 19) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `StockEntry` ADD COLUMN `product_id` TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE `StockEntry` ADD COLUMN `quantity_added` REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE `StockEntry` ADD COLUMN `unit_cost_ppp` REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE `StockEntry` ADD COLUMN `movement_type` TEXT NOT NULL DEFAULT 'ENTRY'")
    }
}

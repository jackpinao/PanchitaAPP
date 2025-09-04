package com.pinao.panchitaapp.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_9_10 = object : Migration(9, 10) { // From version 9 to version 10
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add the new column with a default value
        db.execSQL("ALTER TABLE client ADD COLUMN dateCreate TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP")
        // If dateCreate is INTEGER (for Long timestamps):
        // database.execSQL("ALTER TABLE your_table_name ADD COLUMN dateCreate INTEGER NOT NULL DEFAULT 0")
    }
}
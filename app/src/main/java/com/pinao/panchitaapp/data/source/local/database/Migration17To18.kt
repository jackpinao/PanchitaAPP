package com.pinao.panchitaapp.data.source.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration17To18 : Migration(17, 18) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Añadir columna is_deleted a la tabla product con valor por defecto 0 (false)
        db.execSQL("ALTER TABLE `product` ADD COLUMN `is_deleted` INTEGER NOT NULL DEFAULT 0")
    }
}

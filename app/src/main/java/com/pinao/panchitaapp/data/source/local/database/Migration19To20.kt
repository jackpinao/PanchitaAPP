package com.pinao.panchitaapp.data.source.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val Migration19To20 = object : Migration(19, 20) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create new table without BrandEntity foreign key
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `product_new` (
                `product_id` TEXT NOT NULL, 
                `store_id` TEXT NOT NULL, 
                `category_id` TEXT NOT NULL, 
                `brand_id` TEXT NOT NULL, 
                `detailTicketEntity_id` TEXT NOT NULL DEFAULT '', 
                `name` TEXT NOT NULL, 
                `description` TEXT NOT NULL, 
                `price_Buy` REAL NOT NULL, 
                `price_sell` REAL NOT NULL, 
                `price_excluding_igv` REAL NOT NULL, 
                `stock_quantity` REAL NOT NULL, 
                `stock_min` REAL NOT NULL DEFAULT 5.0, 
                `barcode` TEXT NOT NULL, 
                `image` TEXT NOT NULL, 
                `last_updated` TEXT NOT NULL, 
                `is_synced` INTEGER NOT NULL DEFAULT 0, 
                `is_deleted` INTEGER NOT NULL DEFAULT 0, 
                PRIMARY KEY(`product_id`), 
                FOREIGN KEY(`category_id`) REFERENCES `category`(`category_id`) ON UPDATE NO ACTION ON DELETE RESTRICT
            )
            """.trimIndent()
        )

        // Copy the data
        db.execSQL(
            """
            INSERT INTO `product_new` (`product_id`, `store_id`, `category_id`, `brand_id`, `detailTicketEntity_id`, `name`, `description`, `price_Buy`, `price_sell`, `price_excluding_igv`, `stock_quantity`, `stock_min`, `barcode`, `image`, `last_updated`, `is_synced`, `is_deleted`)
            SELECT `product_id`, `store_id`, `category_id`, `brand_id`, `detailTicketEntity_id`, `name`, `description`, `price_Buy`, `price_sell`, `price_excluding_igv`, `stock_quantity`, `stock_min`, `barcode`, `image`, `last_updated`, `is_synced`, `is_deleted`
            FROM `product`
            """.trimIndent()
        )

        // Drop old table
        db.execSQL("DROP TABLE `product`")

        // Rename new table
        db.execSQL("ALTER TABLE `product_new` RENAME TO `product`")

        // Recreate indexes
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_product_barcode` ON `product` (`barcode`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_category_id` ON `product` (`category_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_product_brand_id` ON `product` (`brand_id`)")
    }
}

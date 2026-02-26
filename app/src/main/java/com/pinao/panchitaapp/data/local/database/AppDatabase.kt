package com.pinao.panchitaapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pinao.panchitaapp.data.local.dao.BrandDao
import com.pinao.panchitaapp.data.local.dao.CategoryDao
import com.pinao.panchitaapp.data.local.dao.ClientDao
import com.pinao.panchitaapp.data.local.dao.SaleDetailDao
import com.pinao.panchitaapp.data.local.dao.ProductDao
import com.pinao.panchitaapp.data.local.dao.RechangeDao
import com.pinao.panchitaapp.data.local.dao.StockEntryDao
import com.pinao.panchitaapp.data.local.dao.SupplierDao
import com.pinao.panchitaapp.data.local.dao.TemporaryProductDao
import com.pinao.panchitaapp.data.local.dao.SaleDao
import com.pinao.panchitaapp.data.local.dao.UserDao
import com.pinao.panchitaapp.data.local.entity.BrandEntity
import com.pinao.panchitaapp.data.local.entity.CategoryEntity
import com.pinao.panchitaapp.data.local.entity.ClientEntity
import com.pinao.panchitaapp.data.local.entity.SaleDetailEntity
import com.pinao.panchitaapp.data.local.entity.ProductsEntity
import com.pinao.panchitaapp.data.local.entity.RechangeEntity
import com.pinao.panchitaapp.data.local.entity.StockEntryEntity
import com.pinao.panchitaapp.data.local.entity.SupplierEntity
import com.pinao.panchitaapp.data.local.entity.TemporaryProductEntity
import com.pinao.panchitaapp.data.local.entity.SaleEntity
import com.pinao.panchitaapp.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ClientEntity::class,
        SaleEntity::class,
        SaleDetailEntity::class,
        ProductsEntity::class,
        RechangeEntity::class,
        CategoryEntity::class,
        TemporaryProductEntity::class,
        BrandEntity::class,
        SupplierEntity::class,
        StockEntryEntity::class
    ],
    exportSchema = false,
    version = 17,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun clientDao(): ClientDao
    abstract fun ticketDao(): SaleDao
    abstract fun detailTicketDao(): SaleDetailDao
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun rechangeDao(): RechangeDao
    abstract fun temporaryProductDao(): TemporaryProductDao
    abstract fun supplierDao(): SupplierDao
    abstract fun stockEntryDao(): StockEntryDao
    abstract fun brandDao(): BrandDao
}

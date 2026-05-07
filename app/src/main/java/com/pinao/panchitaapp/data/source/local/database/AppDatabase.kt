package com.pinao.panchitaapp.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

import com.pinao.panchitaapp.data.source.local.dao.CategoryDao
import com.pinao.panchitaapp.data.source.local.dao.ClientDao
import com.pinao.panchitaapp.data.source.local.dao.SaleDetailDao
import com.pinao.panchitaapp.data.source.local.dao.ProductDao
import com.pinao.panchitaapp.data.source.local.dao.RechangeDao
import com.pinao.panchitaapp.data.source.local.dao.StockEntryDao
import com.pinao.panchitaapp.data.source.local.dao.SupplierDao
import com.pinao.panchitaapp.data.source.local.dao.TemporaryProductDao
import com.pinao.panchitaapp.data.source.local.dao.SaleDao
import com.pinao.panchitaapp.data.source.local.dao.UserDao

import com.pinao.panchitaapp.data.source.local.entity.CategoryEntity
import com.pinao.panchitaapp.data.source.local.entity.ClientEntity
import com.pinao.panchitaapp.data.source.local.entity.SaleDetailEntity
import com.pinao.panchitaapp.data.source.local.entity.ProductsEntity
import com.pinao.panchitaapp.data.source.local.entity.RechangeEntity
import com.pinao.panchitaapp.data.source.local.entity.StockEntryEntity
import com.pinao.panchitaapp.data.source.local.entity.SupplierEntity
import com.pinao.panchitaapp.data.source.local.entity.TemporaryProductEntity
import com.pinao.panchitaapp.data.source.local.entity.SaleEntity
import com.pinao.panchitaapp.data.source.local.entity.UserEntity

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

        SupplierEntity::class,
        StockEntryEntity::class
    ],
    exportSchema = true,
    version = 22,
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

}

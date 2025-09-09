package com.pinao.panchitaapp.data.local.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.pinao.panchitaapp.data.local.dao.ClientDao
import com.pinao.panchitaapp.data.local.dao.DetailTicketDao
import com.pinao.panchitaapp.data.local.dao.ProductDao
import com.pinao.panchitaapp.data.local.dao.RechangeDao
import com.pinao.panchitaapp.data.local.dao.TicketDao
import com.pinao.panchitaapp.data.local.dao.UserDao
import com.pinao.panchitaapp.data.local.entity.CategoryEntity
import com.pinao.panchitaapp.data.local.entity.ClientEntity
import com.pinao.panchitaapp.data.local.entity.DetailTicketEntity
import com.pinao.panchitaapp.data.local.entity.ProductsEntity
import com.pinao.panchitaapp.data.local.entity.RechangeEntity
import com.pinao.panchitaapp.data.local.entity.TicketEntity
import com.pinao.panchitaapp.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ClientEntity::class,
        TicketEntity::class,
        DetailTicketEntity::class,
        ProductsEntity::class,
        RechangeEntity::class,
        CategoryEntity::class
    ],
    exportSchema = true,
    version = 11,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 10, to = 11),
    ],
)
  abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun clientDao(): ClientDao
    abstract fun ticketDao(): TicketDao
    abstract fun detailTicketDao(): DetailTicketDao
    abstract fun productDao(): ProductDao
    abstract fun rechangeDao(): RechangeDao
}
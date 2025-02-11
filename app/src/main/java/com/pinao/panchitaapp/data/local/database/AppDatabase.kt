package com.pinao.panchitaapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pinao.panchitaapp.data.local.dao.ClientDao
import com.pinao.panchitaapp.data.local.dao.DetailTicketDao
import com.pinao.panchitaapp.data.local.dao.ProductDao
import com.pinao.panchitaapp.data.local.dao.RechangeDao
import com.pinao.panchitaapp.data.local.dao.TicketDao
import com.pinao.panchitaapp.data.local.dao.UserDao
import com.pinao.panchitaapp.data.local.entity.ClientEntity
import com.pinao.panchitaapp.data.local.entity.DetailTicketEntity
import com.pinao.panchitaapp.data.local.entity.ProductEntity
import com.pinao.panchitaapp.data.local.entity.RechangeEntity
import com.pinao.panchitaapp.data.local.entity.TicketEntity
import com.pinao.panchitaapp.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ClientEntity::class,
        TicketEntity::class,
        DetailTicketEntity::class,
        ProductEntity::class,
        RechangeEntity::class
    ], version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun clientDao(): ClientDao
    abstract fun ticketDao(): TicketDao
    abstract fun detailTicketDao(): DetailTicketDao
    abstract fun productDao(): ProductDao
    abstract fun rechangeDao(): RechangeDao
}
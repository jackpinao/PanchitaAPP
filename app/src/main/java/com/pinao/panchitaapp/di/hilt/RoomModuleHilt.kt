package com.pinao.panchitaapp.di.hilt

//import android.content.Context
//import androidx.room.Room
//import com.pinao.panchitaapp.data.local.database.AppDatabase
//import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
//import com.pinao.panchitaapp.data.repository.UserRepositoryImpl
//import com.pinao.panchitaapp.domain.repository.RechangeRepository
//import com.pinao.panchitaapp.domain.repository.UserRepository
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object RoomModuleHilt {
//
//    private val DATABASE_NAME = "panchita_app"
//
//    @Provides
//    @Singleton
//    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
//        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
//
//    @Provides
//    @Singleton
//    fun provideTicketDao(database: AppDatabase) = database.ticketDao()
//
//    @Provides
//    @Singleton
//    fun provideProductDao(database: AppDatabase) = database.productDao()
//
//    @Provides
//    @Singleton
//    fun provideDetailTicketDao(database: AppDatabase) = database.detailTicketDao()
//
//    @Provides
//    @Singleton
//    fun provideUserDao(database: AppDatabase) = database.userDao()
//
//    @Provides
//    @Singleton
//    fun provideClientDao(database: AppDatabase) = database.clientDao()
//
//    @Provides
//    fun provideRechangeDao(database: AppDatabase) = database.rechangeDao()
//
//    @Provides
//    @Singleton
//    fun provideUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository =
//        userRepositoryImpl
//
//    @Provides
//    @Singleton
//    fun provideRechangeRepository(rechangeRepositoryImpl: RechangeRepositoryImpl): RechangeRepository =
//        rechangeRepositoryImpl
//
//}
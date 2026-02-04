package com.pinao.panchitaapp.di.koin

import android.content.Context
import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.pinao.panchitaapp.data.local.dao.CategoryDao
import com.pinao.panchitaapp.data.local.dao.ClientDao
import com.pinao.panchitaapp.data.local.dao.DetailTicketDao
import com.pinao.panchitaapp.data.local.dao.ProductDao
import com.pinao.panchitaapp.data.local.dao.RechangeDao
import com.pinao.panchitaapp.data.local.dao.TemporaryProductDao
import com.pinao.panchitaapp.data.local.dao.TicketDao
import com.pinao.panchitaapp.data.local.database.AppDatabase
import com.pinao.panchitaapp.data.network.rechange.RechangeApiClient
import com.pinao.panchitaapp.data.network.rechange.RechangeService
import com.pinao.panchitaapp.data.repository.CategoryRepositoryImpl
import com.pinao.panchitaapp.data.repository.ClientRepositoryImpl
import com.pinao.panchitaapp.data.repository.DetailTicketRepositoryImpl
import com.pinao.panchitaapp.data.repository.GmsBarcodeScannerImpl
import com.pinao.panchitaapp.data.repository.ProductsRepositoryImpl
import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.data.repository.TemporaryProductRepositoryImpl
import com.pinao.panchitaapp.data.repository.TicketRepositoryImpl
import com.pinao.panchitaapp.data.service.AndroidTicketPdfService
import com.pinao.panchitaapp.domain.repository.BarcodeScanner
import com.pinao.panchitaapp.domain.repository.CategoryRepository
import com.pinao.panchitaapp.domain.repository.ClientRepository
import com.pinao.panchitaapp.domain.repository.DetailTicketRepository
import com.pinao.panchitaapp.domain.repository.ProductRepository
import com.pinao.panchitaapp.domain.repository.RechangeRepository
import com.pinao.panchitaapp.domain.repository.TemporaryProductRepository
import com.pinao.panchitaapp.domain.repository.TicketRepository
import com.pinao.panchitaapp.domain.service.TicketPdfService
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import retrofit2.Retrofit

@Module
class DataModule {
    val DATABASE_NAME = "panchita_app"

    @Single(createdAtStart = true)
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Single
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Single
    fun provideRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/") // Replace with your base URL
            .build()
    }

    @Single
    fun provideRechangeApiClient(retrofit: Retrofit): RechangeApiClient {
        return retrofit.create(RechangeApiClient::class.java)
    }

    @Single
    fun provideRechangeDao(database: AppDatabase): RechangeDao {
        return database.rechangeDao()
    }

    @Single
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    @Single
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Single
    fun provideClientDao(database: AppDatabase): ClientDao {
        return database.clientDao()
    }

    @Single
    fun provideDetailTicketDao(database: AppDatabase): DetailTicketDao {
        return database.detailTicketDao()
    }

    @Single
    fun provideTicketDao(database: AppDatabase): TicketDao {
        return database.ticketDao()
    }

    @Single
    fun provideTemporaryProductDao(database: AppDatabase): TemporaryProductDao {
        return database.temporaryProductDao()
    }

    @Single
    fun provideRechangeService(api: RechangeApiClient): RechangeService {
        return RechangeService(api)
    }

    @Single
    fun provideRechangeRepository(
        api: RechangeService,
        rechangeDao: RechangeDao
    ): RechangeRepository {
        return RechangeRepositoryImpl(api, rechangeDao)
    }

    @Single(createdAtStart = true)
    fun provideProductRepository(
        productDao: ProductDao,
        categoryDao: CategoryDao,
        firestore: FirebaseFirestore
    ): ProductRepository {
        return ProductsRepositoryImpl(productDao, categoryDao, firestore)
    }

    @Single(createdAtStart = true)
    fun provideClientRepository(clientDao: ClientDao): ClientRepository {
        return ClientRepositoryImpl(clientDao)
    }

    @Single
    fun provideCategoryRepository(
        categoryDao: CategoryDao,
        firestore: FirebaseFirestore
    ): CategoryRepository {
        return CategoryRepositoryImpl(categoryDao, firestore)
    }

    @Single
    fun provideBarcodeScanner(context: Context): BarcodeScanner {
        return GmsBarcodeScannerImpl(context)
    }

    @Single
    fun provideDetailTicketRepository(
        detailTicketDao: DetailTicketDao,
        firestore: FirebaseFirestore
    ): DetailTicketRepository {
        return DetailTicketRepositoryImpl(detailTicketDao, firestore)
    }

    @Single
    fun provideTicketPdfService(context: Context): TicketPdfService {
        return AndroidTicketPdfService(context)
    }

    @Single
    fun provideTicketRepository(
        ticketDao: TicketDao,
        detailTicketDao: DetailTicketDao,
        firestore: FirebaseFirestore
    ): TicketRepository {
        return TicketRepositoryImpl(
            ticketDao,
            detailTicketDao,
            firestore
        )
    }

    @Single
    fun provideTemporaryProductRepository(
        temporaryProductDao: TemporaryProductDao
    ): TemporaryProductRepository {
        return TemporaryProductRepositoryImpl(
            temporaryProductDao
        )
    }

}
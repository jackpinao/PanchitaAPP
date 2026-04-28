package com.pinao.panchitaapp.di.koin

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.pinao.panchitaapp.data.network.rechange.RechangeApiClient
import com.pinao.panchitaapp.data.network.rechange.RechangeService
import com.pinao.panchitaapp.data.repository.AuthRepositoryImpl
import com.pinao.panchitaapp.data.repository.BrandRepositoryImpl
import com.pinao.panchitaapp.data.repository.CategoryRepositoryImpl
import com.pinao.panchitaapp.data.repository.ClientRepositoryImpl
import com.pinao.panchitaapp.data.repository.DetailTicketRepositoryImpl
import com.pinao.panchitaapp.data.repository.GmsBarcodeScannerImpl
import com.pinao.panchitaapp.data.repository.ProductsRepositoryImpl
import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.data.repository.SaleRepositoryImpl
import com.pinao.panchitaapp.data.repository.TemporaryProductRepositoryImpl
import com.pinao.panchitaapp.data.service.AndroidTicketPdfService
import com.pinao.panchitaapp.data.source.local.SessionManager
import com.pinao.panchitaapp.data.source.local.dao.BrandDao
import com.pinao.panchitaapp.data.source.local.dao.CategoryDao
import com.pinao.panchitaapp.data.source.local.dao.ClientDao
import com.pinao.panchitaapp.data.source.local.dao.ProductDao
import com.pinao.panchitaapp.data.source.local.dao.RechangeDao
import com.pinao.panchitaapp.data.source.local.dao.SaleDao
import com.pinao.panchitaapp.data.source.local.dao.SaleDetailDao
import com.pinao.panchitaapp.data.source.local.dao.TemporaryProductDao
import com.pinao.panchitaapp.data.source.local.database.AppDatabase
import com.pinao.panchitaapp.data.source.remote.BrandRemoteDataSource
import com.pinao.panchitaapp.data.source.remote.CategoryRemoteDataSource
import com.pinao.panchitaapp.data.source.remote.ProductRemoteDataSource
import com.pinao.panchitaapp.data.source.remote.RemoteDataSource
import com.pinao.panchitaapp.data.source.remote.RemoteDataSourceImpl
import com.pinao.panchitaapp.data.source.remote.firebase.FirebaseBrandDataSource
import com.pinao.panchitaapp.data.source.remote.firebase.FirebaseCategoryDataSource
import com.pinao.panchitaapp.data.source.remote.firebase.FirebaseProductDataSource
import com.pinao.panchitaapp.domain.repository.AuthRepository
import com.pinao.panchitaapp.domain.repository.BarcodeScanner
import com.pinao.panchitaapp.domain.repository.BrandRepository
import com.pinao.panchitaapp.domain.repository.CategoryRepository
import com.pinao.panchitaapp.domain.repository.ClientRepository
import com.pinao.panchitaapp.domain.repository.DetailTicketRepository
import com.pinao.panchitaapp.domain.repository.ProductRepository
import com.pinao.panchitaapp.domain.repository.RechangeRepository
import com.pinao.panchitaapp.domain.repository.SaleRepository
import com.pinao.panchitaapp.domain.repository.TemporaryProductRepository
import com.pinao.panchitaapp.domain.service.TicketPdfService
import org.koin.core.annotation.Factory
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
            .addMigrations(com.pinao.panchitaapp.data.source.local.database.Migration17To18(), com.pinao.panchitaapp.data.source.local.database.Migration18To19())
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Single
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Single
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Single
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("panchita_prefs", Context.MODE_PRIVATE)
    }

    @Single
    fun provideSessionManager(sharedPreferences: SharedPreferences): SessionManager {
        return SessionManager(sharedPreferences)
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
    fun provideDetailTicketDao(database: AppDatabase): SaleDetailDao {
        return database.detailTicketDao()
    }

    @Single
    fun provideTicketDao(database: AppDatabase): SaleDao {
        return database.ticketDao()
    }

    @Single
    fun provideTemporaryProductDao(database: AppDatabase): TemporaryProductDao {
        return database.temporaryProductDao()
    }

    @Single
    fun provideBrandDao(database: AppDatabase): BrandDao {
        return database.brandDao()
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

    @Single
    fun provideStockEntryDao(database: AppDatabase): com.pinao.panchitaapp.data.source.local.dao.StockEntryDao {
        return database.stockEntryDao()
    }

    @Single(createdAtStart = true)
    fun provideProductRepository(
        productDao: ProductDao,
        categoryDao: CategoryDao,
        brandDao: BrandDao,
        stockEntryDao: com.pinao.panchitaapp.data.source.local.dao.StockEntryDao,
        remoteDataSource: RemoteDataSource
    ): ProductRepository {
        return ProductsRepositoryImpl(productDao, categoryDao, brandDao, stockEntryDao, remoteDataSource)
    }

    @Single(createdAtStart = true)
    fun provideBrandRepository(
        brandDao: BrandDao,
        remoteDataSource: RemoteDataSource
    ): BrandRepository {
        return BrandRepositoryImpl(
            brandDao,
            remoteDataSource
        )
    }

    @Single(createdAtStart = true)
    fun provideClientRepository(clientDao: ClientDao): ClientRepository {
        return ClientRepositoryImpl(clientDao)
    }

    @Single
    fun provideCategoryRepository(
        categoryDao: CategoryDao,
        remoteDataSource: RemoteDataSource
    ): CategoryRepository {
        return CategoryRepositoryImpl(categoryDao, remoteDataSource)
    }

    @Single
    fun provideBarcodeScanner(context: Context): BarcodeScanner {
        return GmsBarcodeScannerImpl(context)
    }

    @Single
    fun provideDetailTicketRepository(
        saleDetailDao: SaleDetailDao,
        firestore: FirebaseFirestore
    ): DetailTicketRepository {
        return DetailTicketRepositoryImpl(saleDetailDao, firestore)
    }

    @Single
    fun provideTicketPdfService(context: Context): TicketPdfService {
        return AndroidTicketPdfService(context)
    }

    @Single
    fun provideTicketRepository(
        saleDao: SaleDao,
        saleDetailDao: SaleDetailDao,
        firestore: FirebaseFirestore
    ): SaleRepository {
        return SaleRepositoryImpl(
            saleDao,
            saleDetailDao,
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

    @Single
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        sessionManager: SessionManager
    ): AuthRepository {
        return AuthRepositoryImpl(
            firebaseAuth,
            firestore,
            sessionManager
        )
    }

    @Factory
    fun provideRemoteDataSource(
        brandRemoteDataSource: BrandRemoteDataSource,
        categoryRemoteDataSource: CategoryRemoteDataSource,
        productRemoteDataSource: ProductRemoteDataSource
    ): RemoteDataSource = RemoteDataSourceImpl(
        brandRemoteDataSource = brandRemoteDataSource,
        categoryRemoteDataSource = categoryRemoteDataSource,
        productRemoteDataSource = productRemoteDataSource
    )

    @Single
    fun provideBrandRemoteDataSource(firestore: FirebaseFirestore): BrandRemoteDataSource {
        return FirebaseBrandDataSource(firestore)
    }

    @Single
    fun provideCategoryRemoteDataSource(firestore: FirebaseFirestore): CategoryRemoteDataSource {
        return FirebaseCategoryDataSource(firestore)
    }

    @Single
    fun provideProductRemoteDataSource(firestore: FirebaseFirestore): ProductRemoteDataSource {
        return FirebaseProductDataSource(firestore)
    }
}

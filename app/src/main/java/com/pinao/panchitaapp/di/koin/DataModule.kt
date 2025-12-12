package com.pinao.panchitaapp.di.koin

import android.content.Context
import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.pinao.panchitaapp.data.local.dao.CategoryDao
import com.pinao.panchitaapp.data.local.dao.ClientDao
import com.pinao.panchitaapp.data.local.dao.ProductDao
import com.pinao.panchitaapp.data.local.dao.RechangeDao
import com.pinao.panchitaapp.data.local.database.AppDatabase
import com.pinao.panchitaapp.data.local.database.MIGRATION_9_10
import com.pinao.panchitaapp.data.network.rechange.RechangeApiClient
import com.pinao.panchitaapp.data.network.rechange.RechangeService
import com.pinao.panchitaapp.data.repository.CategoryRepositoryImpl
import com.pinao.panchitaapp.data.repository.ClientRepositoryImpl
import com.pinao.panchitaapp.data.repository.ProductsRepositoryImpl
import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.domain.repository.CategoryRepository
import com.pinao.panchitaapp.domain.repository.ClientRepository
import com.pinao.panchitaapp.domain.repository.ProductRepository
import com.pinao.panchitaapp.domain.repository.RechangeRepository
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import retrofit2.Retrofit

@Module
class DataModule {
    val DATABASE_NAME = "panchita_app"

    @Single(createdAtStart = true)
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .addMigrations(MIGRATION_9_10)
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
        firestore: FirebaseFirestore
    ): ProductRepository {
        return ProductsRepositoryImpl(productDao, firestore)
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
}
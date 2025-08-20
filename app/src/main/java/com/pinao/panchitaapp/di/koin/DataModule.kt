package com.pinao.panchitaapp.di.koin

import android.content.Context
import androidx.room.Room
import com.pinao.panchitaapp.data.local.dao.ProductDao
import com.pinao.panchitaapp.data.local.dao.RechangeDao
import com.pinao.panchitaapp.data.local.database.AppDatabase
import com.pinao.panchitaapp.data.network.rechange.RechangeApiClient
import com.pinao.panchitaapp.data.network.rechange.RechangeService
import com.pinao.panchitaapp.data.repository.ProductsRepositoryImpl
import com.pinao.panchitaapp.data.repository.RechangeRepositoryImpl
import com.pinao.panchitaapp.domain.repository.ProductRepository
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import retrofit2.Retrofit

@Module
class DataModule {

    val DATABASE_NAME = "panchita_app"
    @Single
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
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
        return database.productDao() // Assuming you meant to provide the same Dao for recharges
    }

    @Single
    fun provideRechangeService(api: RechangeApiClient): RechangeService {
        return RechangeService(api)
    }

    @Single
    fun provideRechangeRepository(api: RechangeService, rechangeDao: RechangeDao): RechangeRepositoryImpl {
        return RechangeRepositoryImpl(api, rechangeDao)
    }

    @Single
    fun provideProductRepository(productDao: ProductDao): ProductsRepositoryImpl {
        return ProductsRepositoryImpl(productDao) // Assuming you meant to provide the same repository for products
    }
}
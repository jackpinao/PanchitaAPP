package com.pinao.panchitaapp.di.hilt

//import com.google.firebase.sessions.dagger.Provides
import com.pinao.panchitaapp.data.network.rechange.RechangeApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
//import org.koin.core.annotation.Module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModuleHilt {
    @Provides
    @Singleton
//    @Named("ProvideRetrofit")
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiClient(retrofit: Retrofit): RechangeApiClient {
        return retrofit.create(RechangeApiClient::class.java)
    }
}
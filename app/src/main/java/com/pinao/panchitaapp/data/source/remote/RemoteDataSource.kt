package com.pinao.panchitaapp.data.source.remote

interface RemoteDataSource {
    val productRemoteDataSource: ProductRemoteDataSource
    val categoryRemoteDataSource: CategoryRemoteDataSource
    val brandRemoteDataSource: BrandRemoteDataSource
}
package com.pinao.panchitaapp.data.source.remote

data class RemoteDataSource(
    val productRemoteDataSource: ProductRemoteDataSource,
    val categoryRemoteDataSource: CategoryRemoteDataSource,
    val brandRemoteDataSource: BrandRemoteDataSource
)
package com.pinao.panchitaapp.data.source.remote

data class RemoteDataSourceImpl(
    override val productRemoteDataSource: ProductRemoteDataSource,
    override val categoryRemoteDataSource: CategoryRemoteDataSource
) : RemoteDataSource

package com.pinao.panchitaapp.data.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pinao.panchitaapp.domain.usecase.products.SyncUnsyncedProductsUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val syncUnsyncedProductsUseCase: SyncUnsyncedProductsUseCase by inject()

    override suspend fun doWork(): Result {
        return try {
            syncUnsyncedProductsUseCase()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}

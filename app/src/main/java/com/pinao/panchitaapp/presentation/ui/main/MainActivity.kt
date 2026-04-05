package com.pinao.panchitaapp.presentation.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pinao.panchitaapp.data.service.SyncWorker
import com.pinao.panchitaapp.domain.usecase.products.SyncUnsyncedProductsUseCase
import com.pinao.panchitaapp.presentation.navigation.AppNavGraph
import com.pinao.panchitaapp.presentation.theme.resource.PanchitaAPPTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass

class MainActivity : ComponentActivity() {

    private val syncUnsyncedProductsUseCase: SyncUnsyncedProductsUseCase by inject()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Sincronización inmediata al abrir la app
        lifecycleScope.launch {
            try {
                syncUnsyncedProductsUseCase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Configurar sincronización automática en segundo plano
        setupBackgroundSync()

        enableEdgeToEdge()
        setContent {
            val windowSize = calculateWindowSizeClass(this)
            
            PanchitaAPPTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(windowSize = windowSize)
                }
            }
        }
    }

    private fun setupBackgroundSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ProductSyncWork",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}

package com.pinao.panchitaapp.domain.usecase.products

import com.pinao.panchitaapp.domain.repository.AuthRepository
import com.pinao.panchitaapp.domain.repository.ProductRepository
import android.util.Log

class RefreshProductsUseCase(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        if (authRepository.isUserLoggedIn()) {
            productRepository.refreshProductsFromRemote()
        } else {
            Log.d("RefreshProductsUseCase", "User is not logged in. Skipping remote refresh.")
        }
    }
}

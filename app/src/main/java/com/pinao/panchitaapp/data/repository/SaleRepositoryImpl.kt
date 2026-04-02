package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.source.local.dao.SaleDetailDao
import com.pinao.panchitaapp.data.source.local.dao.SaleDao
import com.pinao.panchitaapp.data.source.local.entity.SaleDetailEntity
import com.pinao.panchitaapp.data.mapper.SaleMapper
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.repository.SaleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Repositorio de Tickets que coordina la persistencia local (Room) y remota (Firestore).
 */
class SaleRepositoryImpl(
    private val saleDao: SaleDao,
    private val saleDetailDao: SaleDetailDao,
    private val firestore: FirebaseFirestore
) : SaleRepository {

    private val saleCollection = firestore.collection("ticket")
    private val detailsCollection = firestore.collection("detail_ticket")

    /**
     * Guarda una venta completa sincronizando localmente y en la nube.
     * Utiliza transacciones locales y WriteBatch remoto para garantizar la integridad.
     */
    override suspend fun saveFullSale(sale: SaleModel, products: List<ProductModel>) {
        withContext(Dispatchers.IO) {
            try {
                // 1. Persistencia Local (Room)
                val saleEntity = SaleMapper.toEntity(sale)
                val batch = firestore.batch()
                val detailEntities = products.map { product ->
                    SaleDetailEntity(
                        saleDetailId = UUID.randomUUID().toString(),
                        saleId = sale.saleId,
                        productId = product.barcode,
                        quantity = product.stockQuantity,
                        priceAtSale = product.priceSell,
                        subtotal = product.priceSell * product.stockQuantity
                    )
                }

                // Ejecutamos la transacción en Room
                saleDao.saveFullSale(saleEntity, detailEntities)
                Log.d("TicketRepositoryImp", "Venta guardada localmente en Room")

                detailEntities.forEach { detail ->
                    val detailRef = detailsCollection.document(detail.saleDetailId)
                    batch.set(detailRef, detail)
                    Log.d("TicketRepositoryImp", "Detalle de venta guardado en Firestore")
                }

                // 2. Persistencia Remota (Firestore) mediante WriteBatch
                val saleRef = saleCollection.document(sale.saleId)
                // Guardar la cabecera del ticket
                batch.set(saleRef, sale)

                // Guardar productos como subcolección para evitar límites de tamaño de documento
                products.forEach { product ->
                    // 2. Guardar Detalle de Venta
                    val detailRef = saleRef.collection("items").document(UUID.randomUUID().toString())
                    batch.set(detailRef, product)

                    // 3. ACTUALIZAR STOCK FINAL EN FIRESTORE (Colección Maestra)
                    // Importante: Usar product.id ya que es el ID del documento en Firestore, no product.code
                    val productMasterRef = firestore.collection("product").document(product.productId)
                    batch.update(productMasterRef, "stock", product.stockQuantity)
                }

                // Ejecutamos el lote de forma asíncrona
                batch.commit().await()
                Log.d("TicketRepositoryImpl", "Venta sincronizada exitosamente con Firestore")

            } catch (e: Exception) {
                Log.e("TicketRepositoryImpl", "Error crítico al guardar la venta: ${e.message}", e)
                // Re-lanzamos la excepción para que el ViewModel/UI maneje el estado de error
                throw e
            }
        }
    }
}
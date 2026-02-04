package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.local.dao.DetailTicketDao
import com.pinao.panchitaapp.data.local.dao.TicketDao
import com.pinao.panchitaapp.data.local.entity.DetailTicketEntity
import com.pinao.panchitaapp.data.mapper.TicketMapper
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.TicketModel
import com.pinao.panchitaapp.domain.repository.TicketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Repositorio de Tickets que coordina la persistencia local (Room) y remota (Firestore).
 */
class TicketRepositoryImpl(
    private val ticketDao: TicketDao,
    private val detailTicketDao: DetailTicketDao,
    private val firestore: FirebaseFirestore
) : TicketRepository {

    private val ticketsCollection = firestore.collection("ticket")
    private val detailsCollection = firestore.collection("detail_ticket")

    /**
     * Guarda una venta completa sincronizando localmente y en la nube.
     * Utiliza transacciones locales y WriteBatch remoto para garantizar la integridad.
     */
    override suspend fun saveFullSale(ticket: TicketModel, products: List<ProductModel>) {
        withContext(Dispatchers.IO) {
            try {
                // 1. Persistencia Local (Room)
                val ticketEntity = TicketMapper.toEntity(ticket)
                val detailEntities = products.map { product ->
                    DetailTicketEntity(
                        id = UUID.randomUUID().toString(),
                        date = ticket.date,
                        description = product.name,
                        price = product.sellingPrice,
                        amount = product.stock.toInt(),
                        import = product.sellingPrice * product.stock,
                        // Conversión segura de tipos para la base de datos local
                        idTicket = ticket.id,
                        idProduct = product.code
                    )
                }

                // Ejecutamos la transacción en Room
                ticketDao.saveFullSale(ticketEntity, detailEntities)
                Log.d("TicketRepositoryImp", "Venta guardada localmente en Room")

                // 2. Persistencia Remota (Firestore) mediante WriteBatch
                val batch = firestore.batch()
                val ticketRef = ticketsCollection.document(ticket.id)
                // Guardar la cabecera del ticket
                batch.set(ticketRef, ticket)

                // Guardar productos como subcolección para evitar límites de tamaño de documento
                products.forEach { product ->
                    // 2. Guardar Detalle de Venta
                    val detailRef = ticketRef.collection("items").document(UUID.randomUUID().toString())
                    batch.set(detailRef, product)

                    // 3. ACTUALIZAR STOCK FINAL EN FIRESTORE (Colección Maestra)
                    // Importante: Usar product.id ya que es el ID del documento en Firestore, no product.code
                    val productMasterRef = firestore.collection("product").document(product.id)
                    batch.update(productMasterRef, "stock", product.stock)
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
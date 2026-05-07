package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.mapper.SaleDetailMapper
import com.pinao.panchitaapp.data.mapper.SaleMapper
import com.pinao.panchitaapp.data.source.local.dao.SaleDao
import com.pinao.panchitaapp.data.source.local.dao.SaleDetailDao
import com.pinao.panchitaapp.domain.model.ProductModel
import com.pinao.panchitaapp.domain.model.SaleModel
import com.pinao.panchitaapp.domain.repository.SaleRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio de Tickets que coordina la persistencia local (Room) y remota (Supabase).
 */
class SaleRepositoryImpl(
    private val saleDao: SaleDao,
    private val saleDetailDao: SaleDetailDao,
    private val supabaseClient: SupabaseClient,
    private val firestore: FirebaseFirestore
) : SaleRepository {

    private val saleCollection = firestore.collection("ticket")
    private val detailsCollection = firestore.collection("detail_ticket")

    /**
     * Guarda una venta completa sincronizando localmente y en la nube.
     * Utiliza transacciones locales y WriteBatch remoto para garantizar la integridad.
     */
    override suspend fun saveFullSale(ticket: SaleModel, products: List<ProductModel>) {
        withContext(Dispatchers.IO) {
            try {
                // 1. Persistencia Local (Room)
                val saleEntity = SaleMapper.toEntity(ticket)
                val detailEntities = products.map { product ->
                    SaleDetailMapper.toEntity(product, ticket.saleId)
                }

                // Ejecutamos la transacción en Room
                saleDao.saveFullSale(saleEntity, detailEntities)
                Log.d("TicketRepositoryImp", "Venta guardada localmente en Room")

                // 2. Persistencia Remota (Supabase)

                // Guardar la cabecera del ticket
                val saleDto = SaleMapper.toSupabaseDto(ticket)
                supabaseClient.postgrest["sales"].upsert(saleDto)

                // Guardar detalles de venta
                val detailDtos = detailEntities.mapIndexed { index, entity ->
                    SaleDetailMapper.toSupabaseDto(entity, products[index].name, ticket.storeId)
                }
                supabaseClient.postgrest["sale_items"].upsert(detailDtos)

                // 3. Actualizar Stock en Supabase
                products.forEach { product ->
                    supabaseClient.postgrest["products"].update({
                        set("current_stock", product.stockQuantity)
                    }) {
                        filter {
                            eq("id", product.productId)
                        }
                    }
                }


                Log.d("TicketRepositoryImpl", "Venta sincronizada exitosamente con Supabase")

            } catch (e: Exception) {
                Log.e("TicketRepositoryImpl", "Error crítico al guardar la venta: ${e.message}", e)
                throw e
            }
        }
    }
}
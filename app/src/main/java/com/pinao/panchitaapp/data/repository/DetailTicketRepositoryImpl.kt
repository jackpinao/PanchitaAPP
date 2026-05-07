package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.source.local.dao.SaleDetailDao
import com.pinao.panchitaapp.data.mapper.SaleDetailMapper
import com.pinao.panchitaapp.domain.model.SaleDetailModel
import com.pinao.panchitaapp.domain.repository.DetailTicketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import com.pinao.panchitaapp.data.source.remote.dto.SupabaseSaleDetailDto

class DetailTicketRepositoryImpl(
    private val saleDetailDao: SaleDetailDao,
    private val supabaseClient: SupabaseClient,
    private val firestore: FirebaseFirestore
) : DetailTicketRepository {

    override suspend fun saveTicketDetails(details: SaleDetailModel) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("DetailTicketRepositoryImpl", "Saving details to Supabase: $details")
                // Nota: Aquí necesitaríamos el nombre del producto para el DTO completo, 
                // pero si la interfaz no lo provee, podemos omitirlo o traerlo de Room.
                // Por ahora usamos un placeholder o asumimos que se maneja en SaleRepository.
                
                // detailsCollection.document(details.saleDetailId).set(details).await()
                
                Log.d("DetailTicketRepositoryImpl", "Saving details Ticket to Room: $details")
                saleDetailDao.insertDetails(SaleDetailMapper.toDatabase(details))
                Log.d("DetailTicketRepositoryImpl", "Details saved successfully")

            } catch (e: Exception) {
                Log.e("DetailTicketRepositoryImpl", "Error saving details", e)
            }
        }
    }


    override fun getDetailsByTicketId(ticketId: String): Flow<List<SaleDetailModel>> {
        Log.d("DetailTicketRepositoryImpl", "Getting details by ticketId: $ticketId")
        return saleDetailDao.getDetailsByTicketId(ticketId).map { entities ->
            entities.map { SaleDetailMapper.toDomain(it) }
        }
    }
}

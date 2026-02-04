package com.pinao.panchitaapp.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pinao.panchitaapp.data.local.dao.DetailTicketDao
import com.pinao.panchitaapp.data.mapper.DetailsTicketMapper
import com.pinao.panchitaapp.domain.model.DetailTicketModel
import com.pinao.panchitaapp.domain.repository.DetailTicketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DetailTicketRepositoryImpl(
    private val detailTicketDao: DetailTicketDao,
    private val firestore: FirebaseFirestore
) : DetailTicketRepository {

    private val detailsCollection = firestore.collection("detail_ticket")

    override suspend fun saveTicketDetails(details: DetailTicketModel) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("DetailTicketRepositoryImpl", "Saving details to Firestore: $details")
                detailsCollection.document(details.id).set(details).await()
                Log.d("DetailTicketRepositoryImpl", "Saving details Ticket to Room: $details")
                detailTicketDao.insertDetails(DetailsTicketMapper.toDatabase(details))
                Log.d("DetailTicketRepositoryImpl", "Details saved successfully")

            } catch (e: Exception) {
                Log.e("DetailTicketRepositoryImpl", "Error saving details", e)
            }
        }
    }


    override fun getDetailsByTicketId(ticketId: String): Flow<List<DetailTicketModel>> {
        Log.d("DetailTicketRepositoryImpl", "Getting details by ticketId: $ticketId")
        return detailTicketDao.getDetailsByTicketId(ticketId).map { entities ->
            entities.map { DetailsTicketMapper.toDomain(it) }
        }
    }
}

package com.pinao.panchitaapp.domain.model

/**
 * Representa la cabecera de una entrada de stock (Factura) en la capa de negocio.
 *
 * @property entryId Identificador único de la entrada.
 * @property storeId ID de la tienda asociada.
 * @property supplierId ID del proveedor que suministra el stock.
 * @property entryDate Fecha de entrada en formato Timestamp (Long).
 * @property totalCost Costo total de la factura.
 * @property documentNumber Número físico de la factura o documento de respaldo.
 * @property isSynced Indica si el registro ya fue sincronizado con el servidor.
 */
data class StockEntryModel(
    val entryId: String,
    val storeId: String,
    val supplierId: String,
    val entryDate: Long,
    val totalCost: Double,
    val documentNumber: String?,
    val isSynced: Boolean,
    val productId: String = "",
    val quantityAdded: Double = 0.0,
    val unitCostPpp: Double = 0.0,
    val movementType: String = "ENTRY"
)
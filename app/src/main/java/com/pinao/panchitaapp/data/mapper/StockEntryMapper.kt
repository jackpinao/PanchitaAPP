import com.pinao.panchitaapp.data.source.local.entity.StockEntryEntity
import com.pinao.panchitaapp.domain.model.StockEntryModel

/**
 * Objeto encargado de transformar datos entre la entidad de base de datos y el modelo de dominio.
 */
object StockEntryMapper {

    /**
     * Convierte una entidad de base de datos en un modelo de dominio.
     */
    fun toDomain(entity: StockEntryEntity): StockEntryModel {
        return StockEntryModel(
            entryId = entity.entryId,
            storeId = entity.storeId,
            supplierId = entity.supplierId,
            entryDate = entity.entryDate,
            totalCost = entity.totalCost,
            documentNumber = entity.documentNumber,
            isSynced = entity.isSynced == 1
        )
    }

    /**
     * Convierte un modelo de dominio en una entidad compatible con Room.
     */
    fun toDatabase(model: StockEntryModel): StockEntryEntity {
        return StockEntryEntity(
            entryId = model.entryId,
            storeId = model.storeId,
            supplierId = model.supplierId,
            entryDate = model.entryDate,
            totalCost = model.totalCost,
            documentNumber = model.documentNumber,
            isSynced = if (model.isSynced) 1 else 0
        )
    }
}
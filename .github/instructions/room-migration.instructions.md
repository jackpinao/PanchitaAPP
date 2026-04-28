---
description: "Use when adding or modifying Room database entities, columns, tables, or indexes. Covers creating migration classes, updating schema version, and exporting schema JSON."
---

# Room Database Migration Guidelines

## Regla principal

Cada cambio en una `@Entity` **requiere** incrementar la versión de la base de datos y crear una clase `MigrationXToY`.

## Pasos obligatorios

1. **Modifica la entidad** en `data/source/local/database/` (agrega/elimina columna, cambia tipo, etc.)
2. **Incrementa la versión** en `AppDatabase.kt`:
   ```kotlin
   @Database(version = 19, exportSchema = true, ...)
   ```
3. **Crea la migración** en `data/source/local/database/Migration{X}To{Y}.kt`:
   ```kotlin
   val Migration18To19 = object : Migration(18, 19) {
       override fun migrate(db: SupportSQLiteDatabase) {
           db.execSQL("ALTER TABLE products ADD COLUMN barcode TEXT")
       }
   }
   ```
4. **Registra la migración** en `DataModule.kt` dentro del builder de Room:
   ```kotlin
   .addMigrations(Migration17To18, Migration18To19)
   ```
5. **Exporta el schema**: el archivo JSON se generará automáticamente en `app/schemas/` al compilar. Confírmalo corriendo `./gradlew assembleDebug`.

## Convenciones

- Nombre de clase: `Migration{X}To{Y}` (ej. `Migration18To19`)
- Solo usar `ALTER TABLE ADD COLUMN` para nuevas columnas (SQLite no soporta DROP COLUMN sin recrear la tabla)
- Para eliminar/renombrar columnas: crear tabla temporal, copiar datos, eliminar original, renombrar temporal
- `fallbackToDestructiveMigration()` es el último recurso — las migraciones son requeridas en producción

## Archivos clave

- [AppDatabase.kt](../../app/src/main/java/com/pinao/panchitaapp/data/source/local/database/AppDatabase.kt)
- [DataModule.kt](../../app/src/main/java/com/pinao/panchitaapp/di/koin/DataModule.kt)
- [app/schemas/](../../app/schemas/)

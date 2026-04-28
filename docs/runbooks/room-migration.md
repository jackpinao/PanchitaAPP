# Runbook — Migración de Base de Datos Room

Guía paso a paso para aplicar cambios al esquema de Room de forma segura.

> **Versión actual de la DB**: v18 — ver `AppDatabase.kt`
> **Schema exportado en**: `app/schemas/com.pinao.panchitaapp.data.local.database.AppDatabase/`

---

## Regla fundamental

**Cada cambio en una `@Entity` requiere**:
1. Incrementar la versión en `AppDatabase.kt`
2. Crear una clase `Migration{X}To{Y}`
3. Registrar la migración en `DataModule.kt`
4. Exportar y commitear el schema JSON

`fallbackToDestructiveMigration()` solo es aceptable en desarrollo. **En producción siempre se requiere migración explícita.**

---

## Paso a paso

### 1. Modifica la entidad

Agrega, elimina o modifica el campo en el `@Entity` correspondiente en:
`data/source/local/entity/{Entity}Entity.kt`

```kotlin
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val price: Double,
    val barcode: String?   // ← campo nuevo
)
```

### 2. Incrementa la versión de la DB

En `data/source/local/database/AppDatabase.kt`:

```kotlin
@Database(
    entities = [...],
    version = 19,          // ← era 18, ahora 19
    exportSchema = true
)
```

### 3. Crea la clase de migración

Crea el archivo `data/source/local/database/Migration18To19.kt`:

```kotlin
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val Migration18To19 = object : Migration(18, 19) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE products ADD COLUMN barcode TEXT")
    }
}
```

#### Casos especiales de SQL en SQLite

| Operación | SQL |
|-----------|-----|
| Agregar columna | `ALTER TABLE t ADD COLUMN col TYPE` |
| Agregar columna con default | `ALTER TABLE t ADD COLUMN col INTEGER NOT NULL DEFAULT 0` |
| Eliminar columna | Ver [sección Eliminar columna](#eliminar-columna) |
| Renombrar columna (API 26+) | `ALTER TABLE t RENAME COLUMN old TO new` |
| Crear tabla nueva | `CREATE TABLE IF NOT EXISTS t (...)` |
| Crear índice | `CREATE INDEX IF NOT EXISTS idx_t_col ON t(col)` |

> **SQLite no soporta DROP COLUMN directamente en API < 35**. Ver procedimiento abajo.

#### Eliminar columna (procedimiento de tabla temporal)

```kotlin
val Migration18To19 = object : Migration(18, 19) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Crear tabla nueva sin la columna a eliminar
        db.execSQL("""
            CREATE TABLE products_new (
                id INTEGER PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                price REAL NOT NULL
            )
        """)
        // 2. Copiar datos
        db.execSQL("INSERT INTO products_new SELECT id, name, price FROM products")
        // 3. Eliminar tabla original
        db.execSQL("DROP TABLE products")
        // 4. Renombrar nueva tabla
        db.execSQL("ALTER TABLE products_new RENAME TO products")
    }
}
```

### 4. Registra la migración en DataModule

En `di/koin/DataModule.kt`, agrega la migración al builder de Room:

```kotlin
Room.databaseBuilder(androidContext(), AppDatabase::class.java, "panchita_app")
    .addMigrations(
        Migration17To18,
        Migration18To19    // ← nueva migración
    )
    .build()
```

### 5. Exporta el schema JSON

Compila el proyecto para generar el archivo JSON del esquema:

```bash
./gradlew assembleDebug
```

Verifica que se generó el nuevo archivo en:
`app/schemas/com.pinao.panchitaapp.data.local.database.AppDatabase/19.json`

**Commitea este archivo junto con los demás cambios.**

---

## Checklist de verificación

- [ ] `@Entity` modificada con los cambios correctos
- [ ] `AppDatabase.kt` tiene la versión incrementada
- [ ] Clase `Migration{X}To{Y}` creada en `data/source/local/database/`
- [ ] Migración registrada en `DataModule.kt`
- [ ] `./gradlew assembleDebug` exitoso
- [ ] Schema JSON `{N}.json` generado en `app/schemas/`
- [ ] Schema JSON commiteado al repositorio
- [ ] Si hay mapper: `{Entity}Mapper.toDomain()` / `.toDatabase()` actualizados

---

## Solución de problemas

| Error | Causa | Solución |
|-------|-------|---------|
| `IllegalStateException: Migration didn't properly handle` | La migración no coincide con el esquema esperado | Verifica que el SQL de la migración produce exactamente la estructura del `@Entity` |
| `A migration from X to Y was required but not found` | Falta registrar la migración en `DataModule` | Agrega `.addMigrations(MigrationXToY)` |
| Schema JSON no se genera | `exportSchema = false` o path mal configurado | Verifica `exportSchema = true` en `@Database` y la config en `build.gradle.kts` |
| `no such column` en runtime | La migración se ejecutó pero faltó una columna | Revisa el SQL de la migración; usa `adb shell` para inspeccionar (ver runbook `room-schema-inspect.md`) |

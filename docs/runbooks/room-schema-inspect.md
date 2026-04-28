# Runbook — Inspeccionar el Schema de Room

Cómo inspeccionar la base de datos SQLite de Room en un dispositivo o emulador.

---

## Opción A — Android Studio Database Inspector (recomendado)

Disponible para apps en debug conectadas al emulador o dispositivo físico.

1. Abre Android Studio.
2. Menú **View** → **Tool Windows** → **App Inspection**.
3. Selecciona el dispositivo y el proceso `com.pinao.panchitaapp`.
4. Pestaña **Database Inspector**.
5. Se listará la base de datos `panchita_app`.
6. Expande para ver las tablas y haz clic en una para ver sus datos.
7. Puedes ejecutar queries SQL directamente con el botón **Open New Query Tab**.

### Queries útiles

```sql
-- Ver estructura de una tabla
PRAGMA table_info(products);

-- Ver versión actual de la base de datos
PRAGMA user_version;

-- Contar registros
SELECT COUNT(*) FROM products;

-- Ver los últimos 10 productos
SELECT * FROM products ORDER BY id DESC LIMIT 10;

-- Ver todas las tablas
SELECT name FROM sqlite_master WHERE type='table';
```

---

## Opción B — ADB + DB Browser for SQLite

Útil cuando no tienes Android Studio disponible o trabajas con un dispositivo físico sin depuración de base de datos.

### Prerrequisitos

- ADB instalado (`adb --version`)
- [DB Browser for SQLite](https://sqlitebrowser.org/) instalado

### Extraer la base de datos

```bash
# 1. Verificar que el dispositivo está conectado
adb devices

# 2. Copiar la DB al equipo local
adb exec-out run-as com.pinao.panchitaapp cat databases/panchita_app > panchita_app.db
```

> Esto solo funciona en apps debuggeables (`debuggable = true`). No funciona en builds release de producción.

### Abrir en DB Browser for SQLite

1. Abre DB Browser for SQLite.
2. **File** → **Open Database** → selecciona `panchita_app.db`.
3. Pestaña **Database Structure** para ver tablas e índices.
4. Pestaña **Browse Data** para ver registros.
5. Pestaña **Execute SQL** para ejecutar queries.

---

## Verificar schema JSON exportado

Para comparar el esquema actual con el esperado por Room:

```
app/schemas/com.pinao.panchitaapp.data.local.database.AppDatabase/
├── 17.json
├── 18.json    ← versión actual
└── ...
```

Abre el JSON de la versión actual para ver la definición completa de todas las tablas, columnas e índices que Room espera encontrar.

---

## Verificar versión de la base de datos en dispositivo

```bash
adb exec-out run-as com.pinao.panchitaapp sqlite3 databases/panchita_app "PRAGMA user_version;"
```

El número debe coincidir con la versión declarada en `AppDatabase.kt`:
```kotlin
@Database(version = 18, ...)
```

Si el número en el dispositivo es menor que el de la app, Room intentará ejecutar las migraciones pendientes al próximo arranque.

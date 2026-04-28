# ADR-003 — Doble Persistencia: Room + Firebase Firestore

| Campo      | Valor              |
|------------|--------------------|
| **Estado** | Aceptado           |
| **Fecha**  | 2026-04-27         |
| **Autores**| Equipo PanchitaApp |

---

## Contexto

El negocio requiere acceso a los datos desde múltiples dispositivos (por ejemplo, tablet en caja + teléfono para inventario). Solo Room no resuelve este caso; solo Firebase/Retrofit implicaría dependencia de red permanente.

## Decisión

Se usa una **estrategia de doble persistencia**:

| Capa | Tecnología | Rol |
|------|-----------|-----|
| Local | Room (SQLite) | Fuente de verdad para la UI; disponible offline |
| Remota | Firebase Firestore | Sincronización multi-dispositivo; backup en la nube |
| API externa | Retrofit (REST) | Integración con servicios de recargas Claro |

El repositorio (`{Entity}RepositoryImpl`) coordina ambas fuentes:
- **Lectura**: Siempre desde Room (Flow reactivo).
- **Escritura local**: Room primero → `SyncWorker` escribe a Firestore en background.
- **Sincronización descendente**: `RefreshProductsUseCase` y similares traen datos de Firestore y los guardan en Room.

## Consecuencias

**Positivas**
- Datos disponibles offline en todo momento (Room).
- Sincronización entre dispositivos sin intervención del usuario (Firestore).
- La UI solo tiene una fuente de datos (Room), simplificando el estado.

**Negativas / Trade-offs**
- La lógica de sincronización debe manejar conflictos (last-write-wins por timestamp es la política actual).
- Costo operativo de Firebase Firestore según volumen de operaciones.
- `FirebaseProductDataSource` y `RemoteDataSourceImpl` añaden complejidad en la capa de datos.

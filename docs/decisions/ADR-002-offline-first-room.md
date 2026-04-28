# ADR-002 — Offline-First con Room

| Campo      | Valor              |
|------------|--------------------|
| **Estado** | Aceptado           |
| **Fecha**  | 2026-04-27         |
| **Autores**| Equipo PanchitaApp |

---

## Contexto

PanchitaApp opera en un entorno de tienda donde la conectividad a internet puede ser intermitente o inexistente. El negocio no puede detenerse por falta de red: las ventas, el inventario y las recargas deben funcionar en cualquier condición.

## Decisión

Room (SQLite) es la **fuente de verdad local**. La UI nunca espera a la red para mostrar datos ni para procesar una venta.

Patrón de acceso a datos:
1. La UI observa `Flow<List<T>>` expuesto por los DAOs de Room — se actualiza reactivamente cuando cambia la base de datos local.
2. Al iniciar o en background, `SyncWorker` sincroniza con Firebase/Retrofit.
3. Ante cualquier operación de escritura (nueva venta, producto, etc.) se persiste primero en Room y luego se sincroniza en segundo plano.

## Consecuencias

**Positivas**
- La app funciona completamente sin conexión a internet.
- La UI reacciona automáticamente a cambios gracias a `Flow`; no hay polling manual.
- El tiempo de arranque percibido es más rápido (datos locales disponibles de inmediato).

**Negativas / Trade-offs**
- Los datos pueden estar momentáneamente desincronizados entre dispositivos hasta que `SyncWorker` se ejecute.
- La lógica de sincronización y resolución de conflictos agrega complejidad en la capa de datos.
- Se requiere gestión de migraciones de esquema Room ante cada cambio de entidad.

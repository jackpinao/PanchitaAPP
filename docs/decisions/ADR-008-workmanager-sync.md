# ADR-008 — WorkManager para Sincronización en Background

| Campo      | Valor              |
|------------|--------------------|
| **Estado** | Aceptado           |
| **Fecha**  | 2026-04-27         |
| **Autores**| Equipo PanchitaApp |

---

## Contexto

La app necesita sincronizar datos locales (Room) con Firebase Firestore de forma periódica y en background, sin bloquear la UI. El sistema operativo Android puede matar procesos en background, por lo que se necesita un mecanismo que garantice la ejecución incluso si la app es cerrada.

## Decisión

Se usa **WorkManager** para toda la sincronización en background, implementada en `SyncWorker`.

```kotlin
class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val productsRepository: ProductsRepository,
    // otros repos...
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            productsRepository.syncWithFirebase()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
```

WorkManager se configura para ejecutarse con restricción de red disponible y reintentos con backoff exponencial.

## Consecuencias

**Positivas**
- WorkManager garantiza la ejecución incluso si la app es cerrada o el dispositivo reiniciado.
- La UI nunca se bloquea esperando sincronización de red.
- Soporta restricciones (solo con Wi-Fi, solo con batería cargada) de forma declarativa.
- `CoroutineWorker` permite código asíncrono limpio.

**Negativas / Trade-offs**
- La sincronización no es en tiempo real; hay latencia entre la escritura local y la propagación remota.
- WorkManager no garantiza ejecución inmediata; puede diferirse según condiciones del sistema.
- Requiere inyección manual en el Worker a través de una `WorkerFactory` de Koin.

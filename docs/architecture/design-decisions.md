# PanchitaApp — Decisiones de Diseño Clave

| Decisión | Justificación |
|----------|---------------|
| **Offline-first con Room** | Los DAOs exponen `Flow<List<T>>` → la UI reacciona automáticamente cuando cambian los datos locales |
| **Doble persistencia** (Room + Firestore) | Room como fuente de verdad local; Firestore para sincronización multi-dispositivo |
| **Use Case bundles** (`BrandUseCases`, etc.) | Reduce el número de parámetros inyectados en ViewModels agrupando casos de uso relacionados |
| **`Resource<T>` sealed class** | Wrapper uniforme `Success / Error / Loading` para resultados asíncronos en toda la app |
| **`@KoinViewModel` + `@ComponentScan`** | Auto-descubrimiento de ViewModels por KSP, sin registro manual en módulos Koin |
| **Mappers estáticos** | `{Entity}Mapper.toDomain()` / `.toDatabase()` — nunca se mapea inline dentro de repositorios |
| **WorkManager para sync** | `SyncWorker` ejecuta sincronización en background sin bloquear la UI |

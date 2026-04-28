# ADR-005 — Resource\<T\> Sealed Class

| Campo      | Valor              |
|------------|--------------------|
| **Estado** | Aceptado           |
| **Fecha**  | 2026-04-27         |
| **Autores**| Equipo PanchitaApp |

---

## Contexto

Las operaciones asíncronas (llamadas a red, escrituras en base de datos, autenticación) pueden estar en tres estados: en progreso, exitosas o fallidas. Sin un wrapper estándar, cada feature maneja estos estados de forma diferente, aumentando la inconsistencia y duplicación de código.

## Decisión

Se usa una **sealed class `Resource<T>`** como wrapper uniforme para todos los resultados asíncronos:

```kotlin
sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String, val data: T? = null) : Resource<T>()
    class Loading<T> : Resource<T>()
}
```

Ubicación: `utils/Resource.kt`

Uso estándar en repositorios e implementaciones:

```kotlin
fun syncProducts(): Flow<Resource<List<Product>>> = flow {
    emit(Resource.Loading())
    try {
        val products = remoteDataSource.getProducts()
        emit(Resource.Success(products))
    } catch (e: Exception) {
        emit(Resource.Error(e.message ?: "Error desconocido"))
    }
}
```

Los ViewModels hacen `collect` del Flow y mapean cada estado al `UiState` correspondiente.

## Consecuencias

**Positivas**
- Interfaz consistente para manejar estados asíncronos en toda la app.
- El compilador obliga a manejar todos los casos con `when`.
- Facilita el testing: se puede emitir `Loading`, `Success` y `Error` en tests de flujo.

**Negativas / Trade-offs**
- `Loading` no transporta datos parciales; si se necesita (p. ej., paginación), se debe extender.
- Wrappear todos los resultados puede ser verboso para operaciones simples que raramente fallan.

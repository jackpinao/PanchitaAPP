# ADR-007 — Mappers Estáticos

| Campo      | Valor              |
|------------|--------------------|
| **Estado** | Aceptado           |
| **Fecha**  | 2026-04-27         |
| **Autores**| Equipo PanchitaApp |

---

## Contexto

La app tiene tres representaciones de datos para cada entidad: el modelo de dominio (puro Kotlin), la entidad Room (`@Entity`) y el DTO de red. Convertir entre ellas inline dentro de repositorios o use cases mezcla responsabilidades y hace el código difícil de testear y mantener.

## Decisión

Cada entidad tiene un objeto **Mapper** dedicado con métodos de extensión:

```kotlin
object ProductMapper {
    fun ProductEntity.toDomain(): Product = Product(
        id = this.id,
        name = this.name,
        price = this.price,
        // ...
    )

    fun Product.toDatabase(): ProductEntity = ProductEntity(
        id = this.id,
        name = this.name,
        price = this.price,
        // ...
    )

    fun ProductDto.toDomain(): Product = Product(
        id = this.productId,
        name = this.productName,
        // ...
    )
}
```

Ubicación: `data/mapper/{Entity}Mapper.kt`

**Regla**: Nunca mapear inline dentro de un repositorio, use case o ViewModel.

## Consecuencias

**Positivas**
- Las transformaciones son testeables de forma aislada.
- Un único lugar para actualizar cuando cambia el esquema Room o el contrato de la API.
- El código de repositorio queda limpio y centrado en la lógica de orquestación.

**Negativas / Trade-offs**
- Requiere crear un archivo Mapper por cada entidad, incluso para entidades simples.
- Al agregar un campo a una entidad, hay que actualizar el Mapper, la Entity y el Domain Model.

# ADR-004 — Use Case Bundles

| Campo      | Valor              |
|------------|--------------------|
| **Estado** | Aceptado           |
| **Fecha**  | 2026-04-27         |
| **Autores**| Equipo PanchitaApp |

---

## Contexto

Un ViewModel como `AddProductViewModel` necesita acceder a múltiples casos de uso: guardar producto, escanear código de barras, obtener marcas, obtener categorías. Inyectar cada `UseCase` como parámetro individual genera constructores con muchos parámetros, dificultando la legibilidad y el testing.

## Decisión

Los casos de uso relacionados con una misma entidad o módulo se agrupan en una clase **bundle** (data class o clase contenedora):

```kotlin
data class ProductUseCases(
    val getAllProducts: GetAllProductsUseCase,
    val deleteProduct: DeleteProductUseCase,
    val saveProduct: SaveProductsUseCase,
    val refreshProducts: RefreshProductsUseCase
)
```

El ViewModel recibe el bundle como único parámetro de inyección:

```kotlin
class AddProductViewModel(
    private val productUseCases: ProductUseCases,
    private val brandUseCases: BrandUseCases,
    private val categoryUseCases: CategoryUseCases
) : ViewModel()
```

Los bundles se registran en `DomainModule` con `@Factory`.

## Consecuencias

**Positivas**
- Constructores de ViewModel más limpios y legibles.
- Agregar un nuevo caso de uso al bundle no requiere modificar los ViewModels que no lo usan.
- En tests, se puede mockear el bundle completo o individualmente cada use case.

**Negativas / Trade-offs**
- Un ViewModel recibe todos los casos de uso del bundle, aunque solo use algunos.
- Requiere mantener el bundle actualizado cada vez que se añade un nuevo use case a esa entidad.

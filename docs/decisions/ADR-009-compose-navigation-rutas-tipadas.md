# ADR-009 — Compose Navigation con Rutas Tipadas

| Campo      | Valor              |
|------------|--------------------|
| **Estado** | Aceptado           |
| **Fecha**  | 2026-04-27         |
| **Autores**| Equipo PanchitaApp |

---

## Contexto

La app tiene múltiples pantallas con una estructura de navegación que incluye un drawer modal y soporte para layouts responsivos en tablets. La navegación necesita ser type-safe para evitar errores de strings de ruta y para soportar paso de argumentos sin conversiones manuales.

## Decisión

Se usa **Compose Navigation** con **rutas tipadas** (type-safe navigation via objetos/clases Kotlin serializables).

### Estructura

- `AllDestinations.kt` — Define todos los objetos de ruta (sealed class o data objects).
- `AppNavGraph.kt` — NavHost principal que incluye el drawer modal y el layout responsivo para tablets.
- `AppNavigationActions.kt` — Funciones de navegación reutilizables (encapsulan `navController.navigate(...)`).

```kotlin
// AllDestinations.kt
@Serializable object LoginDestination
@Serializable object HomeDestination
@Serializable data class ProductDetailDestination(val productId: Int)

// AppNavGraph.kt
NavHost(navController = navController, startDestination = LoginDestination) {
    composable<LoginDestination> { LoginScreen(...) }
    composable<HomeDestination> { HomeScreen(...) }
    composable<ProductDetailDestination> { backStackEntry ->
        val args = backStackEntry.toRoute<ProductDetailDestination>()
        ProductDetailScreen(productId = args.productId, ...)
    }
}
```

## Consecuencias

**Positivas**
- El compilador detecta rutas y argumentos incorrectos en tiempo de compilación.
- No hay casting manual de argumentos String/Int desde el bundle de navegación.
- El NavGraph es fácil de leer y extender; cada feature agrega su `composable<Destination>`.

**Negativas / Trade-offs**
- Requiere que las clases de destino sean `@Serializable` (dependencia `kotlinx-serialization`).
- Para navegación profunda (deep links) desde notificaciones, se necesita configuración adicional.
- El `AppNavGraph.kt` puede crecer si no se modulariza en grafos anidados por feature.

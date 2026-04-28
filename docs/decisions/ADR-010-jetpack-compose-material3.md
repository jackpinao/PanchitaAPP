# ADR-010 — Jetpack Compose + Material 3

| Campo      | Valor              |
|------------|--------------------|
| **Estado** | Aceptado           |
| **Fecha**  | 2026-04-27         |
| **Autores**| Equipo PanchitaApp |

---

## Contexto

La app necesita una UI moderna, mantenible y adaptable a distintos tamaños de pantalla (teléfono + tablet). El sistema de diseño debe ser coherente, soportar theming dinámico y facilitar el desarrollo de componentes reutilizables.

## Decisión

Se usa **Jetpack Compose** como toolkit de UI y **Material 3 (Material You)** como sistema de diseño.

### Organización del tema

- `presentation/theme/` contiene:
  - `Color.kt` — Paleta de colores (tokens semánticos MD3).
  - `Type.kt` — Escala tipográfica MD3.
  - `Theme.kt` — `MaterialTheme` con `ColorScheme` y `Typography`.

### Patrones UI adoptados

- Estado de pantalla en `UiState` inmutable dentro del ViewModel; la UI es función del estado.
- Componentes compartidos en `presentation/common/` (botones custom, campos de texto, loaders).
- Layout responsivo en `AppNavGraph.kt`: drawer modal en teléfono, navigation rail/drawer permanente en tablet.
- `AppDrawer.kt` implementa el `ModalNavigationDrawer` de MD3.

### Convención de Composables

```kotlin
@Composable
fun AddProductScreen(
    uiState: AddProductUiState,
    onEvent: (AddProductEvent) -> Unit,
    // solo lambdas y primitivos — no ViewModel directo
)
```

Los Composables de pantalla no reciben el ViewModel directamente; reciben estado y callbacks para facilitar el preview y el testing.

## Consecuencias

**Positivas**
- UI declarativa: menos bugs de estado visual al ser función del `UiState`.
- Material 3 provee tokens de color/tipografía/forma consistentes y soporte para Dynamic Color (Android 12+).
- Los Composables son fáciles de hacer preview con `@Preview` sin necesitar dispositivo.
- Compose Navigation y el sistema de temas se integran nativamente.

**Negativas / Trade-offs**
- Compose tiene una curva de aprendizaje para quien viene de XML/Fragments.
- Las herramientas de profiling de Compose (recomposiciones) requieren familiarización.
- Algunos componentes legacy o de terceros pueden no tener soporte nativo de Compose y requieren `AndroidView`.

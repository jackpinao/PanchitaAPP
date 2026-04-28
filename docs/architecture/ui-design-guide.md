# PanchitaApp — Guía de Diseño UI

## Sistema de Colores

El tema usa **Material 3** con soporte para **Dynamic Color** (Android 12+).

### Paleta base (fallback cuando Dynamic Color no está disponible)

| Token | Modo Claro | Modo Oscuro | Uso |
|-------|-----------|------------|-----|
| `primary` | `#6650A4` (Purple40) | `#D0BCFF` (Purple80) | Botones principales, FAB, elementos destacados |
| `secondary` | `#625B71` (PurpleGrey40) | `#CCC2DC` (PurpleGrey80) | Chips, elementos secundarios |
| `tertiary` | `#7D5260` (Pink40) | `#EFB8C8` (Pink80) | Acentos, badges |

### Uso de colores en Compose

```kotlin
// Siempre usar tokens semánticos del tema, nunca valores hardcodeados
MaterialTheme.colorScheme.primary      // ✅
Color(0xFF6650A4)                       // ❌ (fuera del sistema de diseño)
```

### Dynamic Color

Habilitado por defecto en Android 12+ (`dynamicColor = true` en `PanchitaAPPTheme`).  
En versiones anteriores se usa la paleta base.

---

## Tipografía

Basada en **Material 3 Typography** con `FontFamily.Default` (Roboto del sistema).

| Estilo | Tamaño | Peso | Uso |
|--------|--------|------|-----|
| `bodyLarge` | 16sp | Normal | Texto principal, campos de formulario |
| `titleLarge` | 22sp | Normal | Títulos de pantalla (Material default) |
| `labelSmall` | 11sp | Medium | Etiquetas, chips pequeños |
| `headlineSmall` | 24sp | Normal | Encabezados de sección |

### Regla de tipografía

```kotlin
// Usar siempre estilos del tema
Text("Hola", style = MaterialTheme.typography.bodyLarge)  // ✅
Text("Hola", fontSize = 16.sp)                             // ❌
```

---

## Espaciado y Dimensiones

No hay un sistema de spacing codificado aún. Convenciones observadas en el código:

| Uso | Valor |
|-----|-------|
| Padding interno de pantalla | `16.dp` |
| Separación entre secciones | `12.dp` – `16.dp` |
| Espaciado de items en drawer | `NavigationDrawerItemDefaults.ItemPadding` |
| Separación vertical entre cards | `8.dp` |

> **Regla**: Usar múltiplos de 4dp. Preferir `8.dp`, `12.dp`, `16.dp`, `24.dp`, `32.dp`.

---

## Componentes Reutilizables

### `Screen` — Contenedor base de pantalla

Archivo: `presentation/ui/Screen.kt`

Envuelve todo el contenido de una pantalla en `MaterialTheme` + `Surface` con `fillMaxSize`.  
**Todas las pantallas deben usar este wrapper como raíz.**

```kotlin
@Composable
fun Screen(modifier: Modifier = Modifier, content: @Composable () -> Unit)
```

Uso:
```kotlin
@Composable
fun MiPantallaScreen(...) {
    Screen {
        // contenido aquí
    }
}
```

---

## Patrones de Layout

### Pantalla estándar

```
Screen {
    Scaffold(
        topBar = { TopAppBar(...) },
        floatingActionButton = { ... }
    ) { paddingValues ->
        // contenido con padding
    }
}
```

### Pantalla con scroll

Usar `TopAppBarDefaults.pinnedScrollBehavior()` + `Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)` en el Scaffold para colapsar el TopAppBar al hacer scroll.

### Listas

Usar `LazyColumn` / `LazyRow` para listas de tamaño desconocido.  
Usar `Column` / `Row` solo para listas cortas y fijas (≤ 5 items).

---

## Navegación Visual

- **Drawer**: `ModalNavigationDrawer` con `ModalDrawerSheet` — acceso desde el ícono de menú del `TopAppBar`.
- **Rutas tipadas**: definidas en `AllDestinations.kt`.
- El drawer se cierra con `scope.launch { drawerState.close() }` al seleccionar un ítem.

---

## Tema

### Aplicar el tema

El tema `PanchitaAPPTheme` se aplica una sola vez en el punto de entrada de la UI (`MainActivity`).  
**No** anidar llamadas a `PanchitaAPPTheme` dentro de composables.

```kotlin
// MainActivity / punto de entrada
PanchitaAPPTheme {
    AppNavGraph(...)
}
```

### Modo oscuro

Soportado automáticamente. El tema respeta `isSystemInDarkTheme()`.  
No hardcodear colores que no funcionen en ambos modos.

---

## Íconos

Usar `Icons.Default.*` o `Icons.Filled.*` de `androidx.compose.material.icons`.  
Para iconos personalizados, añadirlos como `ImageVector` en `presentation/theme/`.

---

## Reglas Generales

- Toda pantalla usa `Screen {}` como raíz.
- Nunca hardcodear colores, tamaños de texto o fuentes fuera del sistema de tema.
- Los estados de carga deben mostrar `CircularProgressIndicator` con `color = MaterialTheme.colorScheme.primary`.
- Los estados de error deben usar `Snackbar` del `SnackbarHost` del `Scaffold`.
- Las acciones destructivas (eliminar, etc.) deben pedir confirmación con `AlertDialog`.

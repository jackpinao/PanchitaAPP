# ADR-006 — Koin 4 + KSP con @KoinViewModel y @ComponentScan

| Campo      | Valor              |
|------------|--------------------|
| **Estado** | Aceptado           |
| **Fecha**  | 2026-04-27         |
| **Autores**| Equipo PanchitaApp |

---

## Contexto

La app necesita un framework de inyección de dependencias. Se evaluaron Hilt (Dagger) y Koin. Hilt genera mucho boilerplate con anotaciones de componentes y módulos de Android. Con Koin 4 y KSP, los ViewModels pueden ser auto-descubiertos sin registro manual.

## Decisión

Se usa **Koin 4** como framework de DI, integrado con **KSP** para la generación de código en tiempo de compilación.

### Estructura de módulos

| Módulo | Contenido | Scope |
|--------|----------|-------|
| `DataModule` | DB, Firebase, Retrofit, DAOs, Repositorios | `@Single` |
| `DomainModule` | Use Cases y bundles | `@Factory` |
| `PresentationModule` | `@ComponentScan` → auto-descubre `@KoinViewModel` | — |

### Reglas de scope

- `@Single`: `AppDatabase`, instancias de Firebase, `SessionManager`, `Retrofit`.
- `@Factory`: Use Cases y bundles (nueva instancia por inyección).
- `@KoinViewModel`: ViewModels — descubiertos automáticamente por KSP.

### Inyección en Compose

```kotlin
@Composable
fun AddProductScreen(
    viewModel: AddProductViewModel = koinViewModel()
) { ... }
```

## Consecuencias

**Positivas**
- No se requiere registrar cada ViewModel manualmente en un módulo Koin.
- KSP genera el código de descubrimiento en tiempo de compilación (no reflexión en runtime).
- Koin es más sencillo de configurar y depurar que Hilt para proyectos medianos.

**Negativas / Trade-offs**
- `@ComponentScan` requiere que todos los ViewModels estén en el paquete escaneado.
- Koin no verifica el grafo de dependencias en tiempo de compilación (a diferencia de Hilt/Dagger).
- Al actualizar Koin, hay que revisar compatibilidad de anotaciones KSP.

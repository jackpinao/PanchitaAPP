# PanchitaApp 🛒

Aplicación Android de Punto de Venta (POS) para gestión de ventas, inventario, recargas telefónicas y clientes.

---

## Stack Tecnológico

| Área | Tecnología |
|------|-----------|
| UI | Jetpack Compose + Material 3 |
| Arquitectura | Clean Architecture + MVVM |
| DI | Koin 4 + KSP |
| Base de datos local | Room (SQLite) |
| Base de datos remota | Firebase Firestore |
| Red | Retrofit 2 |
| Navegación | Compose Navigation (rutas tipadas) |
| Background sync | WorkManager |
| Tests | JUnit + MockK + Turbine |
| Cobertura | Kover (mín. 60%) |

- **Java 17** · **Kotlin 2.1.0** · **minSdk 26** · **compileSdk 36**

---

## Arquitectura

Clean Architecture estricta con 3 capas. Las dependencias solo apuntan hacia adentro:

```
Presentation  ──►  Domain  ──►  Data
```

| Capa | Ubicación | Responsabilidad |
|------|-----------|-----------------|
| Presentation | `presentation/ui/{feature}/` | ViewModels + Compose screens |
| Domain | `domain/` | Use cases, interfaces de repositorio, modelos de dominio |
| Data | `data/` | Room, Firebase, Retrofit, implementaciones de repositorio, mappers |

---

## Funcionalidades

| Feature | Descripción |
|---------|-------------|
| **Auth** | Login con Firebase Authentication |
| **Inventario** | Listado, búsqueda y gestión de productos |
| **Agregar Producto** | Alta/edición con cálculo PPP (Precio Promedio Ponderado), soporte IGV y percepción |
| **Módulo de Venta** | Registro de ventas con carrito temporal |
| **Recargas Claro** | Registro y consulta de recargas telefónicas |
| **Categorías** | Gestión de categorías de producto |

---

## Comandos de Build y Test

```bash
# Build
./gradlew assembleDebug          # APK debug → app/build/outputs/apk/debug/
./gradlew assembleRelease        # APK release → app/build/outputs/apk/release/

# Tests
./gradlew test                   # Unit tests
./gradlew connectedAndroidTest   # Instrumented tests
./gradlew koverHtmlReport        # Reporte de cobertura (mín. 60%)

# Herramientas
./gradlew lint                   # Análisis estático
./gradlew generateKotzillaJson   # Regenerar config de analytics
```

> **Nota:** Para ejecutar Gradle en Windows sin Android Studio, usar el JDK incluido:
> ```powershell
> $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
> .\gradlew.bat test
> ```

---

## Estructura del Proyecto

```
app/src/main/java/com/pinao/panchitaapp/
├── data/              # Implementaciones de repos, Room, Firebase, Retrofit, mappers
├── di/koin/           # Módulos de Koin (DataModule, DomainModule, PresentationModule)
├── domain/            # Use cases, interfaces de repos, modelos, Resource<T>
├── presentation/
│   ├── navigation/    # AppNavGraph, AllDestinations
│   └── ui/{feature}/  # Screens + ViewModels por funcionalidad
└── utils/             # Utilidades compartidas (PriceUtils, etc.)
```

---

## Base de Datos (Room v18)

- Nombre: `panchita_app`
- Schema exportado en `app/schemas/`
- Toda modificación de entidades **requiere** una clase `Migration{X}To{Y}` y exportar el schema.

---

## Convenciones de Nombrado

| Tipo | Patrón | Ejemplo |
|------|--------|---------|
| ViewModel | `{Feature}ViewModel` | `AddProductViewModel` |
| Use Case | `{Action}{Entity}UseCase` | `GetAllProductsUseCase` |
| Repository impl | `{Entity}RepositoryImpl` | `ProductsRepositoryImpl` |
| Mapper | `{Entity}Mapper` | `ProductMapper` |
| UI State | `{Feature}UiState` | `AddProductUiState` |

---

## Documentación

- [Arquitectura](docs/architecture/overview.md)
- [Decisiones de diseño (ADRs)](docs/decisions/README.md)
- [Runbooks operativos](docs/runbooks/README.md)
- [Guía de UI](docs/architecture/ui-design-guide.md)

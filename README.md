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
| Base de datos remota | Supabase (PostgreSQL + RLS) |
| Red | Supabase Client (Ktor) |
| Navegación | Compose Navigation (rutas tipadas) |
| Impresión Bluetooth | RFCOMM Sockets (ESC/POS 80mm) |
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
| **Auth** | Login con Supabase Auth |
| **Inventario** | Listado, búsqueda y gestión de productos |
| **Agregar Producto** | Alta/edición con ingreso manual de precios, cálculo de costo unitario (PPP), soporte multi-tenant y autogeneración de códigos de barras |
| **Módulo de Venta** | Registro de ventas con carrito temporal e impresión directa de ticket térmico de 80mm vía Bluetooth |
| **Venta Rápida** | Punto de venta independiente del stock con carga manual (Nombre/Precio/Cantidad), impresión de ticket de 80mm vía Bluetooth y exportación PDF |
| **Impresión Bluetooth** | Conexión directa a impresoras térmicas de 80mm (48 columnas) vía Bluetooth SPP, con selección interactiva de impresora mediante Bottom Sheet, persistencia de preferencias de dispositivo, formateador ESC/POS robusto adaptado para caracteres latinos, cabeceras dinámicas basadas en los datos de la tienda sincronizados desde Supabase y precorte (pre-cut) parcial obligatorio al final del ticket. |
| **Módulo de Ajustes** | Pantalla dedicada de Configuraciones accesible desde el menú lateral para establecer/cambiar la impresora predeterminada, listar los dispositivos vinculados, disparar la impresión de un ticket de prueba formateado, y personalizar localmente los datos del negocio (Nombre de tienda, RUC, Dirección y Teléfono). |
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
├── data/              # Repos, Room, Supabase DataSources, mappers, DTOs
├── di/koin/           # Módulos de Koin (DataModule, DomainModule, PresentationModule)
├── domain/            # Use cases, interfaces de repos, modelos, Resource<T>
├── presentation/
│   ├── navigation/    # AppNavGraph, AllDestinations
│   └── ui/{feature}/  # Screens + ViewModels por funcionalidad
└── utils/             # Utilidades compartidas (PriceUtils, DateUtils, etc.)
```

---

## Base de Datos (Room v23)

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

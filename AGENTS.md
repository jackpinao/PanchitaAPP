# PanchitaApp — Agent Instructions

Android POS (Point of Sale) app for managing sales, inventory, phone recharges, and clients. Uses **Clean Architecture + MVVM + Jetpack Compose**.

## Rules
- no hagas build en cada cambio que hagas.
- Siempre asegúrate de importar las clases y archivos necesarios al realizar cambios o crear nuevos archivos.
- Antes de hacer un commit con cambios de consideración (nueva feature, cambio de arquitectura, nueva dependencia, migración Room, nuevas pantallas), actualiza `README.md` e inclúyelo en el mismo commit. Ver `.github/instructions/update-readme.instructions.md` para la lista completa de criterios.

## Skills
- **material-3** (`.github/skills/material-3/SKILL.md`): Úsalo siempre que implementes o modifiques UI con Jetpack Compose Material3 — componentes, tokens de color/tipografía/forma, temas, layouts adaptativos y auditoría MD3.

## Instructions
- **room-migration** (`.github/instructions/room-migration.instructions.md`): Aplica siempre que modifiques entidades Room — migraciones, versión de schema, exportación de JSON.
- **update-readme** (`.github/instructions/update-readme.instructions.md`): Aplica antes de cualquier commit con cambios de consideración — actualizar `README.md` e incluirlo en el commit.

## Build & Test Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew test                   # Unit tests
./gradlew connectedAndroidTest   # Instrumented tests
./gradlew koverHtmlReport        # Code coverage report (min 60%)
./gradlew generateKotzillaJson   # Regenerate analytics config
```

- **Java 17**, **Kotlin 2.1.0**, **minSdk 26**, **compileSdk 36**
- Dependencies in [gradle/libs.versions.toml](gradle/libs.versions.toml)

## Architecture

Clean Architecture with 3 layers. **Never skip layers** — data layer must not be accessed directly from presentation.

```
presentation/ → domain/ → data/
```

| Layer | Location | Responsibility |
|-------|----------|---------------|
| Presentation | `presentation/ui/{feature}/` | ViewModels + Compose screens |
| Domain | `domain/` | Use cases, repository interfaces, domain models |
| Data | `data/` | Room, Firebase, Retrofit, repository implementations, mappers |

### Patterns to Follow

- **ViewModel**: Holds `StateFlow<UiState>` (immutable data class). Updates via `.copy()`. Async with `viewModelScope.launch`.
- **Use Cases**: Single responsibility. Grouped into bundles (e.g., `ProductUseCases`) for injection.
- **Repository**: Interface in `domain/repository/`, implementation in `data/repository/`.
- **Mappers**: `{Entity}Mapper.toDomain()` / `.toDatabase()` — never map inline.
- **Resource**: Use `Resource<T>` sealed class (`Success`, `Error`, `Loading`) for async results.
- **Offline-first**: Always cache to Room; sync from Firebase/Retrofit.

## Dependency Injection — Koin 4

Modules are in [di/koin/](app/src/main/java/com/pinao/panchitaapp/di/koin/):

| Module | Contents |
|--------|---------|
| `DataModule` | Database, Firebase, Retrofit, DAOs, Repositories |
| `DomainModule` | Use Cases (`@Factory` for most, `@Single` for singletons) |
| `PresentationModule` | Use case bundles; `@ComponentScan` picks up `@KoinViewModel` classes |

- Use `@KoinViewModel` on ViewModels — auto-discovered by `@ComponentScan`.
- Inject ViewModels in Compose via `koinViewModel()`.
- Use `@Single` for: Database, Firebase, SessionManager. Use `@Factory` for: Use Cases.

## Navigation

Compose Navigation with typed routes. See [presentation/navigation/](app/src/main/java/com/pinao/panchitaapp/presentation/navigation/):
- Routes defined in `AllDestinations.kt`
- NavHost in `AppNavGraph.kt` (includes modal drawer + responsive layout for tablets)
- Add new screens: declare route in `AllDestinations`, add composable in `AppNavGraph`, add drawer entry if needed.

## Database — Room (v18)

- DB name: `panchita_app` | Schema exported to [app/schemas/](app/schemas/)
- **Always** export schema and create a migration class when changing entities.
- Migration classes go in `data/source/local/database/` (e.g., `Migration17To18.kt`).
- `fallbackToDestructiveMigration()` is the fallback — migrations are required for production.
- Session data stored in `panchita_prefs` via `SessionManager`.

## Testing Conventions

- **Unit tests**: [app/src/test/](app/src/test/java/com/pinao/panchitaapp/)
- **ViewModel tests**: Use `MainDispatcherRule` + MockK + Turbine for Flow assertions.
- **Flow testing**: `viewModel.uiState.test { }` with Turbine.
- **Assertions**: Use Google Truth (`assertThat(...).isEqualTo(...)`).
- Coverage exclusions: DI modules, Activities, Composables, generated code, data models.

## Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| ViewModel | `{Feature}ViewModel` | `AddProductViewModel` |
| Use Case | `{Action}{Entity}UseCase` | `GetAllProductsUseCase` |
| Repository impl | `{Entity}RepositoryImpl` | `ProductsRepositoryImpl` |
| Mapper | `{Entity}Mapper` | `ProductMapper` |
| Remote data source | `{Entity}RemoteDataSource` | `ProductRemoteDataSource` |
| Firebase data source | `Firebase{Entity}DataSource` | `FirebaseProductDataSource` |
| UI State | `{Feature}UiState` | `AddProductUiState` |

## Key Files

| File | Purpose |
|------|---------|
| [PanchitaApp.kt](app/src/main/java/com/pinao/panchitaapp/PanchitaApp.kt) | App entry point, Koin initialization |
| [AppNavGraph.kt](app/src/main/java/com/pinao/panchitaapp/presentation/navigation/AppNavGraph.kt) | Full navigation graph |
| [AppDatabase.kt](app/src/main/java/com/pinao/panchitaapp/data/source/local/database/AppDatabase.kt) | Room DB with all DAOs |
| [DataModule.kt](app/src/main/java/com/pinao/panchitaapp/di/koin/DataModule.kt) | DB, Firebase, Retrofit wiring |
| [Resource.kt](app/src/main/java/com/pinao/panchitaapp/utils/Resource.kt) | Async result wrapper |
| [gradle/libs.versions.toml](gradle/libs.versions.toml) | All dependency versions |

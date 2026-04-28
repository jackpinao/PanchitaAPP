---
name: add-feature
description: "Creates the complete skeleton of a new feature in PanchitaApp following Clean Architecture + MVVM. Use when adding a new screen, module, or feature end-to-end: domain model, repository interface, repository implementation, use cases, ViewModel, Compose screen, Koin wiring, and navigation."
argument-hint: "Feature name in PascalCase (e.g. 'StockEntry', 'Supplier', 'Promotion')"
---

# Add Feature Skill

Genera el esqueleto completo de una nueva feature siguiendo Clean Architecture + MVVM para PanchitaApp.

## Procedimiento

### 1. Recopila contexto

Lee estos archivos para entender los patrones existentes:
- Un modelo existente en `domain/model/` (ej. [ProductModel.kt](../../app/src/main/java/com/pinao/panchitaapp/domain/model/))
- Una interfaz de repositorio en `domain/repository/`
- Una implementación de repositorio en `data/repository/`
- Un ViewModel existente en `presentation/ui/`
- [AllDestinations.kt](../../app/src/main/java/com/pinao/panchitaapp/presentation/navigation/AllDestinations.kt)
- [AppNavGraph.kt](../../app/src/main/java/com/pinao/panchitaapp/presentation/navigation/AppNavGraph.kt)
- [DataModule.kt](../../app/src/main/java/com/pinao/panchitaapp/di/koin/DataModule.kt)
- [DomainModule.kt](../../app/src/main/java/com/pinao/panchitaapp/di/koin/DomainModule.kt)
- [PresentationModule.kt](../../app/src/main/java/com/pinao/panchitaapp/di/koin/PresentationModule.kt)

### 2. Archivos a crear

Para una feature llamada `{Feature}` (ej. `Promotion`), crea estos archivos en orden:

#### Domain Layer
| Archivo | Ruta |
|---------|------|
| `{Feature}Model.kt` | `domain/model/` |
| `{Feature}Repository.kt` (interfaz) | `domain/repository/` |
| `Get{Feature}ListUseCase.kt` | `domain/usecase/{feature}/` |
| `Add{Feature}UseCase.kt` | `domain/usecase/{feature}/` |
| `{Feature}UseCases.kt` (bundle) | `domain/usecase/{feature}/` |

#### Data Layer
| Archivo | Ruta |
|---------|------|
| `{Feature}Entity.kt` | `data/source/local/database/entity/` |
| `{Feature}Dao.kt` | `data/source/local/database/dao/` |
| `{Feature}Mapper.kt` | `data/mapper/` |
| `{Feature}RepositoryImpl.kt` | `data/repository/` |

#### Presentation Layer
| Archivo | Ruta |
|---------|------|
| `{Feature}ViewModel.kt` | `presentation/ui/{feature}/` |
| `{Feature}Screen.kt` | `presentation/ui/{feature}/` |
| `{Feature}UiState.kt` | `presentation/ui/{feature}/` |

### 3. Patrones a seguir

**UiState** — siempre data class inmutable:
```kotlin
data class {Feature}UiState(
    val isLoading: Boolean = false,
    val items: List<{Feature}Model> = emptyList(),
    val error: String? = null
)
```

**ViewModel** — `@KoinViewModel`, `StateFlow`:
```kotlin
@KoinViewModel
class {Feature}ViewModel(
    private val useCases: {Feature}UseCases
) : ViewModel() {
    private val _uiState = MutableStateFlow({Feature}UiState())
    val uiState: StateFlow<{Feature}UiState> = _uiState.asStateFlow()
}
```

**Mapper** — extensiones en la clase Mapper:
```kotlin
object {Feature}Mapper {
    fun {Feature}Entity.toDomain(): {Feature}Model = ...
    fun {Feature}Model.toDatabase(): {Feature}Entity = ...
}
```

### 4. Registro en Koin

En `DataModule.kt`:
```kotlin
single { db.{feature}Dao() }
factory<{Feature}Repository> { {Feature}RepositoryImpl(get()) }
```

En `DomainModule.kt`:
```kotlin
factory { Get{Feature}ListUseCase(get()) }
factory { Add{Feature}UseCase(get()) }
```

En `PresentationModule.kt`:
```kotlin
factory { {Feature}UseCases(get(), get()) }
```

### 5. Registro en Navigation

En `AllDestinations.kt`:
```kotlin
const val {FEATURE}_ROUTE = "{feature}"
```

En `AppNavGraph.kt`:
```kotlin
composable(AllDestinations.{FEATURE}_ROUTE) {
    {Feature}Screen(viewModel = koinViewModel())
}
```

### 6. Room — si la feature tiene entidad nueva

- Incrementa la versión de `AppDatabase` (+1)
- Agrega la entidad a `@Database(entities = [...])`
- Agrega el DAO abstracto
- Crea `Migration{X}To{Y}.kt`
- Registra la migración en `DataModule.kt`
- Exporta el schema con `./gradlew assembleDebug`

> Ver instrucciones detalladas en [room-migration.instructions.md](../../.github/instructions/room-migration.instructions.md)

### 7. Verificación final

```bash
./gradlew assembleDebug   # Sin errores de compilación
./gradlew test            # Tests existentes siguen pasando
```

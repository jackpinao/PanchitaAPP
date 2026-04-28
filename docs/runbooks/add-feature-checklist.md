# Runbook — Checklist para Agregar un Nuevo Feature

Lista de verificación completa para implementar un nuevo módulo en PanchitaApp siguiendo Clean Architecture + MVVM.

> Para la guía detallada de creación de features, ver el skill en `.github/skills/add-feature/SKILL.md`.

---

## Checklist

### Capa de Dominio

- [ ] **Modelo de dominio** creado en `domain/model/{Entity}.kt`
  - Data class pura Kotlin, sin anotaciones de Android ni Room
- [ ] **Interfaz de repositorio** creada en `domain/repository/{Entity}Repository.kt`
- [ ] **Use Cases** creados en `domain/usecase/{feature}/`
  - Un archivo por caso de uso: `{Action}{Entity}UseCase.kt`
  - Bundle creado si hay más de 2 use cases: `{Entity}UseCases.kt`
- [ ] Use Cases inyectan solo la interfaz del repositorio (nunca la implementación)

### Capa de Datos

- [ ] **Room Entity** creada en `data/source/local/entity/{Entity}Entity.kt`
  - Si modifica el esquema: migración Room aplicada (ver [room-migration.md](room-migration.md))
- [ ] **DAO** creado en `data/source/local/dao/{Entity}Dao.kt`
  - Métodos retornan `Flow<List<T>>` para queries de lectura
- [ ] **DAO registrado** en `AppDatabase.kt`
- [ ] **Mapper** creado en `data/mapper/{Entity}Mapper.kt`
  - `{Entity}Entity.toDomain()` y `Domain.toDatabase()`
  - Si hay DTO remoto: `{Entity}Dto.toDomain()`
- [ ] **Firebase data source** creado (si aplica): `data/source/remote/firebase/Firebase{Entity}DataSource.kt`
- [ ] **Implementación del repositorio** creada en `data/repository/{Entity}RepositoryImpl.kt`
  - Implementa la interfaz de dominio
  - Usa mapper para todas las conversiones (nunca inline)
  - Retorna `Resource<T>` para operaciones asíncronas

### Inyección de Dependencias (Koin)

- [ ] DAO registrado en `DataModule.kt`
- [ ] Firebase data source registrado en `DataModule.kt` con `@Single`
- [ ] Repository registrado en `DataModule.kt` (`@Single`, bind a interfaz)
- [ ] Use Cases / bundle registrados en `DomainModule.kt` con `@Factory`
- [ ] ViewModel tiene anotación `@KoinViewModel` (auto-descubierto por `@ComponentScan`)

### Capa de Presentación

- [ ] **UiState** definido: `{Feature}UiState.kt` (data class inmutable)
- [ ] **ViewModel** creado en `presentation/ui/{feature}/{Feature}ViewModel.kt`
  - Expone `StateFlow<{Feature}UiState>`
  - Actualiza estado con `.copy()`
  - Operaciones async en `viewModelScope.launch`
- [ ] **Compose Screen** creada en `presentation/ui/{feature}/{Feature}Screen.kt`
  - Recibe `uiState` y `onEvent` como parámetros (no el ViewModel directamente)
  - Usa tokens de color/tipografía de `MaterialTheme` (no valores hardcodeados)

### Navegación

- [ ] **Destino** declarado en `AllDestinations.kt`
- [ ] **composable<Destination>** agregado en `AppNavGraph.kt`
- [ ] Si aparece en el drawer: entrada agregada en `AppDrawer.kt`

### Tests

- [ ] Test del ViewModel creado en `test/.../ui/{feature}/{Feature}ViewModelTest.kt`
  - Usa `MainDispatcherRule`
  - Mockea use cases con MockK
  - Usa Turbine para assertions de Flow
  - Assertions con Google Truth (`assertThat(...)`)
- [ ] Tests del use case creados (si tiene lógica no trivial)
- [ ] Tests del mapper creados

### Schema y Build

- [ ] Si hubo cambios en Room: schema JSON en `app/schemas/` commiteado
- [ ] `./gradlew assembleDebug` pasa sin errores
- [ ] `./gradlew test` pasa sin errores

---

## Nomenclatura de referencia rápida

| Tipo | Patrón | Ejemplo |
|------|--------|---------|
| ViewModel | `{Feature}ViewModel` | `AddProductViewModel` |
| Use Case | `{Action}{Entity}UseCase` | `GetAllProductsUseCase` |
| Repository impl | `{Entity}RepositoryImpl` | `ProductsRepositoryImpl` |
| Mapper | `{Entity}Mapper` | `ProductMapper` |
| Firebase data source | `Firebase{Entity}DataSource` | `FirebaseProductDataSource` |
| UI State | `{Feature}UiState` | `AddProductUiState` |

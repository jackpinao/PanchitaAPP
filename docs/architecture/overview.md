# PanchitaApp — Visión General de Arquitectura

PanchitaApp es un POS (Point of Sale) Android que sigue **Clean Architecture + MVVM** con 3 capas estrictas:

```
Presentation  ──►  Domain  ──►  Data
```

Las dependencias solo apuntan hacia adentro. La capa de dominio es pura Kotlin, sin dependencias de Android ni de frameworks externos.

---

## Principios Fundamentales

- **Offline-first**: Room es la fuente de verdad local; Firebase sincroniza en segundo plano.
- **Unidireccionalidad**: `Screen → ViewModel → UseCase → Repository` — nunca al revés.
- **Capas estrictas**: La capa de presentación nunca accede directamente a la capa de datos.
- **ViewModel con StateFlow**: Estado inmutable `UiState` actualizado con `.copy()`.
- **Resource\<T\>**: Wrapper uniforme `Success / Error / Loading` para resultados asíncronos.

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
| Navegación | Compose Navigation (typed routes) |
| Background sync | WorkManager |
| Tests | JUnit + MockK + Turbine + Truth |
| Cobertura | Kover (mín. 60%) |

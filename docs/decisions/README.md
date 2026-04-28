# Decisiones de Arquitectura — PanchitaApp

Este directorio contiene los **Architecture Decision Records (ADRs)** del proyecto PanchitaApp.

Un ADR documenta una decisión de arquitectura significativa: el contexto que la motivó, la decisión tomada y sus consecuencias.

---

## Índice

| ADR | Título | Estado |
|-----|--------|--------|
| [ADR-001](ADR-001-clean-architecture-mvvm.md) | Clean Architecture + MVVM | ✅ Aceptado |
| [ADR-002](ADR-002-offline-first-room.md) | Offline-First con Room | ✅ Aceptado |
| [ADR-003](ADR-003-doble-persistencia-room-firestore.md) | Doble Persistencia: Room + Firebase Firestore | ✅ Aceptado |
| [ADR-004](ADR-004-use-case-bundles.md) | Use Case Bundles | ✅ Aceptado |
| [ADR-005](ADR-005-resource-sealed-class.md) | Resource\<T\> Sealed Class | ✅ Aceptado |
| [ADR-006](ADR-006-koin-ksp-component-scan.md) | Koin 4 + KSP con @KoinViewModel y @ComponentScan | ✅ Aceptado |
| [ADR-007](ADR-007-mappers-estaticos.md) | Mappers Estáticos | ✅ Aceptado |
| [ADR-008](ADR-008-workmanager-sync.md) | WorkManager para Sincronización en Background | ✅ Aceptado |
| [ADR-009](ADR-009-compose-navigation-rutas-tipadas.md) | Compose Navigation con Rutas Tipadas | ✅ Aceptado |
| [ADR-010](ADR-010-jetpack-compose-material3.md) | Jetpack Compose + Material 3 | ✅ Aceptado |

---

## Cómo añadir un nuevo ADR

1. Crea un archivo `ADR-NNN-titulo-corto.md` con el siguiente número secuencial.
2. Usa esta estructura:

```markdown
# ADR-NNN — Título

| Campo      | Valor   |
|------------|---------|
| **Estado** | Propuesto / Aceptado / Deprecado / Reemplazado por ADR-XXX |
| **Fecha**  | YYYY-MM-DD |
| **Autores**| ... |

## Contexto
(¿Por qué se tomó esta decisión? ¿Qué problema resuelve?)

## Decisión
(¿Qué se decidió exactamente?)

## Consecuencias
(Positivas y negativas / trade-offs)
```

3. Agrega la entrada al índice de este README.

---

## Estados posibles

| Estado | Significado |
|--------|------------|
| **Propuesto** | En discusión, aún no implementado |
| **Aceptado** | Implementado y vigente |
| **Deprecado** | Ya no se sigue pero no ha sido reemplazado |
| **Reemplazado** | Sustituido por otro ADR (indicar cuál) |

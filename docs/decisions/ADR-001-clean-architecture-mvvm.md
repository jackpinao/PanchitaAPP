# ADR-001 — Clean Architecture + MVVM

| Campo      | Valor                  |
|------------|------------------------|
| **Estado** | Aceptado               |
| **Fecha**  | 2026-04-27             |
| **Autores**| Equipo PanchitaApp     |

---

## Contexto

PanchitaApp es un POS Android que necesita gestionar ventas, inventario, recargas y clientes.
La app requiere:
- Ser testeada de forma independiente en cada capa (lógica de negocio, UI, persistencia).
- Ser extendida con nuevos módulos sin romper los existentes.
- Soportar múltiples fuentes de datos (Room local + Firebase remoto + Retrofit API).

## Decisión

Se adopta **Clean Architecture** con 3 capas estrictas combinada con el patrón **MVVM**:

```
Presentation  ──►  Domain  ──►  Data
```

- **Presentation**: ViewModels + Compose Screens. Solo conoce el dominio.
- **Domain**: Use Cases + interfaces de Repository + modelos puros Kotlin. Sin dependencias de Android ni frameworks.
- **Data**: Implementaciones de Room, Firebase y Retrofit. Nunca accedida directamente desde Presentation.

El flujo de datos es unidireccional:

```
Screen → ViewModel → UseCase → Repository → DataSource
```

## Consecuencias

**Positivas**
- La capa de dominio es completamente testeable con JUnit puro, sin necesidad de Android instrumentation tests.
- Cambiar de Room a otra base de datos solo afecta la capa de datos.
- Cada capa puede evolucionar de forma independiente.

**Negativas / Trade-offs**
- Mayor cantidad de archivos y clases para features simples.
- Curva de aprendizaje inicial para nuevos colaboradores.
- Requiere disciplina para no saltar capas (mitigado con revisiones de código y estas ADRs).

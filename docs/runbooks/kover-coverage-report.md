# Runbook — Reporte de Cobertura con Kover

Cómo generar, leer y mantener el reporte de cobertura de código de PanchitaApp.

---

## Generar el reporte

```bash
./gradlew koverHtmlReport
```

**Reporte generado en**: `app/build/reports/kover/html/index.html`

Abre el archivo en cualquier navegador para ver la cobertura interactiva.

### Reporte en formato XML (para CI/CD)

```bash
./gradlew koverXmlReport
```

**XML generado en**: `app/build/reports/kover/report.xml`

---

## Cobertura mínima requerida

**Mínimo: 60%** de cobertura de líneas en código de negocio.

Si la cobertura cae por debajo del umbral, el build de CI debe fallar. Esto está configurado en `app/build.gradle.kts` bajo el bloque de Kover:

```kotlin
kover {
    reports {
        verify {
            rule {
                minBound(60) // mínimo 60%
            }
        }
    }
}
```

Para verificar el umbral sin generar el reporte HTML:

```bash
./gradlew koverVerify
```

---

## Leer el reporte HTML

Al abrir `index.html`, verás:

| Columna | Significado |
|---------|------------|
| **Class** | Nombre de la clase |
| **Method coverage** | % de métodos cubiertos por tests |
| **Line coverage** | % de líneas ejecutadas durante tests |
| **Branch coverage** | % de ramas (if/when) cubiertas |

### Cómo navegar

- Haz clic en un paquete para ver las clases que lo componen.
- Haz clic en una clase para ver línea a línea qué está cubierto (verde) y qué no (rojo).

---

## Exclusiones de cobertura

El código generado, DI, Activities y Composables están excluidos de la medición. Esto está configurado en `app/build.gradle.kts`:

```kotlin
kover {
    reports {
        filters {
            excludes {
                classes(
                    "*.di.*",                          // Módulos Koin
                    "*.MainActivity*",
                    "*ComposableSingletons*",          // Código generado por Compose
                    "*.BuildConfig",
                    "com.pinao.panchitaapp.*.*\$*"    // Lambdas generadas
                )
            }
        }
    }
}
```

**No exclusiones válidas**: Use Cases, ViewModels, Repositories, Mappers — todo el código de negocio debe tener cobertura.

---

## Qué cubrir prioritariamente

| Componente | Prioridad | Herramientas |
|-----------|-----------|-------------|
| Use Cases | Alta | JUnit + MockK |
| ViewModels | Alta | `MainDispatcherRule` + Turbine + MockK |
| Mappers | Media | JUnit puro |
| Repository implementations | Media | MockK (mockear DAOs y data sources) |

---

## Flujo para aumentar cobertura

1. Corre `./gradlew koverHtmlReport`
2. Abre el reporte y ordena por **Line coverage** ascendente
3. Identifica Use Cases o ViewModels sin tests
4. Crea tests siguiendo el patrón de `test/java/com/pinao/panchitaapp/`
5. Vuelve a correr el reporte para verificar mejora

---

## Integración con CI

En el pipeline de CI, agregar el paso de verificación antes del build de release:

```bash
./gradlew test koverVerify
```

Si `koverVerify` falla, el build se detiene y no se genera el APK.

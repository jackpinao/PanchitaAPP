# Runbooks — PanchitaApp

Guías operativas para tareas frecuentes y resolución de problemas en PanchitaApp.

---

## Índice

### Configuración y Setup

| Runbook | Descripción |
|---------|------------|
| [new-developer-setup.md](new-developer-setup.md) | Configurar el entorno de desarrollo desde cero |
| [google-services-setup.md](google-services-setup.md) | Obtener y configurar `google-services.json` |

### Base de Datos

| Runbook | Descripción |
|---------|------------|
| [room-migration.md](room-migration.md) | Crear y aplicar migraciones de Room |
| [room-schema-inspect.md](room-schema-inspect.md) | Inspeccionar la DB SQLite en dispositivo |

### Firebase

| Runbook | Descripción |
|---------|------------|
| [firebase-sync-troubleshooting.md](firebase-sync-troubleshooting.md) | Diagnosticar fallos en `SyncWorker` y sincronización |
| [firebase-rules-deploy.md](firebase-rules-deploy.md) | Actualizar y publicar reglas de seguridad de Firestore |

### Build y Despliegue

| Runbook | Descripción |
|---------|------------|
| [build-and-release.md](build-and-release.md) | Generar APK debug/release y AAB para Google Play |

### Desarrollo

| Runbook | Descripción |
|---------|------------|
| [add-feature-checklist.md](add-feature-checklist.md) | Checklist completo para agregar un nuevo feature |

### Calidad y Monitoreo

| Runbook | Descripción |
|---------|------------|
| [kover-coverage-report.md](kover-coverage-report.md) | Generar y leer el reporte de cobertura Kover |
| [crashlytics-triage.md](crashlytics-triage.md) | Priorizar y accionar sobre crashes en Crashlytics |

---

## Cómo agregar un nuevo runbook

1. Crea el archivo `nombre-descriptivo.md` en esta carpeta.
2. Usa la siguiente estructura mínima:

```markdown
# Runbook — Título

Descripción breve de cuándo usar este runbook.

---

## Prerrequisitos
(herramientas, accesos o condiciones necesarias)

## Pasos
(numerados, con comandos exactos)

## Solución de problemas
(tabla de errores comunes y sus soluciones)
```

3. Agrega la entrada al índice de este README.

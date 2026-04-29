---
description: "Update README.md before committing significant changes. Applies when adding features, modifying architecture, changing dependencies, updating build config, or altering public-facing behavior."
---

# Instrucción: Actualizar README antes de commit

## Regla

Antes de ejecutar cualquier `git commit` que incluya cambios de consideración, **actualiza `README.md`** en la raíz del proyecto para reflejar el estado actual del código.

## ¿Qué se considera "cambio de consideración"?

- Agregar o eliminar una **funcionalidad/feature** completa
- Modificar la **arquitectura** o estructura de capas
- Agregar, eliminar o actualizar **dependencias** relevantes en `gradle/libs.versions.toml` o `app/build.gradle.kts`
- Cambiar versiones de `versionCode` / `versionName`
- Cambiar el **schema de Room** (nueva migración)
- Agregar o renombrar **pantallas o módulos** de navegación
- Modificar **comandos de build/test** o la configuración de Gradle

## Secciones del README que deben mantenerse al día

| Cambio realizado | Sección(es) a revisar |
|-----------------|----------------------|
| Nueva feature/pantalla | `## Funcionalidades`, `## Estructura del Proyecto` |
| Nueva dependencia o actualización de versión | `## Stack Tecnológico` |
| Cambio de `versionCode`/`versionName` | No aplica en README (se documenta en releases) |
| Nueva migración Room | `## Base de Datos` |
| Nuevo comando de build/test | `## Comandos de Build y Test` |
| Cambio de convenciones de nombrado | `## Convenciones de Nombrado` |
| Cambio en arquitectura o capas | `## Arquitectura` |

## Flujo obligatorio antes de commit

1. Identifica si el cambio es de consideración (ver lista anterior).
2. Si lo es, abre `README.md` y actualiza las secciones afectadas.
3. Incluye `README.md` en el mismo commit (`git add README.md`).
4. El mensaje del commit debe reflejar tanto el cambio funcional como la actualización del README cuando corresponda.

## Ejemplo de commit correcto

```bash
git add app/src/.../NuevaFeature.kt README.md
git commit -m "feat(nueva-feature): agregar pantalla X con ViewModel y use cases"
```

## Lo que NO requiere actualizar el README

- Corrección de bugs internos sin cambio de interfaz pública
- Refactors que no cambian comportamiento ni estructura visible
- Actualizaciones de tests unitarios (sin nuevo feature)
- Cambios en archivos de configuración de lint o calidad de código

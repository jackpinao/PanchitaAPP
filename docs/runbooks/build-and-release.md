# Runbook — Build y Release

Proceso completo para generar builds de debug y release de PanchitaApp.

---

## Build de Debug

Uso: desarrollo, QA interno, pruebas en dispositivo.

```bash
./gradlew assembleDebug
```

**APK generado**: `app/build/outputs/apk/debug/app-debug.apk`

### Instalar directamente en dispositivo conectado

```bash
./gradlew installDebug
```

---

## Build de Release

### Prerrequisitos: Keystore de firma

La app release **debe estar firmada**. El keystore no se almacena en el repositorio.

Solicita al líder del proyecto:
- Archivo `panchita-release.jks` (o `.keystore`)
- `keyAlias`
- `keyPassword`
- `storePassword`

### Configurar credenciales de firma

**Opción A — `local.properties`** (recomendado, no se commitea):

```properties
# local.properties
RELEASE_STORE_FILE=/ruta/absoluta/panchita-release.jks
RELEASE_STORE_PASSWORD=tu_store_password
RELEASE_KEY_ALIAS=tu_key_alias
RELEASE_KEY_PASSWORD=tu_key_password
```

**Opción B — Variables de entorno** (para CI/CD):

```bash
export RELEASE_STORE_FILE=/ruta/panchita-release.jks
export RELEASE_STORE_PASSWORD=...
export RELEASE_KEY_ALIAS=...
export RELEASE_KEY_PASSWORD=...
```

### Generar el APK release

```bash
./gradlew assembleRelease
```

**APK generado**: `app/build/outputs/apk/release/app-release.apk`

> Si la firma no está configurada se genera `app-release-unsigned.apk` que no puede instalarse directamente.

### Generar AAB para Google Play

```bash
./gradlew bundleRelease
```

**AAB generado**: `app/build/outputs/bundle/release/app-release.aab`

---

## Tests antes de release

Siempre correr tests antes de generar un release:

```bash
# Unit tests
./gradlew test

# Reporte de cobertura (mín. 60%)
./gradlew koverHtmlReport
# Reporte en: app/build/reports/kover/html/index.html
```

---

## Checklist de release

- [ ] Tests unitarios pasan (`./gradlew test`)
- [ ] Cobertura ≥ 60% (`./gradlew koverHtmlReport`)
- [ ] `versionCode` incrementado en `app/build.gradle.kts`
- [ ] `versionName` actualizado (semver: MAJOR.MINOR.PATCH)
- [ ] Schema de Room commiteado si hubo migraciones
- [ ] APK/AAB firmado con el keystore de producción
- [ ] APK instalado y probado manualmente en dispositivo físico

---

## Versioning

En `app/build.gradle.kts`:

```kotlin
defaultConfig {
    versionCode = 10          // ← incrementar por cada release
    versionName = "1.2.0"     // ← semver
}
```

| Campo | Regla |
|-------|-------|
| `versionCode` | Entero incremental. Google Play lo usa para detectar actualizaciones. Nunca repetir. |
| `versionName` | Semver `MAJOR.MINOR.PATCH`. Solo informativo para el usuario. |

---

## Solución de problemas

| Error | Causa | Solución |
|-------|-------|---------|
| `Keystore file not found` | Ruta incorrecta en `local.properties` | Verifica la ruta absoluta del archivo `.jks` |
| `Failed to read key from keystore` | `keyAlias` o contraseña incorrectos | Verifica credenciales con el líder del proyecto |
| `Duplicate class` en release | Dependencias conflictivas con R8 | Revisa reglas en `proguard-rules.pro` |
| APK muy grande | Sin minificación activada | Verifica `isMinifyEnabled = true` en el bloque `release` de `build.gradle.kts` |

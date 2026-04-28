# Runbook — Setup de Nuevo Desarrollador

Guía para configurar el entorno de desarrollo de PanchitaApp desde cero en Windows o macOS.

---

## Prerrequisitos de Sistema

| Herramienta | Versión requerida | Descarga |
|-------------|-------------------|---------|
| JDK | **17** (LTS) | [Adoptium Temurin 17](https://adoptium.net/) |
| Android Studio | Ladybug o superior | [developer.android.com/studio](https://developer.android.com/studio) |
| Git | Cualquier versión reciente | `git --version` |

> **Importante**: Confirma que `JAVA_HOME` apunta a JDK 17.
> ```bash
> java -version   # debe mostrar openjdk 17.x.x
> ```

---

## 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd PanchitaAPP
```

---

## 2. Configurar `local.properties`

El archivo `local.properties` **no se commitea** y debe existir en la raíz del proyecto.

```properties
# local.properties
sdk.dir=/Users/<tu-usuario>/Library/Android/sdk        # macOS
# sdk.dir=C\:\\Users\\<tu-usuario>\\AppData\\Local\\Android\\Sdk  # Windows
```

Android Studio lo genera automáticamente al abrir el proyecto. Solo verifica que exista.

---

## 3. Configurar Firebase (`google-services.json`)

El archivo `google-services.json` **no está en el repositorio** (contiene credenciales).

1. Solicita el archivo al líder del proyecto o descárgalo desde [Firebase Console](https://console.firebase.google.com/).
2. Colócalo en: `app/google-services.json`

Sin este archivo **la app no compila**.

> Ver runbook completo: [google-services-setup.md](google-services-setup.md)

---

## 4. Abrir en Android Studio

1. Abre Android Studio → **Open** → selecciona la carpeta `PanchitaAPP`.
2. Espera a que Gradle sync termine (puede tardar varios minutos la primera vez).
3. Si hay errores de sync, verifica que `local.properties` exista y que `google-services.json` esté en `app/`.

---

## 5. Verificar el build

```bash
./gradlew assembleDebug
```

APK generado en: `app/build/outputs/apk/debug/app-debug.apk`

Si el build es exitoso, el entorno está configurado correctamente.

---

## 6. Ejecutar tests unitarios

```bash
./gradlew test
```

---

## 7. Configuración de Android Studio recomendada

- **Kotlin**: Asegúrate de tener el plugin de Kotlin actualizado (Help → Check for Updates).
- **KSP**: No se necesita configuración adicional; está declarado en `gradle/libs.versions.toml`.
- **Emulador**: API 26 mínimo (`minSdk = 26`). Recomendado: API 33+ para probar Dynamic Color (Material You).

---

## Estructura del proyecto

```
PanchitaAPP/
├── app/
│   ├── google-services.json       ← NO en repo; solicitar al equipo
│   └── src/main/java/com/pinao/panchitaapp/
│       ├── presentation/          ← ViewModels + Compose screens
│       ├── domain/                ← Use Cases + interfaces
│       ├── data/                  ← Room + Firebase + Retrofit
│       └── di/koin/               ← Módulos de inyección de dependencias
├── gradle/
│   └── libs.versions.toml         ← Versiones de todas las dependencias
└── local.properties               ← NO en repo; generado localmente
```

---

## Problemas comunes

| Error | Causa | Solución |
|-------|-------|---------|
| `google-services.json not found` | Falta el archivo de Firebase | Ver sección 3 |
| `SDK location not found` | Falta `local.properties` | Abrir proyecto con Android Studio para generarlo |
| `Unsupported class file major version` | JDK incorrecto | Cambiar a JDK 17 en Android Studio (File → Project Structure → SDK Location) |
| `Gradle sync failed` | Problemas de red o caché | `./gradlew --refresh-dependencies` |
| Errores de KSP | Caché corrupta de KSP | `./gradlew clean` luego volver a sync |

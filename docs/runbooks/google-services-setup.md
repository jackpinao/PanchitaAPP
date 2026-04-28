# Runbook — Configurar google-services.json

Cómo obtener y configurar el archivo `google-services.json` en PanchitaApp.

---

## ¿Qué es google-services.json?

Es el archivo de configuración que conecta la app con el proyecto Firebase. Contiene:
- Project ID y App ID de Firebase
- Claves de API para los servicios habilitados (Auth, Firestore, Crashlytics)
- Sender ID para notificaciones push (si aplica)

**Este archivo contiene credenciales sensibles y NO debe commitearse al repositorio.**
Está incluido en `.gitignore`.

---

## Ubicación requerida

```
app/
└── google-services.json    ← debe estar exactamente aquí
```

Sin este archivo, la compilación falla con:
```
File google-services.json is missing.
```

---

## Cómo obtener el archivo

### Opción A — Desde Firebase Console (acceso directo)

1. Abre [Firebase Console](https://console.firebase.google.com/)
2. Selecciona el proyecto **PanchitaApp**
3. Haz clic en el ícono de engranaje (⚙) → **Configuración del proyecto**
4. En la sección **Tus apps**, selecciona la app Android (`com.pinao.panchitaapp`)
5. Haz clic en **Descargar google-services.json**
6. Mueve el archivo descargado a `app/google-services.json`

### Opción B — Solicitar al equipo

Si no tienes acceso a Firebase Console, solicita el archivo directamente al líder del proyecto.

---

## Verificar que está correctamente configurado

El `package_name` dentro del JSON debe coincidir con el application ID de la app:

```json
{
  "client": [
    {
      "client_info": {
        "android_client_info": {
          "package_name": "com.pinao.panchitaapp"   ← debe coincidir
        }
      }
    }
  ]
}
```

El application ID está en `app/build.gradle.kts`:
```kotlin
applicationId = "com.pinao.panchitaapp"
```

---

## Múltiples ambientes (Debug vs Release)

Si el proyecto tiene apps Firebase distintas para debug y release, se pueden colocar en:

```
app/src/debug/google-services.json      ← para builds debug
app/src/release/google-services.json    ← para builds release
app/google-services.json                ← fallback para ambos
```

---

## Rotación de credenciales

Si se sospecha que las credenciales fueron expuestas:

1. En Firebase Console → **Configuración del proyecto** → **Cuentas de servicio** → regenerar claves si aplica.
2. Para la clave de API de Android: **Google Cloud Console** → **APIs & Services** → **Credentials** → regenerar la clave de API restringida a `com.pinao.panchitaapp`.
3. Distribuir el nuevo `google-services.json` al equipo de forma segura (nunca por canales públicos).
4. Notificar al equipo para que actualicen sus copias locales.

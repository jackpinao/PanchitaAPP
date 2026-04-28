# Runbook — Firebase Sync Troubleshooting

Guía para diagnosticar y resolver problemas cuando `SyncWorker` falla o los datos no llegan a Firestore.

---

## Síntomas comunes

| Síntoma | Posible causa |
|---------|--------------|
| Datos locales correctos pero no aparecen en otro dispositivo | `SyncWorker` no se ejecutó o falló silenciosamente |
| `SyncWorker` siempre en estado `FAILED` | Error de red, autenticación o reglas de Firestore |
| Datos en Firestore desactualizados | Worker diferido por restricciones del sistema (batería, sin red) |
| Crash al sincronizar | Excepción no capturada en `SyncWorker.doWork()` |

---

## 1. Verificar el estado de WorkManager

### Desde Android Studio (Device Explorer)

En Android Studio → **App Inspection** → **Background Task Inspector** → busca `SyncWorker`.

Estados posibles:

| Estado | Significado |
|--------|------------|
| `ENQUEUED` | En cola, esperando condiciones |
| `RUNNING` | Ejecutándose actualmente |
| `SUCCEEDED` | Completado con éxito |
| `FAILED` | Falló; ver logs de Crashlytics o Logcat |
| `BLOCKED` | Esperando condición (red, batería) |

### Desde Logcat

```
tag: SyncWorker
```

O filtrar por nivel ERROR para ver excepciones.

---

## 2. Verificar conectividad y restricciones del Worker

`SyncWorker` tiene restricción de red disponible. Si el dispositivo no tiene conexión, el worker se difiere automáticamente.

Verifica en Background Task Inspector que las **constraints** del worker estén satisfechas:
- `NetworkType.CONNECTED` — requiere cualquier tipo de red

Para forzar ejecución inmediata en debug (sin restricciones):

```kotlin
// Solo para testing — NO usar en producción
val request = OneTimeWorkRequestBuilder<SyncWorker>()
    .build()
WorkManager.getInstance(context).enqueue(request)
```

---

## 3. Verificar autenticación de Firebase

La sincronización requiere que el usuario esté autenticado. Si la sesión expiró, Firestore rechazará las escrituras.

Verifica en **Firebase Console** → **Authentication** que el usuario tiene una sesión activa.

Desde Logcat busca errores como:
```
PERMISSION_DENIED: Missing or insufficient permissions
```

→ Solución: El usuario debe volver a iniciar sesión. Verifica `SessionManager` y `SignInUseCase`.

---

## 4. Verificar reglas de seguridad de Firestore

Si `SyncWorker` recibe errores de permisos, las reglas de Firestore pueden estar mal configuradas.

Desde **Firebase Console** → **Firestore Database** → **Rules**:

```javascript
// Reglas mínimas para PanchitaApp (usuarios autenticados)
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

Para probar reglas antes de publicar: usa el **Rules Playground** en Firebase Console.

> Ver runbook completo: [firebase-rules-deploy.md](firebase-rules-deploy.md)

---

## 5. Verificar la implementación de SyncWorker

Revisa `data/service/SyncWorker.kt`:

- `doWork()` debe retornar `Result.success()` en caso exitoso.
- Excepciones deben retornar `Result.retry()` (no `Result.failure()`) para que WorkManager reintente.
- `Result.failure()` solo cuando el error es definitivo y no tiene sentido reintentar.

```kotlin
override suspend fun doWork(): Result {
    return try {
        productsRepository.syncWithFirebase()
        Result.success()
    } catch (e: Exception) {
        if (runAttemptCount < 3) Result.retry() else Result.failure()
    }
}
```

---

## 6. Inspeccionar datos en Firestore directamente

Desde **Firebase Console** → **Firestore Database** → navega a la colección correspondiente (ej. `products`) y verifica si los documentos existen y tienen los campos esperados.

---

## Escalación

Si ninguno de los pasos anteriores resuelve el problema:

1. Revisar Crashlytics para excepciones del `SyncWorker`: [crashlytics-triage.md](crashlytics-triage.md)
2. Revisar el historial de ejecuciones en Background Task Inspector
3. Reproducir el error en debug con logs adicionales en `SyncWorker.doWork()`

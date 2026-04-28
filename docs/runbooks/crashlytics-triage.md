# Runbook — Triage de Crashes en Crashlytics

Cómo leer, priorizar y accionar sobre crashes reportados en Firebase Crashlytics.

---

## Acceder a Crashlytics

[Firebase Console](https://console.firebase.google.com/) → proyecto PanchitaApp → **Crashlytics** (en el menú lateral bajo "Quality").

---

## Entender el dashboard

### Métricas principales

| Métrica | Qué mide | Cuándo preocuparse |
|---------|----------|-------------------|
| **Crash-free users** | % de usuarios sin crash en el período | < 99% requiere atención |
| **Crash count** | Total de eventos de crash | Pico repentino = regresión en último release |
| **Affected users** | Usuarios únicos afectados | Crash que afecta > 1% de usuarios = crítico |

---

## Priorizar un crash

Al hacer clic en un issue de Crashlytics, revisar en orden:

1. **Usuarios afectados** — ¿cuántos usuarios impacta?
2. **Versiones de app afectadas** — ¿es una regresión del último release?
3. **Stack trace** — ¿en qué capa ocurre?
4. **Frecuencia** — ¿es constante o intermitente?

### Escala de severidad

| Severidad | Criterio | Acción |
|-----------|----------|--------|
| 🔴 Crítico | Crash en flujo principal (login, venta, inventario) o > 5% usuarios | Hotfix inmediato |
| 🟠 Alto | Crash en feature secundaria o 1-5% usuarios | Incluir en próximo sprint |
| 🟡 Medio | Crash intermitente o < 1% usuarios | Backlog priorizado |
| 🟢 Bajo | Edge case, 1-2 usuarios reportados | Backlog normal |

---

## Leer un Stack Trace

Ejemplo de stack trace típico en PanchitaApp:

```
Fatal Exception: java.lang.NullPointerException
  at com.pinao.panchitaapp.presentation.ui.addProduct.AddProductViewModel.onEvent(AddProductViewModel.kt:87)
  at com.pinao.panchitaapp.presentation.ui.addProduct.AddProductScreen$...$invoke(AddProductScreen.kt:134)
```

**Lectura del stack trace**:
- La línea más alta es el origen real del crash.
- Busca la primera línea con `com.pinao.panchitaapp` — ese es el código propio.
- Las líneas de Android framework o Compose son el contexto de ejecución, no la causa.

---

## Crashlytics y ofuscación R8

Los builds release están ofuscados con R8. Crashlytics desofusca automáticamente los stack traces si el mapping file fue subido.

El mapping file se sube automáticamente durante el build release si está configurado en `build.gradle.kts`:

```kotlin
buildTypes {
    release {
        firebaseCrashlytics {
            mappingFileUploadEnabled = true
        }
    }
}
```

Si el stack trace se ve ofuscado (clases con nombres `a.b.c`), el mapping file no fue subido. Reconstruye el release y verifica la configuración.

---

## Reproducir el crash localmente

1. Anota el **OS Version**, **Device** y **App Version** desde el panel de Crashlytics.
2. Si hay **Keys** o **Logs** personalizados en el issue, úsalos para entender el estado de la app en el momento del crash.
3. Usa un emulador o dispositivo con las mismas características para reproducir.

### Agregar logs y keys de Crashlytics para debug futuro

```kotlin
// En ViewModel o UseCase — antes de operaciones riesgosas
FirebaseCrashlytics.getInstance().setCustomKey("product_id", productId)
FirebaseCrashlytics.getInstance().log("Iniciando guardado de producto $productId")
```

---

## Marcar un issue como resuelto

Una vez corregido el crash:
1. En Crashlytics → issue → **Close issue**.
2. Si el crash reaparece en una versión futura, Crashlytics lo reabrirá automáticamente como regresión.

---

## Alertas automáticas

Configura alertas para recibir notificaciones ante nuevos crashes o regresiones:

Firebase Console → Crashlytics → ⚙ **Settings** → **Email Alerts** → habilitar para nuevos issues y regresiones.

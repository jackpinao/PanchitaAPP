# Runbook — Desplegar Reglas de Seguridad de Firestore

Cómo revisar, probar y publicar las reglas de seguridad de Firestore para PanchitaApp.

---

## Prerequisitos

- Acceso a [Firebase Console](https://console.firebase.google.com/) con rol **Editor** o **Owner** del proyecto.
- (Opcional) Firebase CLI instalado para deploy desde terminal.

### Instalar Firebase CLI

```bash
npm install -g firebase-tools
firebase login
```

---

## 1. Ver las reglas actuales

**Desde Firebase Console**:
Firebase Console → proyecto PanchitaApp → **Firestore Database** → pestaña **Rules**.

**Desde terminal** (requiere Firebase CLI):

```bash
firebase firestore:rules:get --project <project-id>
```

---

## 2. Reglas base de PanchitaApp

La app usa autenticación Firebase Auth. Las reglas mínimas permiten acceso solo a usuarios autenticados:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Productos, categorías, marcas — solo usuarios autenticados
    match /products/{docId} {
      allow read, write: if request.auth != null;
    }

    match /categories/{docId} {
      allow read, write: if request.auth != null;
    }

    match /brands/{docId} {
      allow read, write: if request.auth != null;
    }

    match /clients/{docId} {
      allow read, write: if request.auth != null;
    }

    match /tickets/{docId} {
      allow read, write: if request.auth != null;
    }

    match /rechanges/{docId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

---

## 3. Probar reglas antes de publicar

Usar el **Rules Playground** en Firebase Console (pestaña Rules → Playground):

1. Selecciona operación: `get`, `list`, `create`, `update`, `delete`.
2. Ingresa el path del documento (ej. `/products/producto1`).
3. Selecciona si el request está autenticado o no.
4. Haz clic en **Run** → debe mostrar `Allow` o `Deny`.

**Verifica siempre**:
- ✅ Usuario autenticado puede leer y escribir
- ✅ Usuario no autenticado recibe `PERMISSION_DENIED`

---

## 4. Publicar reglas

### Desde Firebase Console

1. Edita las reglas en la pestaña **Rules**.
2. Haz clic en **Publish**.

### Desde terminal (Firebase CLI)

```bash
# 1. Inicializa Firebase en el proyecto (solo primera vez)
firebase init firestore

# 2. Las reglas quedan en firestore.rules
# Edita el archivo y luego despliega:
firebase deploy --only firestore:rules --project <project-id>
```

---

## 5. Monitorear errores de permisos post-deploy

Después de publicar reglas nuevas, revisa en **Firebase Console** → **Firestore** → **Usage** si hay un aumento en errores de `PERMISSION_DENIED`.

Si hay errores inesperados:
1. Revisa los logs en Firebase Console → **Functions** (si aplica) o Logcat en la app.
2. Vuelve a las reglas anteriores desde el historial en la pestaña Rules → **History**.

---

## Historial de reglas

Firebase guarda un historial de versiones de reglas. Para revertir:

Firebase Console → Firestore → Rules → **History** → selecciona versión anterior → **Revert**.

# PanchitaApp

Una aplicación Android moderna desarrollada en Kotlin para [descripción breve de la app, ej: gestión de recargas y usuarios].

## 🚀 Características

- **Arquitectura Limpia**: Implementada con capas de dominio, presentación y datos.
- **Inyección de Dependencias**: Soporte para Hilt y Koin.
- **Base de Datos Local**: Utiliza Room para almacenamiento persistente.
- **Red**: Integración con APIs para usuarios y recargas.
- **UI Moderna**: Interfaz de usuario con Jetpack Compose (asumiendo basado en la estructura).
- **Firebase**: Integración para autenticación y servicios en la nube.

## 🛠 Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **Framework**: Android SDK
- **Arquitectura**: MVVM con Clean Architecture
- **Base de Datos**: Room
- **Inyección de Dependencias**: Hilt y Koin
- **Red**: Retrofit (asumiendo basado en módulos de red)
- **Navegación**: Jetpack Navigation
- **Firebase**: Para servicios en la nube

## 📋 Requisitos

- Android Studio Arctic Fox o superior
- JDK 11 o superior
- Dispositivo Android con API 21+ o emulador

## 🔧 Instalación

1. Clona el repositorio:

   ```bash
   git clone [URL del repositorio]
   cd PanchitaAPP
   ```

2. Abre el proyecto en Android Studio.

3. Sincroniza el proyecto con Gradle.

## ⚙️ Configuración

### Firebase

Este proyecto utiliza Firebase. Para poder compilar y ejecutar la aplicación, necesitas añadir tu propio archivo de configuración `google-services.json`.

1. Ve a la **Consola de Firebase**.
2. Selecciona tu proyecto.
3. En **Configuración del Proyecto** (el ícono de engranaje ⚙️), descarga el archivo `google-services.json`.
4. Copia el archivo descargado en el directorio `app/` de este proyecto.

### Dependencias

Las dependencias están gestionadas a través de Gradle. Asegúrate de que `gradle.properties` tenga las configuraciones necesarias.

## ▶️ Uso

1. Conecta un dispositivo Android o inicia un emulador.
2. Ejecuta la aplicación desde Android Studio (Shift + F10).

## 📁 Estructura del Proyecto

```
app/
├── src/main/java/com/pinao/panchitaapp/
│   ├── data/
│   │   ├── local/          # Base de datos Room y DAOs
│   │   ├── localstorage/   # Almacenamiento local
│   │   ├── mapper/         # Mapeadores de datos
│   │   ├── network/        # APIs de red
│   │   └── repository/     # Implementaciones de repositorios
│   ├── di/                 # Módulos de inyección de dependencias
│   ├── domain/             # Lógica de dominio (modelos, repositorios, casos de uso)
│   ├── presentation/       # Capa de presentación (UI, navegación, ViewModels)
│   └── utils/              # Utilidades comunes
└── src/main/res/           # Recursos de Android
```

## 🤝 Contribución

1. Haz un fork del proyecto.
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`).
3. Commit tus cambios (`git commit -am 'Añade nueva funcionalidad'`).
4. Push a la rama (`git push origin feature/nueva-funcionalidad`).
5. Abre un Pull Request.

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 📞 Contacto

[Tu nombre] - [tu.email@ejemplo.com]

Proyecto Link: [https://github.com/tu-usuario/PanchitaAPP](https://github.com/tu-usuario/PanchitaAPP)

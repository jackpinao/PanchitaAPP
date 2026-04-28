# PanchitaApp — Estructura de Paquetes

```
com.pinao.panchitaapp/
├── PanchitaApp.kt                     ← Application, inicializa Koin
│
├── presentation/
│   ├── navigation/
│   │   ├── AllDestinations.kt         ← Constantes de rutas
│   │   ├── AppNavGraph.kt             ← NavHost principal (drawer + responsive)
│   │   └── AppNavigationActions.kt    ← Acciones de navegación
│   ├── common/                        ← Composables compartidos
│   ├── theme/                         ← Material3 theme, colores, tipografía
│   └── ui/
│       ├── login/                     ← LoginScreen + LoginViewModel + LoginUiState
│       ├── home/                      ← HomeScreen + HomeViewModel
│       ├── inventoryList/             ← InventoryListScreen + ViewModel
│       ├── addProduct/                ← AddProductScreen + ViewModel
│       ├── addCategory/               ← AddCategoryScreen + ViewModel
│       ├── moduloVenta/               ← ModuloVentaScreen + ViewModel
│       ├── clarorecarga/              ← ClaroRecargaScreen + ViewModel
│       ├── main/                      ← MainActivity
│       └── AppDrawer.kt               ← Modal Navigation Drawer
│
├── domain/
│   ├── model/                         ← Data classes puros (sin anotaciones Android)
│   ├── repository/                    ← Interfaces de repositorios + BarcodeScanner
│   ├── usecase/
│   │   ├── Auth/                      ← AuthUseCase bundle
│   │   ├── brand/                     ← BrandUseCases bundle
│   │   ├── category/                  ← CategoryUseCases bundle
│   │   ├── client/                    ← ClientUseCases
│   │   ├── products/                  ← ProductUseCases (incluye RefreshProductsUseCase)
│   │   ├── rechange/                  ← RechangeUseCases
│   │   ├── temporary/                 ← TemporaryProductUseCases bundle
│   │   ├── ticket/                    ← DetailTicketUseCases + CompleteSaleUseCase
│   │   └── user/                      ← UserUseCases
│   ├── service/                       ← Interfaces de servicios del dominio
│   └── util/                          ← Resource<T> sealed class
│
├── data/
│   ├── repository/                    ← Implementaciones de repositorios
│   ├── mapper/                        ← {Entity}Mapper.toDomain() / .toDatabase()
│   ├── source/
│   │   ├── local/
│   │   │   ├── dao/                   ← Room DAOs (11 entidades)
│   │   │   ├── entity/                ← Room Entities
│   │   │   ├── database/
│   │   │   │   ├── AppDatabase.kt     ← RoomDatabase v18
│   │   │   │   └── Migration*.kt      ← Migraciones incrementales
│   │   │   └── SessionManager.kt      ← SharedPreferences (panchita_prefs)
│   │   └── remote/
│   │       ├── firebase/              ← Firebase{Entity}DataSource
│   │       ├── dto/                   ← DTOs de red
│   │       ├── RemoteDataSource.kt
│   │       └── RemoteDataSourceImpl.kt (Retrofit)
│   ├── network/                       ← Retrofit services (rechange, user)
│   └── service/
│       ├── AndroidTicketPdfService.kt ← Generación de PDF de tickets
│       └── SyncWorker.kt              ← WorkManager sync periódico
│
├── di/
│   └── koin/
│       ├── AppModule.kt               ← Módulo raíz (incluye los 3)
│       ├── DataModule.kt              ← @Single: DB, Firebase, Retrofit, Repos
│       ├── DomainModule.kt            ← @Factory: Use Cases
│       ├── PresentationModule.kt      ← @ComponentScan → @KoinViewModel
│       └── others/                    ← Módulos auxiliares
│
└── utils/
    ├── Common.kt                      ← String.sinAcento()
    ├── Constants.kt                   ← BASE_URL
    └── Resource.kt                    ← sealed class Resource<T>
```

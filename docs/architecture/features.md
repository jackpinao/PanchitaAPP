# PanchitaApp — Módulos de Funcionalidad

| Feature | Screen | ViewModel | Use Cases |
|---------|--------|-----------|-----------|
| **Auth** | `LoginScreen` | `LoginViewModel` | `SignInUseCase`, `SignOutUseCase`, `IsUserLoggedInUseCase` |
| **Home** | `HomeScreen` | `HomeViewModel` | — |
| **Inventario** | `InventoryListScreen` | `InventoryViewModel` | `GetAllProductsUseCase`, `DeleteProductUseCase`, `RefreshProductsUseCase` |
| **Agregar Producto** | `AddProductScreen` | `AddProductViewModel` | `SaveProductsUseCase`, `ScanBarcodeUseCase`, `GetAllBrandsUseCase`, `GetAllCategoriesUseCase` |
| **Agregar Categoría** | `AddCategoryScreen` | — | `SaveCategoryUseCase`, `CheckCategoryNameUseCase` |
| **Módulo de Venta** | `ModuloVentaScreen` | `ModuloVentaViewModel` | `TemporaryProductUseCases`, `CompleteSaleUseCase`, `SearchProductsUseCase` |
| **Recargas Claro** | `ClaroRecargaScreen` | `ClaroRecargaViewModel` | `SaveRechangeUseCase`, `GetAllDateRechangeUseCase`, `GetListForDateRechangeUC` |

---

## Convenciones de Nomenclatura

| Tipo | Patrón | Ejemplo |
|------|--------|---------|
| ViewModel | `{Feature}ViewModel` | `AddProductViewModel` |
| Use Case | `{Action}{Entity}UseCase` | `GetAllProductsUseCase` |
| Repository impl | `{Entity}RepositoryImpl` | `ProductsRepositoryImpl` |
| Mapper | `{Entity}Mapper` | `ProductMapper` |
| Remote data source | `{Entity}RemoteDataSource` | `ProductRemoteDataSource` |
| Firebase data source | `Firebase{Entity}DataSource` | `FirebaseProductDataSource` |
| UI State | `{Feature}UiState` | `AddProductUiState` |

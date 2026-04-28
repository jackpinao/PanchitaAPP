---
description: "Generates a new Use Case with its unit test following PanchitaApp conventions"
argument-hint: "Action and entity — e.g. 'GetClientById' or 'DeleteProduct'"
agent: agent
tools: [codebase, read_file, create_file]
---

Genera un nuevo Use Case para PanchitaApp siguiendo las convenciones del proyecto.

## Parámetro

El argumento proporcionado es el nombre del Use Case en formato `{Action}{Entity}` (ej. `GetClientById`).

## Lo que debes hacer

1. **Identifica la entidad** a partir del nombre (ej. `Client`) y localiza:
   - El modelo de dominio en `domain/model/`
   - La interfaz del repositorio en `domain/repository/`

2. **Crea el Use Case** en `domain/usecase/{entity_lowercase}/`:
   ```kotlin
   class {Action}{Entity}UseCase(private val repository: {Entity}Repository) {
       suspend operator fun invoke(/* params */): Resource<{ReturnType}> {
           return try {
               Resource.Success(repository.{method}(/* params */))
           } catch (e: Exception) {
               Resource.Error(e.message ?: "Error desconocido")
           }
       }
   }
   ```

3. **Registra el Use Case** en `DomainModule.kt` con `@Factory`:
   ```kotlin
   factory { {Action}{Entity}UseCase(get()) }
   ```

4. **Agrega al bundle** si ya existe `{Entity}UseCases` en `PresentationModule.kt`:
   ```kotlin
   val {entity}UseCases = {Entity}UseCases(
       ...,
       {action}{Entity}UseCase = get()
   )
   ```

5. **Crea el test unitario** en `test/domain/{entity_lowercase}/{Action}{Entity}UseCaseTest.kt`:
   ```kotlin
   @RunWith(JUnit4::class)
   class {Action}{Entity}UseCaseTest {
       private val repository: {Entity}Repository = mockk()
       private val useCase = {Action}{Entity}UseCase(repository)

       @Test
       fun `invoke returns success when repository succeeds`() = runTest {
           // arrange
           coEvery { repository.{method}(any()) } returns /* expected */
           // act
           val result = useCase(/* params */)
           // assert
           assertThat(result).isInstanceOf(Resource.Success::class.java)
       }

       @Test
       fun `invoke returns error when repository throws`() = runTest {
           coEvery { repository.{method}(any()) } throws Exception("DB error")
           val result = useCase(/* params */)
           assertThat(result).isInstanceOf(Resource.Error::class.java)
       }
   }
   ```

## Convenciones a respetar

- Retornar siempre `Resource<T>` — nunca lanzar excepciones hacia el ViewModel
- `operator fun invoke` para invocación directa
- Imports: `com.pinao.panchitaapp.utils.Resource`
- Tests con MockK + Truth; ver [ExampleUnitTest.kt](../../app/src/test/java/com/pinao/panchitaapp/ExampleUnitTest.kt)

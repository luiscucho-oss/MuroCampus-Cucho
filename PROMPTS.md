# PROMPTS.md — Fase 2 (rama `mejora-ia`)

Mejoras elegidas (mínimo 3, al menos una cambia el Repository o la interface de la API):

| # | Mejora | Capas que cambian | Commit |
|---|---|---|---|
| 1 | Borrar publicación con `@DELETE` y `Response<Unit>` | API + Repository + ViewModel + View | `feat(borrar): elimina publicaciones con DELETE revisando el codigo de respuesta` |
| 2 | Búsqueda por título en la lista descargada | ViewModel + View | `feat(busqueda): busca publicaciones por titulo en la lista descargada` |
| 3 | Interceptor con `Accept: application/json` | RetrofitClient | `feat(red): agrega interceptor con encabezado Accept application/json` |

## Registro de prompts

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
|---|---|---|---|
| «Haz este laboratorio» (adjuntando la guía del Lab 9) | Leyó la guía y armó un plan de 11 commits. Como el proyecto base no estaba en mi equipo, lo recreó con las versiones de Gradle de mis laboratorios anteriores. | El ejemplo de `Usuario` y `UiState` necesitan Retrofit y kotlinx.serialization para compilar, así que entraron en el commit del Paso 1 junto con las dependencias. `TarjetaPost` recibe `usuario`, `titulo` y `cuerpo` en vez de un `Post`, para que el proyecto base compile antes de los modelos. | La guía dice que el base "solo trae Compose y ViewModel", pero sus archivos [E] usan Retrofit. Hubo que ordenar los commits para que cada uno compile por sí solo. |
| (Revisión de la regla «nada de `HttpException` en el ViewModel») | Notó que la guía pone `mensajeDe()` dentro de `viewmodel/UiState.kt`, y esa función usa `HttpException`. | Movió `mensajeDe()` a `data/MensajesError.kt`. `UiState` se quedó en `viewmodel/`. | Así el paquete `viewmodel/` no importa nada de Retrofit y la prueba «Revisa el viewmodel/: no hay Retrofit, PostApi ni HttpException» se cumple de verdad. |
| «Borrar publicación con @DELETE que revise el código con Response<Unit>» | `@DELETE("posts/{id}")` que devuelve `Response<Unit>`. En el Repository, `if (!respuesta.isSuccessful) throw HttpException(respuesta)` dentro del mismo `llamar { }`, que lo convierte en `Result.failure`. En el ViewModel, `borrado: StateFlow<UiState<Int>?>`. En el detalle, un botón *Eliminar* con un `AlertDialog` de confirmación. | Al probarlo se vio un detalle: si el borrado fallaba, el error se quedaba visible al abrir otra publicación. Se agregó `_borrado.value = null` en `abrir()`, en el mismo commit. Como JSONPlaceholder no borra de verdad, el ViewModel quita la publicación de la lista local. | Con `Response<Unit>`, Retrofit **no** lanza `HttpException` en un 404, así que hay que revisar `isSuccessful` a mano. Hacerlo en el Repository mantiene la regla: el ViewModel solo ve `Result`. |
| «Búsqueda por título sobre las publicaciones ya descargadas» | `_busqueda` en el ViewModel y `listaVisible = combine(_lista, _busqueda)` con `SharingStarted.Eagerly`. Un `OutlinedTextField` arriba de los chips y el mensaje «Sin resultados para …». | La búsqueda no hace otra petición: filtra lo que ya está en memoria y respeta el filtro por usuario. | Filtrar es lógica: va en el ViewModel, no en el Composable. Como solo envuelve el `UiState`, la pantalla sigue con su mismo `when`. |
| «Interceptor que agregue Accept: application/json a todas las peticiones» | Un `Interceptor` de OkHttp que hace `request.newBuilder().header("Accept", "application/json")`, agregado **antes** del de log. | Ninguno. | El orden importa: al ir antes del `HttpLoggingInterceptor`, Logcat muestra `Accept: application/json` en cada petición, y así se comprobó que funciona. |

## Cómo verifiqué las mejoras (emulador)

- **Borrar:** Logcat muestra `--> DELETE …/posts/1` y `<-- 200`. La app vuelve a la lista y la publicación 1 ya no aparece.
- **Búsqueda:** al escribir «dolorem» solo quedan las publicaciones con esa palabra en el título, sin ninguna petición nueva.
- **Interceptor:** cada petición en Logcat lleva la línea `Accept: application/json`.
- **Regla MVVM:** `PostsViewModel.kt` no importa Retrofit, `PostApi`, `HttpException` ni `Context`. Solo `PostsViewModelFactory` conoce `RetrofitClient`, porque es la que arma la cadena.

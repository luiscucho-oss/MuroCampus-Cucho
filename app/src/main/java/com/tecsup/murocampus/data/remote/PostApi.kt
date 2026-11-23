package com.tecsup.murocampus.data.remote

import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Rutas sin "/" inicial: se suman a la URL base en lugar de reemplazar su camino
interface PostApi {

    @GET("posts")
    suspend fun listar(): List<Post>

    // @Path reemplaza {id} dentro de la ruta: posts/1
    @GET("posts/{id}")
    suspend fun detalle(@Path("id") id: Int): Post

    @GET("posts/{id}/comments")
    suspend fun comentarios(@Path("id") id: Int): List<Comentario>

    // @Query agrega ?userId=2 al final de la URL
    @GET("posts")
    suspend fun porUsuario(@Query("userId") userId: Int): List<Post>

    // @Body envía el objeto como JSON en el cuerpo de la petición
    @POST("posts")
    suspend fun crear(@Body nuevo: NuevoPost): Post

    // DELETE responde con cuerpo vacío: Response<Unit> deja revisar el código (200, 404…)
    @DELETE("posts/{id}")
    suspend fun borrar(@Path("id") id: Int): Response<Unit>
}

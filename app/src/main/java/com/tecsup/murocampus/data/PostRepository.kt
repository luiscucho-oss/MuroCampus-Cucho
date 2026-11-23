package com.tecsup.murocampus.data

import com.tecsup.murocampus.data.remote.PostApi
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

// Lo único que conoce el ViewModel: funciones que devuelven Result, nunca excepciones
interface PostRepository {
    suspend fun listar(): Result<List<Post>>
    suspend fun detalle(id: Int): Result<Post>
    suspend fun comentarios(id: Int): Result<List<Comentario>>
    suspend fun porUsuario(userId: Int): Result<List<Post>>
    suspend fun crear(nuevo: NuevoPost): Result<Post>
    suspend fun borrar(id: Int): Result<Unit>
}

class PostRepositoryRemote(private val api: PostApi) : PostRepository {

    override suspend fun listar() = llamar { api.listar() }
    override suspend fun detalle(id: Int) = llamar { api.detalle(id) }
    override suspend fun comentarios(id: Int) = llamar { api.comentarios(id) }
    override suspend fun porUsuario(userId: Int) = llamar { api.porUsuario(userId) }
    override suspend fun crear(nuevo: NuevoPost) = llamar { api.crear(nuevo) }

    // Con Response<Unit> Retrofit no lanza HttpException solo: si el código no es 2xx, la lanzamos aquí
    override suspend fun borrar(id: Int) = llamar {
        val respuesta = api.borrar(id)
        if (!respuesta.isSuccessful) throw HttpException(respuesta)
    }

    // Un solo try/catch para las cinco llamadas
    private suspend fun <T> llamar(bloque: suspend () -> T): Result<T> =
        try {
            Result.success(bloque())
        } catch (e: CancellationException) {
            throw e // la cancelación no es un error: se relanza
        } catch (e: Exception) {
            Result.failure(e) // IOException, HttpException, SerializationException…
        }
}

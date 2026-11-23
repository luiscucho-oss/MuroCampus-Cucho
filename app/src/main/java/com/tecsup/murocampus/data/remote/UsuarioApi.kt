package com.tecsup.murocampus.data.remote

import com.tecsup.murocampus.model.Usuario
import retrofit2.http.GET

// Ejemplo resuelto: una interface sin código; Retrofit genera la implementación
interface UsuarioApi {
    @GET("users")
    suspend fun listar(): List<Usuario>
}

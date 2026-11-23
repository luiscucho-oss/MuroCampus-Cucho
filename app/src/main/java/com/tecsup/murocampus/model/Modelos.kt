package com.tecsup.murocampus.model

import kotlinx.serialization.Serializable

// Ejemplo resuelto: los nombres de las propiedades son iguales a los del JSON
@Serializable
data class Usuario(
    val id: Int,
    val name: String,
    val email: String
)

// TODO Paso 2: Post, Comentario y NuevoPost

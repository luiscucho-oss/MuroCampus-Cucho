package com.tecsup.murocampus.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

// Se crea una sola vez (object + by lazy) y se reutiliza en toda la app
object RetrofitClient {

    // ignoreUnknownKeys: si el servidor agrega campos nuevos, la app no se rompe
    private val json = Json { ignoreUnknownKeys = true }

    // Agrega "Accept: application/json" a TODAS las peticiones: le dice al servidor qué formato esperamos.
    // Va antes del de log para que el log muestre el encabezado ya agregado.
    private val encabezadoAccept = Interceptor { chain ->
        val peticion = chain.request().newBuilder()
            .header("Accept", "application/json")
            .build()
        chain.proceed(peticion)
    }

    private val cliente = OkHttpClient.Builder()
        .addInterceptor(encabezadoAccept)
        // Muestra en Logcat (etiqueta okhttp.OkHttpClient) cada petición y su JSON. Solo para desarrollo
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    val api: PostApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/") // termina en "/"
            .client(cliente)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PostApi::class.java)
    }
}

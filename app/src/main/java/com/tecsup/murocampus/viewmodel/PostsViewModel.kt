package com.tecsup.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.murocampus.data.PostRepository
import com.tecsup.murocampus.data.mensajeDe
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.Post
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Lo que muestra la pantalla de detalle
data class DetalleUi(val post: Post, val comentarios: List<Comentario>)

// Solo conoce la interface PostRepository: nada de Retrofit, PostApi, HttpException ni Context
class PostsViewModel(private val repo: PostRepository) : ViewModel() {

    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista: StateFlow<UiState<List<Post>>> = _lista.asStateFlow()

    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle: StateFlow<UiState<DetalleUi>> = _detalle.asStateFlow()
    private var trabajoDetalle: Job? = null

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _lista.value = UiState.Cargando
            repo.listar()
                .onSuccess { _lista.value = UiState.Exito(it) }
                .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
        }
    }

    // Pide la publicación y sus comentarios a la vez; si cualquiera falla, todo el detalle es Error
    fun abrir(id: Int) {
        trabajoDetalle?.cancel() // si se abre otra publicación, la anterior ya no interesa
        trabajoDetalle = viewModelScope.launch {
            _detalle.value = UiState.Cargando
            val (post, comentarios) = coroutineScope {
                val p = async { repo.detalle(id) }
                val c = async { repo.comentarios(id) }
                p.await() to c.await()
            }
            _detalle.value = post.fold(
                onSuccess = { p ->
                    comentarios.fold(
                        onSuccess = { UiState.Exito(DetalleUi(p, it)) },
                        onFailure = { UiState.Error(mensajeDe(it)) }
                    )
                },
                onFailure = { UiState.Error(mensajeDe(it)) }
            )
        }
    }
}

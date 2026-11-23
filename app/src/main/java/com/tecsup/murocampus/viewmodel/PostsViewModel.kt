package com.tecsup.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.murocampus.data.PostRepository
import com.tecsup.murocampus.data.mensajeDe
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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

    // Búsqueda por título sobre lo ya descargado (no hace otra petición)
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val listaVisible: StateFlow<UiState<List<Post>>> = combine(_lista, _busqueda) { estado, texto ->
        if (estado is UiState.Exito && texto.isNotBlank()) {
            UiState.Exito(estado.datos.filter { it.title.contains(texto.trim(), ignoreCase = true) })
        } else {
            estado
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Cargando)

    // null = Todos
    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro: StateFlow<Int?> = _usuarioFiltro.asStateFlow()
    private var trabajoLista: Job? = null

    // null = todavía no se envió nada
    private val _publicacion = MutableStateFlow<UiState<Post>?>(null)
    val publicacion: StateFlow<UiState<Post>?> = _publicacion.asStateFlow()

    // Resultado de borrar: Exito trae el id eliminado; null = nada pendiente
    private val _borrado = MutableStateFlow<UiState<Int>?>(null)
    val borrado: StateFlow<UiState<Int>?> = _borrado.asStateFlow()

    init { cargar() }

    // Carga según el filtro actual: GET posts o GET posts?userId=N
    fun cargar() {
        trabajoLista?.cancel() // si se cambia de chip rápido, solo vale la última petición
        trabajoLista = viewModelScope.launch {
            _lista.value = UiState.Cargando
            val usuario = _usuarioFiltro.value
            val resultado = if (usuario == null) repo.listar() else repo.porUsuario(usuario)
            resultado
                .onSuccess { _lista.value = UiState.Exito(it) }
                .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    fun filtrar(userId: Int?) {
        _usuarioFiltro.value = userId
        cargar()
    }

    // Pide la publicación y sus comentarios a la vez; si cualquiera falla, todo el detalle es Error
    fun abrir(id: Int) {
        trabajoDetalle?.cancel() // si se abre otra publicación, la anterior ya no interesa
        _borrado.value = null     // un error de borrado anterior no debe verse en otra publicación
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

    // POST posts con el JSON de NuevoPost en el cuerpo
    fun publicar(titulo: String, texto: String) {
        viewModelScope.launch {
            _publicacion.value = UiState.Cargando
            repo.crear(NuevoPost(userId = 1, title = titulo.trim(), body = texto.trim()))
                .onSuccess { _publicacion.value = UiState.Exito(it) }
                .onFailure { _publicacion.value = UiState.Error(mensajeDe(it)) }
        }
    }

    // Al salir de la pantalla Crear se olvida el resultado anterior
    fun limpiarPublicacion() {
        _publicacion.value = null
    }

    // DELETE posts/{id}. JSONPlaceholder no borra de verdad, así que se quita de la lista local
    fun borrar(id: Int) {
        viewModelScope.launch {
            _borrado.value = UiState.Cargando
            repo.borrar(id)
                .onSuccess {
                    val actual = _lista.value
                    if (actual is UiState.Exito) {
                        _lista.value = UiState.Exito(actual.datos.filterNot { it.id == id })
                    }
                    _borrado.value = UiState.Exito(id)
                }
                .onFailure { _borrado.value = UiState.Error(mensajeDe(it)) }
        }
    }

    fun limpiarBorrado() {
        _borrado.value = null
    }
}

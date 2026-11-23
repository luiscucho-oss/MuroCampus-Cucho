package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

// Solo dibuja el estado: un when con los tres casos
@Composable
fun PantallaLista(vm: PostsViewModel, onAbrir: (Int) -> Unit, modifier: Modifier = Modifier) {
    val estado by vm.lista.collectAsStateWithLifecycle()

    Column(modifier.fillMaxSize()) {
        when (val e = estado) {
            UiState.Cargando -> Cargando()
            is UiState.Error -> ErrorConReintento(e.mensaje, onReintentar = { vm.cargar() })
            is UiState.Exito -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(e.datos, key = { it.id }) { post ->
                    TarjetaPost(
                        usuario = post.userId,
                        titulo = post.title,
                        cuerpo = post.body,
                        onClick = { onAbrir(post.id) }
                    )
                }
            }
        }
    }
}

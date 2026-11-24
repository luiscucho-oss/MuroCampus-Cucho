package com.tecsup.murocampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

@Composable
fun PantallaLista(
    vm: PostsViewModel,
    desplazamiento: LazyListState,
    onAbrir: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.listaVisible.collectAsStateWithLifecycle()
    val filtro by vm.usuarioFiltro.collectAsStateWithLifecycle()
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()
    val colores = MaterialTheme.colorScheme

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = desplazamiento,
        contentPadding = PaddingValues(bottom = 92.dp)
    ) {
        item {
            CabeceraMuro(busqueda = busqueda, onBuscar = vm::buscar)
        }
        item {
            FiltroUsuarios(seleccionado = filtro, onElegir = vm::filtrar)
        }
        when (val e = estado) {
            UiState.Cargando -> item { Cargando(modifier = Modifier.fillParentMaxHeight(0.46f)) }
            is UiState.Error -> item {
                ErrorConReintento(e.mensaje, vm::cargar, Modifier.fillParentMaxHeight(0.46f))
            }
            is UiState.Exito -> {
                item {
                    val cantidad = e.datos.size
                    Text(
                        if (cantidad == 1) "1 publicación" else "$cantidad publicaciones",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 12.dp)
                            .semantics { heading() }
                    )
                }
                if (e.datos.isEmpty()) {
                    item {
                        Box(
                            Modifier.fillMaxWidth().padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (busqueda.isBlank()) "Sin publicaciones" else "Sin resultados para «$busqueda»",
                                color = colores.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                items(e.datos, key = { it.id }) { post ->
                    TarjetaPost(
                        usuario = post.userId,
                        titulo = post.title,
                        cuerpo = post.body,
                        onClick = { onAbrir(post.id) },
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CabeceraMuro(busqueda: String, onBuscar: (String) -> Unit) {
    val colores = MaterialTheme.colorScheme
    val foco = LocalFocusManager.current

    Column(
        Modifier.fillMaxWidth().background(
            colores.inverseSurface, RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp)
        ).padding(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "Publicaciones",
            color = colores.inverseOnSurface,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.semantics { heading() }
        )
        TextField(
            value = busqueda,
            onValueChange = onBuscar,
            label = { Text("Buscar por título") },
            leadingIcon = { IconoMuro(Trazo.Buscar) },
            trailingIcon = {
                if (busqueda.isNotEmpty()) {
                    IconButton(onClick = { onBuscar("") }) {
                        IconoMuro(Trazo.Cerrar, "Limpiar búsqueda")
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { foco.clearFocus() }),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colores.surface,
                unfocusedContainerColor = colores.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FiltroUsuarios(seleccionado: Int?, onElegir: (Int?) -> Unit) {
    Column(Modifier.padding(top = 13.dp, bottom = 4.dp)) {
        Text(
            "Filtrar por usuario",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = seleccionado == null,
                    onClick = { onElegir(null) },
                    label = { Text("Todos") },
                    shape = RoundedCornerShape(50)
                )
            }
            items((1..10).toList()) { usuario ->
                FilterChip(
                    selected = seleccionado == usuario,
                    onClick = { onElegir(usuario) },
                    label = { Text("Usuario $usuario") },
                    shape = RoundedCornerShape(50)
                )
            }
        }
    }
}

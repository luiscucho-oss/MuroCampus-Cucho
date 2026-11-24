package com.tecsup.murocampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

@Composable
fun PantallaDetalle(
    vm: PostsViewModel,
    id: Int,
    onBorrado: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.detalle.collectAsStateWithLifecycle()
    val borrado by vm.borrado.collectAsStateWithLifecycle()
    var confirmar by rememberSaveable(id) { mutableStateOf(false) }

    LaunchedEffect(borrado) {
        val eliminado = borrado as? UiState.Exito
        if (eliminado?.datos == id) {
            vm.limpiarBorrado()
            onBorrado()
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("Eliminar publicación") },
            text = { Text("¿Seguro que quieres eliminar la publicación $id?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmar = false
                        vm.borrar(id)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } }
        )
    }

    when (val e = estado) {
        UiState.Cargando -> Cargando("Cargando publicación…", modifier)
        is UiState.Error -> ErrorConReintento(e.mensaje, { vm.abrir(id) }, modifier)
        is UiState.Exito -> {
            val (post, comentarios) = e.datos
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    PublicacionCompleta(post, borrado, onEliminar = { confirmar = true })
                }
                item {
                    Text(
                        "Comentarios (${comentarios.size})",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 4.dp).semantics { heading() }
                    )
                }
                items(comentarios, key = { it.id }) { TarjetaComentario(it) }
            }
        }
    }
}

@Composable
private fun PublicacionCompleta(post: Post, borrado: UiState<Int>?, onEliminar: () -> Unit) {
    val colores = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, colores.outlineVariant)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AutorPost(post.userId)
            Text(
                post.title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                post.body,
                style = MaterialTheme.typography.bodyLarge,
                color = colores.onSurfaceVariant
            )
            HorizontalDivider(color = colores.outlineVariant)
            OutlinedButton(
                onClick = onEliminar,
                enabled = borrado !is UiState.Cargando,
                border = BorderStroke(1.dp, colores.outlineVariant),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colores.error)
            ) { Text(if (borrado is UiState.Cargando) "Eliminando…" else "Eliminar") }
            (borrado as? UiState.Error)?.let {
                Text(it.mensaje, color = colores.error, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun TarjetaComentario(comentario: Comentario) {
    val colores = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, colores.outlineVariant)
    ) {
        Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InsigniaAutor(comentario.email.take(1).uppercase())
                Text(
                    comentario.email,
                    color = colores.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Text(
                comentario.body,
                color = colores.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

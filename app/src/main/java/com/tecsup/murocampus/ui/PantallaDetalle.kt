package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.Comentario
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
    var confirmar by remember { mutableStateOf(false) }

    // Cuando el DELETE responde bien, se vuelve a la lista
    LaunchedEffect(borrado) {
        if (borrado is UiState.Exito) {
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
                TextButton(onClick = {
                    confirmar = false
                    vm.borrar(id)
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } }
        )
    }

    Column(modifier.fillMaxSize()) {
        when (val e = estado) {
            UiState.Cargando -> Cargando("Cargando publicación…")
            is UiState.Error -> ErrorConReintento(e.mensaje, onReintentar = { vm.abrir(id) })
            is UiState.Exito -> {
                val (post, comentarios) = e.datos
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        TarjetaPost(
                            usuario = post.userId,
                            titulo = post.title,
                            cuerpo = post.body,
                            onClick = {},
                            maxLineas = Int.MAX_VALUE // título y texto completos
                        )
                    }
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { confirmar = true },
                                enabled = borrado !is UiState.Cargando,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) { Text(if (borrado is UiState.Cargando) "Eliminando…" else "Eliminar") }
                            (borrado as? UiState.Error)?.let {
                                Text(it.mensaje, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                    item {
                        Text(
                            "Comentarios (${comentarios.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(comentarios, key = { it.id }) { TarjetaComentario(it) }
                }
            }
        }
    }
}

@Composable
private fun TarjetaComentario(c: Comentario) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                c.email,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
            HorizontalDivider()
            Text(c.body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

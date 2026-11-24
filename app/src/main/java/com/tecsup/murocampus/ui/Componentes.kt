package com.tecsup.murocampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun TarjetaPost(
    usuario: Int,
    titulo: String,
    cuerpo: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colores = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colores.outlineVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                AutorPost(usuario, Modifier.weight(1f))
                IconoMuro(Trazo.Flecha)
            }
            Text(
                titulo,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                cuerpo.replace('\n', ' '),
                color = colores.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AutorPost(usuario: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InsigniaAutor(usuario.toString())
        Text(
            "Usuario $usuario",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun InsigniaAutor(inicial: String) {
    val colores = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.size(32.dp).clearAndSetSemantics {},
        color = colores.primaryContainer,
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(inicial, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun Cargando(texto: String = "Cargando publicaciones…", modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(texto, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
    }
}

@Composable
fun ErrorConReintento(mensaje: String, onReintentar: () -> Unit, modifier: Modifier = Modifier) {
    val colores = MaterialTheme.colorScheme
    Box(modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
        Surface(
            color = colores.errorContainer,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("No se pudo cargar", style = MaterialTheme.typography.titleLarge)
                Text(mensaje, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
                Button(onClick = onReintentar) { Text("Reintentar") }
            }
        }
    }
}

enum class Trazo { Atrás, Buscar, Cerrar, Flecha, Más }

@Composable
fun IconoMuro(trazo: Trazo, descripcion: String? = null) {
    val color = LocalContentColor.current
    val modifier = Modifier.size(20.dp).let {
        if (descripcion == null) it else it.clearAndSetSemantics { contentDescription = descripcion }
    }

    Canvas(modifier) {
        val unidad = size.width / 24
        val grosor = 2.dp.toPx()
        fun linea(x1: Int, y1: Int, x2: Int, y2: Int) {
            drawLine(
                color, Offset(x1 * unidad, y1 * unidad), Offset(x2 * unidad, y2 * unidad),
                strokeWidth = grosor, cap = StrokeCap.Round
            )
        }

        when (trazo) {
            Trazo.Atrás -> {
                linea(20, 12, 4, 12)
                linea(4, 12, 11, 5)
                linea(4, 12, 11, 19)
            }
            Trazo.Buscar -> {
                drawCircle(color, 6 * unidad, Offset(10 * unidad, 10 * unidad), style = Stroke(grosor))
                linea(14, 14, 20, 20)
            }
            Trazo.Cerrar -> {
                linea(6, 6, 18, 18)
                linea(6, 18, 18, 6)
            }
            Trazo.Flecha -> {
                linea(9, 6, 15, 12)
                linea(15, 12, 9, 18)
            }
            Trazo.Más -> {
                linea(12, 5, 12, 19)
                linea(5, 12, 19, 12)
            }
        }
    }
}

package com.tecsup.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.murocampus.ui.IconoMuro
import com.tecsup.murocampus.ui.PantallaCrear
import com.tecsup.murocampus.ui.PantallaDetalle
import com.tecsup.murocampus.ui.PantallaLista
import com.tecsup.murocampus.ui.Trazo
import com.tecsup.murocampus.ui.theme.MuroCampusTheme
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.PostsViewModelFactory

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        setContent {
            MuroCampusTheme {
                val vm: PostsViewModel = viewModel(factory = PostsViewModelFactory())
                val desplazamiento = rememberLazyListState()
                var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
                var creando by rememberSaveable { mutableStateOf(false) }
                val enLista = idSeleccionado == null && !creando
                val volver = {
                    if (creando) vm.limpiarPublicacion()
                    idSeleccionado = null
                    creando = false
                }
                val colores = MaterialTheme.colorScheme

                BackHandler(enabled = !enLista, onBack = volver)

                Scaffold(
                    containerColor = colores.background,
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    when {
                                        creando -> "Crear"
                                        enLista -> "MuroCampus"
                                        else -> "Publicación"
                                    },
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            navigationIcon = {
                                if (!enLista) {
                                    IconButton(onClick = volver) {
                                        IconoMuro(Trazo.Atrás, "Volver al muro")
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = colores.inverseSurface,
                                titleContentColor = colores.inverseOnSurface,
                                navigationIconContentColor = colores.inverseOnSurface
                            )
                        )
                    },
                    floatingActionButton = {
                        if (enLista) {
                            ExtendedFloatingActionButton(
                                onClick = { creando = true },
                                icon = { IconoMuro(Trazo.Más) },
                                text = { Text("Nueva publicación", Modifier.clearAndSetSemantics {}) },
                                modifier = Modifier.semantics {
                                    contentDescription = "Nueva publicación"
                                    role = Role.Button
                                },
                                shape = RoundedCornerShape(18.dp),
                                containerColor = colores.primary,
                                contentColor = colores.onPrimary
                            )
                        }
                    }
                ) { padding ->
                    val id = idSeleccionado
                    val contenido = Modifier.padding(padding)
                    when {
                        creando -> PantallaCrear(vm = vm, modifier = contenido)
                        id != null -> PantallaDetalle(
                            vm = vm,
                            id = id,
                            onBorrado = volver,
                            modifier = contenido
                        )
                        else -> PantallaLista(
                            vm = vm,
                            desplazamiento = desplazamiento,
                            onAbrir = {
                                vm.abrir(it)
                                idSeleccionado = it
                            },
                            modifier = contenido
                        )
                    }
                }
            }
        }
    }
}

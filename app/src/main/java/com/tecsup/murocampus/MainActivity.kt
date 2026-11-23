package com.tecsup.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.murocampus.ui.PantallaCrear
import com.tecsup.murocampus.ui.PantallaDetalle
import com.tecsup.murocampus.ui.PantallaLista
import com.tecsup.murocampus.ui.theme.MuroCampusTheme
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.PostsViewModelFactory

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuroCampusTheme {
                val vm: PostsViewModel = viewModel(factory = PostsViewModelFactory())
                // Navegación simple: null = lista; un id = detalle de esa publicación
                var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
                var creando by rememberSaveable { mutableStateOf(false) }

                // El botón Atrás del sistema vuelve a la lista (sin recargarla)
                BackHandler(enabled = idSeleccionado != null || creando) {
                    if (creando) vm.limpiarPublicacion()
                    idSeleccionado = null
                    creando = false
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("MuroCampus") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    },
                    floatingActionButton = {
                        if (idSeleccionado == null && !creando) {
                            FloatingActionButton(onClick = { creando = true }) {
                                Text("+", style = MaterialTheme.typography.headlineMedium)
                            }
                        }
                    }
                ) { padding ->
                    val id = idSeleccionado
                    if (creando) {
                        PantallaCrear(vm = vm, modifier = Modifier.padding(padding))
                    } else if (id == null) {
                        PantallaLista(
                            vm = vm,
                            onAbrir = {
                                vm.abrir(it)
                                idSeleccionado = it
                            },
                            modifier = Modifier.padding(padding)
                        )
                    } else {
                        PantallaDetalle(vm = vm, id = id, modifier = Modifier.padding(padding))
                    }
                }
            }
        }
    }
}

package com.example.applistadecompras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.applistadecompras.db.AppDatabase
import com.example.applistadecompras.db.Compra
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicia un nuevo thread para interactuar con la base de datos
        lifecycleScope.launch(Dispatchers.IO) {
            val compraDao = AppDatabase.getInstance(this@MainActivity).compraDao()
            val itemCount = compraDao.contar()

            // Si no hay elementos en la base de datos, se insertan algunos ejemplos
            if (itemCount < 1) {
                compraDao.insertar(Compra(0, "Huevos", true))
                compraDao.insertar(Compra(0, "Cecinas", true))
                compraDao.insertar(Compra(0, "Queso Mantecoso", true))
                compraDao.insertar(Compra(0, "Pan", true))
                compraDao.insertar(Compra(0, "Legumbres", true))
                compraDao.insertar(Compra(0, "Carne", true))
                compraDao.insertar(Compra(0, "Pollo", true))
            }
        }

        // Establece el contenido de la actividad con la interfaz de usuario Composable
        setContent {
            TabsApp()
        }
    }
}

// Composable para mostrar la lista de compras
@Composable
fun ListaComprasUI(onNavigateBack: () -> Unit) {
    val contexto = LocalContext.current
    // Estado reactivo para las listas de compras
    val (comprasCompradas, setComprasCompradas) = remember { mutableStateOf(emptyList<Compra>()) }
    val (comprasNoCompradas, setComprasNoCompradas) = remember { mutableStateOf(emptyList<Compra>()) }

    // Efecto lanzado para cargar las compras desde la base de datos
    LaunchedEffect(comprasCompradas, comprasNoCompradas) {
        withContext(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(contexto).compraDao()
            val allCompras = dao.findAll()
            val compradas = allCompras.filter { it.realizada }
            val noCompradas = allCompras.filterNot { it.realizada }

            setComprasCompradas(compradas)
            setComprasNoCompradas(noCompradas)
        }
    }

    // Diseño de la pantalla de la lista de compras
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(comprasNoCompradas) { compra ->
                CompraItemUI(compra) {
                    // Actualizar la lista cuando se cambie el estado
                    setComprasNoCompradas(emptyList<Compra>())
                }
            }

            items(comprasCompradas) { compra ->
                CompraItemUI(compra) {
                    // Actualizar la lista cuando se cambie el estado
                    setComprasCompradas(emptyList<Compra>())
                }
            }
        }

        // Botón "Volver" para regresar a la página principal
        Button(
            onClick = { onNavigateBack() },
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        ) {
            Text(text = "Volver")
        }
    }
}


// Composable para mostrar un elemento individual de compra en la lista
@Composable
fun CompraItemUI(compra: Compra, onSave: () -> Unit = {}) {
    val contexto = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Diseño en fila para cada elemento de compra
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        if (compra.realizada) {
            // Checkbox para elementos de compra ya realizados
            Checkbox(
                checked = compra.realizada,
                onCheckedChange = { isChecked ->
                    coroutineScope.launch(Dispatchers.IO) {
                        val dao = AppDatabase.getInstance(contexto).compraDao()
                        val updatedCompra = compra.copy(realizada = isChecked)
                        dao.actualizar(updatedCompra)
                        onSave()
                    }
                }
            )
        } else {
            // Icono de carrito para elementos de compra no realizados
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription = "Hacer Compra",
                modifier = Modifier.clickable {
                    coroutineScope.launch(Dispatchers.IO) {
                        val dao = AppDatabase.getInstance(contexto).compraDao()
                        val updatedCompra = compra.copy(realizada = true)
                        dao.actualizar(updatedCompra)
                        onSave()
                    }
                }
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        // Texto de la descripción de la compra
        Text(
            text = compra.compra,
            modifier = Modifier.weight(2f)
        )
        // Icono para eliminar elementos de compra
        Icon(
            Icons.Filled.Delete,
            contentDescription = "Eliminar Compra",
            modifier = Modifier.clickable {
                coroutineScope.launch(Dispatchers.IO) {
                    val dao = AppDatabase.getInstance(contexto).compraDao()
                    dao.eliminar(compra)
                    onSave()
                }
            }
        )
    }
}

// Composable para la página principal
@Composable
fun PaginaPrincipal(onNavigateToCompras: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Texto de bienvenida
        Text(
            text = "Bienvenido",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        // Botón para navegar a la lista de compras
        Button(onClick = { onNavigateToCompras() }) {
            Text(text = "Ir a Compras")
        }
    }
}

// Composable para manejar las pestañas
@Composable
fun TabsApp() {
    val tabs = listOf("Principal", "Compras")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column {
        // Barra de pestañas
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title) }
                )
            }
        }

        // Contenido de las pestañas
        when (selectedTabIndex) {
            0 -> PaginaPrincipal { selectedTabIndex = 1 }
            1 -> ListaComprasUI { selectedTabIndex = 0 } // Cambio de pestaña al volver
        }
    }
}











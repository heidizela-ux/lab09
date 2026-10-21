package com.example.lab09

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@Composable
fun ScreenRecetas(navController: NavHostController) {
    var recetas by remember { mutableStateOf<List<RecetaModel>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var intento by remember { mutableStateOf(0) }

    LaunchedEffect(intento) {
        cargando = true
        error = null
        try {
            recetas = RetrofitRecetas.servicio.getRecetas().recipes
        } catch (e: Exception) {
            error = "No se pudieron cargar las recetas. Revisa tu conexión."
        }
        cargando = false
    }

    when {
        cargando -> PantallaCargando()
        error != null -> PantallaError(error!!) { intento++ }
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(recetas) { receta ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("recetaVer/${receta.id}") },
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column {
                        AsyncImage(
                            model = receta.image,
                            contentDescription = receta.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                        )
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                text = receta.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${receta.cuisine} • ${receta.difficulty}",
                                color = Color.Gray
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Star,
                                    contentDescription = "Calificación",
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = " ${receta.rating} (${receta.reviewCount})",
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.weight(1f))
                                Text(
                                    text = "${receta.prepTimeMinutes + receta.cookTimeMinutes} min",
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenReceta(navController: NavHostController, id: Int) {
    var receta by remember { mutableStateOf<RecetaModel?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var intento by remember { mutableStateOf(0) }

    LaunchedEffect(intento) {
        cargando = true
        error = null
        try {
            receta = RetrofitRecetas.servicio.getRecetaById(id)
        } catch (e: Exception) {
            error = "No se pudo cargar la receta. Revisa tu conexión."
        }
        cargando = false
    }

    when {
        cargando -> PantallaCargando()
        error != null -> PantallaError(error!!) { intento++ }
        receta != null -> {
            val r = receta!!
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = r.image,
                    contentDescription = r.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                )
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = r.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip(r.cuisine)
                        InfoChip(r.difficulty)
                        InfoChip("${r.caloriesPerServing} kcal")
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip("Prep: ${r.prepTimeMinutes} min")
                        InfoChip("Cocción: ${r.cookTimeMinutes} min")
                        InfoChip("${r.servings} porciones")
                    }
                    Spacer(Modifier.height(18.dp))
                    Text("Ingredientes", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    r.ingredients.forEach { Text("• $it", modifier = Modifier.padding(vertical = 2.dp)) }
                    Spacer(Modifier.height(18.dp))
                    Text("Preparación", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    r.instructions.forEachIndexed { index, paso ->
                        Text("${index + 1}. $paso", modifier = Modifier.padding(vertical = 4.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(texto: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun PantallaCargando() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun PantallaError(mensaje: String, onReintentar: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(mensaje, textAlign = TextAlign.Center, color = Color.Red)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onReintentar) { Text("Reintentar") }
    }
}


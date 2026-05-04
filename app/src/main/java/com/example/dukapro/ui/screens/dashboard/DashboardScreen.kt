package com.example.dukapro.ui.screens.dashboard


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dukapro.navigation.Routes

// Colors
val DarkBg = Color(0xFF020617)
val CardBg = Color(0xFF0F172A)
val Border = Color(0xFF334155)
val Green = Color(0xFF16A34A)
val TextDim = Color(0xFF94A3B8)

// Data Model
data class Product(
    val name: String,
    val price: String,
    val stock: String
)

@Composable
fun DashboardScreen(navController: NavController) {

    var search by remember { mutableStateOf("") }

    // Sample data (later replace with Firebase)
    val products = listOf(
        Product("Sugar 2kg", "KES 320", "12 in stock"),
        Product("Milk Packet", "KES 60", "40 in stock"),
        Product("Bread", "KES 70", "25 in stock"),
        Product("Cooking Oil", "KES 350", "10 in stock"),
        Product("Maize Flour 2kg", "KES 180", "15 in stock"),
        Product("Salt 1kg", "KES 35", "50 in stock"),
        Product("Tea Leaves", "KES 50", "30 in stock"),
        Product("Rice 5kg", "KES 850", "8 in stock")
    )

    val filtered = products.filter {
        it.name.contains(search, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // 🔝 Header
            Text(
                text = "DukaPro Dashboard",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 🔍 Search
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("Search product...", color = TextDim) },
                leadingIcon = { Icon(
                    Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 📊 Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Products", "${products.size}", Modifier.weight(1f))
                StatCard("Sales", "KES 12K", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Your Products",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 📦 Product List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered) { product ->
                    ProductCard(product) {
                        navController.navigate(Routes.PAYMENT)
                    }
                }
            }
        }

        // ➕ Floating Button
        FloatingActionButton(
            onClick = {
                navController.navigate(Routes.ADD_PRODUCT)
            },
            containerColor = Green,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }
}

// 📊 Stat Card
@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {

    Card(
        modifier = modifier.height(90.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(value, color = Color.White, fontWeight = FontWeight.Bold)
            Text(title, color = TextDim, fontSize = 12.sp)
        }
    }
}

// 📦 Product Card
@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(14.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(product.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text(product.price, color = Green)
                Text(product.stock, color = TextDim, fontSize = 12.sp)
            }

            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextDim)
        }
    }
}

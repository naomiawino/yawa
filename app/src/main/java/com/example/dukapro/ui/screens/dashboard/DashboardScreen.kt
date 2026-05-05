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
import com.google.firebase.firestore.FirebaseFirestore

// Colors
val DarkBg = Color(0xFF020617)
val CardBg = Color(0xFF0F172A)
val Border = Color(0xFF334155)
val Green = Color(0xFF16A34A)
val TextDim = Color(0xFF94A3B8)

// Data Model
data class Product(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val stock: String = ""
)

@Composable
fun DashboardScreen(navController: NavController) {

    var search by remember { mutableStateOf("") }
    val products = remember { mutableStateListOf<Product>() }
    var totalSales by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch products and calculate sales from Firebase
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        
        // Listen for products
        db.collection("products").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                products.clear()
                products.addAll(snapshot.toObjects(Product::class.java).mapIndexed { index, p -> 
                    p.copy(id = snapshot.documents[index].id)
                })
            }
            isLoading = false
        }

        // Listen for sales
        db.collection("payments").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                var sum = 0
                for (doc in snapshot.documents) {
                    val amountStr = doc.getString("amount") ?: "0"
                    // Extract number from "KES 300"
                    val numericValue = amountStr.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                    sum += numericValue
                }
                totalSales = sum
            }
        }
    }

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
                StatCard("Sales", "KES ${totalSales}", Modifier.weight(1f))
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
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Green)
                }
            } else if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No products found. Add some!", color = TextDim)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered) { product ->
                        ProductCard(product) {
                            navController.navigate("${Routes.PAYMENT}/${product.price}")
                        }
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

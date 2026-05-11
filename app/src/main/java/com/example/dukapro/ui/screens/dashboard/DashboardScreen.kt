package com.example.dukapro.ui.screens.dashboard


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dukapro.data.models.Product
import com.example.dukapro.ui.viewmodel.DukaViewModel
import com.example.dukapro.navigation.Routes
import com.example.dukapro.ui.theme.*

@Composable
fun DashboardScreen(navController: NavController, viewModel: DukaViewModel = viewModel()) {

    var search by remember { mutableStateOf("") }
    val products by viewModel.products.collectAsState()
    val totalSales by viewModel.totalSales.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val filtered = products.filter {
        it.name.contains(search, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // 🔝 Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DukaPro Store",
                        color = PrimaryTeal,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Welcome back!",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }

                IconButton(
                    onClick = {
                        viewModel.signOut()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.DASHBOARD) { inclusive = true }
                        }
                    },
                    modifier = Modifier.background(AccentPeach.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = AccentPeach)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔍 Search
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("Search products...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = PrimaryTeal) },
                trailingIcon = {
                    if (search.isNotEmpty()) {
                        IconButton(onClick = { search = "" }) {
                            Icon(Icons.Default.Clear, null, tint = TextSecondary)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = PrimaryTeal,
                    unfocusedBorderColor = Border,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = PrimaryTeal
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 📊 Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Products", "${products.size}", Modifier.weight(1f))
                StatCard("Total Sales", "KES ${totalSales}", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 📦 Orders Shortcut
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Routes.ORDERS) },
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentPeach.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ListAlt, contentDescription = null, tint = AccentPeach)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Manage Orders", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("Track customer deliveries", color = TextSecondary, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your Inventory",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 📦 Product Grid
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentPeach)
                }
            } else if (products.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Your store is empty.", color = TextSecondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.seedSampleProducts() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                    ) {
                        Text("Add 20 Sample Products", color = Color.White)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
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
            containerColor = PrimaryTeal,
            contentColor = Color.White,
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
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, color = PrimaryTeal, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text(title, color = TextSecondary, fontSize = 12.sp)
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
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Placeholder for Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(PrimaryTeal.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Inventory2, contentDescription = null, tint = PrimaryTeal.copy(alpha = 0.3f), modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = product.name,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1
            )
            
            Text(
                text = "KES ${product.price}",
                color = PrimaryTeal,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(12.dp), tint = TextSecondary)
                Spacer(modifier = Modifier.width(4.dp))
                Text(product.stock, color = TextSecondary, fontSize = 11.sp)
            }
        }
    }
}

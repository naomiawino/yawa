package com.example.dukapro.ui.screens.login


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dukapro.ui.viewmodel.DukaViewModel
import com.example.dukapro.ui.theme.*

@Composable
fun LoginScreen(navController: NavController, viewModel: DukaViewModel = viewModel()) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // rest of your UI...

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            // 🏪 Attractive Logo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(PrimaryTeal, AccentPeach)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "DukaPro Store",
                color = PrimaryTeal,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Login to continue shopping with us",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 📦 Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border.copy(alpha = 0.5f))
            ) {

                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address", color = TextSecondary) },
                        placeholder = { Text("Enter your email", color = TextSecondary) },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = PrimaryTeal) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = Border,
                            focusedLabelColor = PrimaryTeal,
                            unfocusedLabelColor = TextSecondary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = PrimaryTeal
                        )
                    )

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = TextSecondary) },
                        placeholder = { Text("Enter your password", color = TextSecondary) },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = PrimaryTeal) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible)
                                        Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    null,
                                    tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = Border,
                            focusedLabelColor = PrimaryTeal,
                            unfocusedLabelColor = TextSecondary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = PrimaryTeal
                        )
                    )

                    // ❗ Error Message
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                   val scope = rememberCoroutineScope()

                    // 🔐 Login Button
                    Button(
                        onClick = {

                            // Basic validation
                            when {
                                email.isEmpty() -> {
                                    errorMessage = "Email is required"
                                }
                                password.isEmpty() -> {
                                    errorMessage = "Password is required"
                                }
                                else -> {
                                    errorMessage = ""
                                    isLoading = true

                                    val trimmedEmail = email.trim()
                                    val trimmedPassword = password.trim()
                                    viewModel.signIn(trimmedEmail, trimmedPassword)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                navController.navigate("dashboard") {
                                                    popUpTo("login") { inclusive = true }
                                                }
                                            } else {
                                                isLoading = false
                                                errorMessage = task.exception?.message ?: "Login failed"
                                            }
                                        }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Login", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // 🧠 Forgot Password (placeholder)
                    Text(
                        text = "Forgot Password?",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.End)
                    )

                    // 🔗 Register Link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("No account? ", color = TextSecondary)
                        Text(
                            text = "Register Now",
                            color = PrimaryTeal,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate("register")
                            }
                        )
                    }
                }
            }
        }
    }
}
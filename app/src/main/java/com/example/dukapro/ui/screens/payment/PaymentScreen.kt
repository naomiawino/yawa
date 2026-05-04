package com.example.dukapro.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Reusing colors from Dashboard for consistency
val DarkBg = Color(0xFF020617)
val CardBg = Color(0xFF0F172A)
val Green = Color(0xFF16A34A)
val TextDim = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(navController: NavController) {
    var selectedMethod by remember { mutableStateOf<String?>(null) }
    var selectedBank by remember { mutableStateOf<String?>(null) }
    var phoneNumber by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var paymentStatus by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val banks = listOf("Equity Bank", "Co-op Bank", "KCB Bank", "NCBA Bank", "Family Bank")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        containerColor = DarkBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (paymentStatus == null) {
                Text(
                    text = "Select Payment Method",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // M-Pesa Option
                PaymentMethodCard(
                    title = "M-Pesa",
                    selected = selectedMethod == "mpesa",
                    onClick = {
                        selectedMethod = "mpesa"
                        selectedBank = null
                    }
                )

                if (selectedMethod == "mpesa") {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { if (it.length <= 10) phoneNumber = it },
                        label = { Text("M-Pesa Phone Number", color = TextDim) },
                        placeholder = { Text("0712345678", color = TextDim) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Green) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Green,
                            unfocusedBorderColor = Border
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Card Option
                PaymentMethodCard(
                    title = "Credit/Debit Card",
                    selected = selectedMethod == "card",
                    onClick = { selectedMethod = "card" }
                )

                if (selectedMethod == "card") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Select Your Bank",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(banks) { bank ->
                            BankSelectionCard(
                                bankName = bank,
                                isSelected = selectedBank == bank,
                                onSelect = { selectedBank = bank }
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Button(
                    onClick = {
                        isProcessing = true
                        scope.launch {
                            delay(2000) // Simulate processing
                            if (selectedMethod == "mpesa") {
                                paymentStatus = "Prompt sent to $phoneNumber. Please enter your M-Pesa PIN on your phone."
                            } else {
                                paymentStatus = "Card payment via $selectedBank successful!"
                            }
                            isProcessing = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isProcessing && (
                        (selectedMethod == "mpesa" && phoneNumber.length >= 10) || 
                        (selectedMethod == "card" && selectedBank != null)
                    )
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Pay Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Success/Status View
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle, 
                        contentDescription = null, 
                        tint = Green, 
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Payment Initiated",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = paymentStatus!!,
                        color = TextDim,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = CardBg),
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Text("Back to Dashboard", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(title: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Green.copy(alpha = 0.1f) else CardBg
        ),
        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, Green) else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            if (selected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Green)
            }
        }
    }
}

@Composable
fun BankSelectionCard(bankName: String, isSelected: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Green.copy(alpha = 0.2f) else CardBg.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = Green, unselectedColor = TextDim)
            )
            Text(bankName, color = Color.White, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

val Border = Color(0xFF334155)

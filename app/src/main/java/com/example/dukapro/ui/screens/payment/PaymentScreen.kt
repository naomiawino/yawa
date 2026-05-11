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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dukapro.ui.viewmodel.DukaViewModel
import com.example.dukapro.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(navController: NavController, productPrice: String, viewModel: DukaViewModel = viewModel()) {
    var selectedMethod by remember { mutableStateOf<String?>(null) }
    var selectedBank by remember { mutableStateOf<String?>(null) }
    var phoneNumber by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var paymentStatus by remember { mutableStateOf<String?>(null) }
    var showMpesaDialog by remember { mutableStateOf(false) }
    var mpesaPin by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()
    val banks = listOf("Equity Bank", "Co-op Bank", "KCB Bank", "NCBA Bank", "Family Bank")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout - $productPrice", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        containerColor = BackgroundWhite
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
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // M-Pesa Option
                PaymentMethodCard(
                    title = "M-Pesa ($productPrice)",
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
                        label = { Text("M-Pesa Phone Number", color = TextSecondary) },
                        placeholder = { Text("0712345678", color = TextSecondary) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryTeal) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = Border,
                            focusedLabelColor = PrimaryTeal,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
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
                        color = TextPrimary,
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
                        if (selectedMethod == "mpesa") {
                            isProcessing = true
                            scope.launch {
                                delay(1500) // Realistic delay for "Requesting..."
                                isProcessing = false
                                showMpesaDialog = true
                            }
                        } else {
                            isProcessing = true
                            scope.launch {
                                delay(2000)
                                paymentStatus = "Card payment via $selectedBank successful!"
                                isProcessing = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isProcessing && (
                        (selectedMethod == "mpesa" && phoneNumber.length >= 10) || 
                        (selectedMethod == "card" && selectedBank != null)
                    )
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Pay $productPrice", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                        tint = Color(0xFF2E7D32), 
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Payment Completed",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = paymentStatus!!,
                        color = TextSecondary,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        Text("Back to Dashboard", color = Color.White)
                    }
                }
            }
        }
    }

    // 📱 LIVELY M-PESA PIN PROMPT (SIMULATION)
    if (showMpesaDialog) {
        AlertDialog(
            onDismissRequest = { showMpesaDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val transactionId = "RI${(10000..99999).random()}QYMN"
                    showMpesaDialog = false
                    isProcessing = true
                    
                    val paymentData: Map<String, Any?> = hashMapOf(
                        "amount" to productPrice,
                        "phoneNumber" to phoneNumber,
                        "transactionId" to transactionId,
                        "method" to "M-Pesa",
                        "timestamp" to com.google.firebase.Timestamp.now(),
                        "userId" to viewModel.getUserId(),
                        "status" to "Ordered"
                    )

                    viewModel.addOrder(paymentData) { success ->
                        isProcessing = false
                        if (success) {
                            paymentStatus = "Payment confirmed. Transaction ID: $transactionId"
                        } else {
                            paymentStatus = "Payment saved locally, but failed to sync to cloud."
                        }
                    }
                }) {
                    Text("OK", color = PrimaryTeal, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMpesaDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            title = {
                Text("M-Pesa", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column {
                    Text(
                        "Enter PIN for DukaPro Store $productPrice",
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = mpesaPin,
                        onValueChange = { if (it.length <= 4) mpesaPin = it },
                        placeholder = { Text("PIN") },
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = Border,
                            focusedLabelColor = PrimaryTeal,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun PaymentMethodCard(title: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) AccentPeach.copy(alpha = 0.1f) else CardBg
        ),
        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, AccentPeach) else androidx.compose.foundation.BorderStroke(1.dp, Border),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold)
            if (selected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentPeach)
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
            containerColor = if (isSelected) AccentPeach.copy(alpha = 0.2f) else CardBg.copy(alpha = 0.5f)
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
                colors = RadioButtonDefaults.colors(selectedColor = AccentPeach, unselectedColor = TextSecondary)
            )
            Text(bankName, color = TextPrimary, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

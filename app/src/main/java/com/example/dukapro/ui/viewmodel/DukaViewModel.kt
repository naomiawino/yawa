package com.example.dukapro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dukapro.data.models.Order
import com.example.dukapro.data.models.Product
import com.example.dukapro.data.models.User
import com.example.dukapro.data.repository.FirebaseRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DukaViewModel(private val repository: FirebaseRepository = FirebaseRepository()) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _totalSales = MutableStateFlow(0)
    val totalSales: StateFlow<Int> = _totalSales.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                launch {
                    repository.getProducts().collect {
                        _products.value = it
                        _isLoading.value = false
                    }
                }
                launch {
                    repository.getOrders().collect {
                        _orders.value = it
                    }
                }
                launch {
                    repository.getTotalSales().collect {
                        _totalSales.value = it
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    // Auth
    fun signIn(email: String, password: String): Task<AuthResult> = repository.signIn(email, password)
    fun signUp(email: String, password: String): Task<AuthResult> = repository.signUp(email, password)
    fun getUserId() = repository.getUserId()
    fun signOut() = repository.signOut()

    fun saveUser(user: User, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.saveUser(user)
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    // Products
    fun addProduct(name: String, price: String, stock: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                repository.addProduct(name, price, stock)
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

    // Orders
    fun addOrder(orderData: Map<String, Any?>, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.addOrder(orderData)
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
        }
    }

    fun seedSampleProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            val sampleProducts = listOf(
                Product(name = "Smart Watch Series 7", price = "12000", stock = "15"),
                Product(name = "Wireless Earbuds Pro", price = "4500", stock = "30"),
                Product(name = "Gaming Mouse RGB", price = "2500", stock = "20"),
                Product(name = "Mechanical Keyboard", price = "7000", stock = "10"),
                Product(name = "HD Webcam 1080p", price = "3500", stock = "25"),
                Product(name = "Bluetooth Speaker", price = "5000", stock = "40"),
                Product(name = "Portable Power Bank", price = "3000", stock = "50"),
                Product(name = "USB-C Hub Adapter", price = "2000", stock = "35"),
                Product(name = "Laptop Stand", price = "1500", stock = "60"),
                Product(name = "LED Desk Lamp", price = "2200", stock = "45"),
                Product(name = "Smartphone Gimbal", price = "9000", stock = "8"),
                Product(name = "External SSD 1TB", price = "15000", stock = "12"),
                Product(name = "Wireless Charger", price = "1800", stock = "70"),
                Product(name = "Noise Canceling Headphones", price = "25000", stock = "5"),
                Product(name = "Dual Monitor Arm", price = "8000", stock = "15"),
                Product(name = "Smart Home Plug", price = "1200", stock = "100"),
                Product(name = "Fitness Tracker", price = "3800", stock = "40"),
                Product(name = "Graphic Tablet", price = "11000", stock = "10"),
                Product(name = "Electric Screwdriver", price = "4000", stock = "20"),
                Product(name = "VR Headset", price = "45000", stock = "3")
            )
            repository.seedProducts(sampleProducts)
            _isLoading.value = false
        }
    }
}

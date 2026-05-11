package com.example.dukapro.data.repository

import com.example.dukapro.data.models.Order
import com.example.dukapro.data.models.Product
import com.example.dukapro.data.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Auth
    fun getCurrentUser() = auth.currentUser
    fun getUserId() = auth.currentUser?.uid
    fun signOut() = auth.signOut()

    fun signIn(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    fun signUp(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    fun getProducts(): Flow<List<Product>> = callbackFlow {
        val subscription = db.collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList()) // Return empty on error to stop loading
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val products = snapshot.toObjects(Product::class.java).mapIndexed { index, p ->
                        p.copy(id = snapshot.documents[index].id)
                    }
                    trySend(products)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addProduct(name: String, price: String, stock: String) {
        val product = hashMapOf(
            "name" to name,
            "price" to price,
            "stock" to stock
        )
        db.collection("products").add(product).await()
    }

    fun getOrders(): Flow<List<Order>> = callbackFlow {
        val subscription = db.collection("payments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val orders = snapshot.toObjects(Order::class.java).mapIndexed { index, order ->
                        order.copy(id = snapshot.documents[index].id)
                    }
                    trySend(orders)
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getTotalSales(): Flow<Int> = callbackFlow {
        val subscription = db.collection("payments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(0)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    var sum = 0
                    for (doc in snapshot.documents) {
                        val amountStr = doc.getString("amount") ?: "0"
                        val numericValue = amountStr.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                        sum += numericValue
                    }
                    trySend(sum)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addOrder(orderData: Map<String, Any?>) {
        db.collection("payments").add(orderData).await()
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        db.collection("payments").document(orderId).update("status", newStatus).await()
    }

    // Users
    suspend fun saveUser(user: User) {
        db.collection("users").document(user.userId).set(user).await()
    }
}

package com.example.dukapro.data.models

import com.google.firebase.Timestamp

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = ""
)

data class Product(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val stock: String = ""
)

data class Order(
    val id: String = "",
    val userId: String = "",
    val amount: String = "",
    val status: String = "Ordered",
    val timestamp: Timestamp? = null,
    val phoneNumber: String = ""
)

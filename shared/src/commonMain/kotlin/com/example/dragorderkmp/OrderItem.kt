package com.example.dragorderkmp

data class OrderItem(
    val name: String,
    val price: Int,
    val imageUrl: String,
    val qty: Int = 0
)

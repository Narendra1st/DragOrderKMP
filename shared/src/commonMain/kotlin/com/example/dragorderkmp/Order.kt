package com.example.dragorderkmp

data class Order(
    val id: String,
    val name: String,
    val tableNo: String,
    val items: List<OrderItem> = emptyList(),
    val qty: Int = 0,
    val total: Int = 0
)




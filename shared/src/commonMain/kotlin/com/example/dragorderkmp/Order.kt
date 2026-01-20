package com.example.dragorderkmp

data class Order(
    val id: String,
    val name: String,
    val tableNo: String,
    val items: MutableList<OrderItem>,
    val qty: Int = 0,
    val total: Int = 0
)




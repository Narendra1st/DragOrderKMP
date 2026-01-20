package com.example.dragorderkmp.android.viewmodel

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.dragorderkmp.Order
import com.example.dragorderkmp.OrderItem
import com.example.dragorderkmp.android.storage.OrderStorage
import androidx.compose.ui.geometry.Rect

class OrderViewModel(private val context: Context) : ViewModel() {
    var selectedDropArea by mutableStateOf<androidx.compose.ui.geometry.Rect?>(null)
    var orderDropAreas by mutableStateOf<Map<String, Rect>>(emptyMap())

    var showPopup by mutableStateOf(false)

    var orderId by mutableStateOf("")
    var name by mutableStateOf("")
    var tableNo by mutableStateOf("")
    var error by mutableStateOf("")

    var orders = mutableStateListOf<Order>()
    var selectedOrderId by mutableStateOf<String?>(null)

    init {
        orders.addAll(OrderStorage.load(context))
        if (orders.isEmpty()) {
            var order1 = Order(
                id = "1011",
                name = "Sam",
                tableNo = "10",
                items = mutableListOf<OrderItem>()
            )
            var order2 = Order(
                id = "1012",
                name = "Ram",
                tableNo = "11",
                items = mutableListOf<OrderItem>()
            )
            orders.add(order1)
            orders.add(order2)
        }
    }
    fun createOrder() {
        error = ""

        if (orderId.isBlank()) {
            error = "Order ID required"; return
        }
        if (name.isBlank()) {
            error = "Name required"; return
        }
        if (tableNo.isBlank()) {
            error = "Table No required"; return
        }

        val existing = orders.find { it.id == orderId }

        if (existing != null) {
            selectedOrderId = existing.id
        } else {
            val newOrder = Order(orderId, name, tableNo, mutableListOf())
            orders.add(newOrder)
            selectedOrderId = orderId
        }

        OrderStorage.save(context, orders)
        orderId = ""; name = ""; tableNo = ""
        showPopup = false
    }

    fun selectOrder(id: String) {
        selectedOrderId = id
    }

    fun addItem(item: OrderItem) {
        val index = orders.indexOfFirst { it.id == selectedOrderId }
        if (index == -1) return

        val old = orders[index]
        val newItems = old.items.toMutableList()
        newItems.add(item)

        orders[index] = old.copy(
            items = newItems,
            qty = old.qty + 1
        )
        OrderStorage.save(context, orders)
        Toast.makeText(context, "item added to cart successfully", Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.CENTER, 0, -200)
            show()
        }
    }

    fun addItemTo(orderId: String, item: OrderItem) {
        val index = orders.indexOfFirst { it.id == orderId }
        if (index == -1) return

        val old = orders[index]
        val newItems = old.items.toMutableList()
        newItems.add(item)

        orders[index] = old.copy(
            items = newItems,
            qty = old.qty + 1
        )
        OrderStorage.save(context, orders)
        Toast.makeText(context, "item added to cart successfully", Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.CENTER, 0, -200)
            show()
        }
    }



    fun saveSelectedOrder() {
        val index = orders.indexOfFirst { it.id == selectedOrderId }
        if (index == -1) return

        val old = orders[index]

        if (old.items.isEmpty()) {
            error = "At least one item add karo"
            return
        }

        val total = old.items.sumOf { it.price }

        orders[index] = old.copy(
            total = total
        )

        error = ""
        OrderStorage.save(context, orders)
    }


    fun selectedOrder(): Order? =
        orders.find { it.id == selectedOrderId }

    fun removeOrder(id: String) {
        orders.removeAll { it.id == id }
        if (selectedOrderId == id) {
            selectedOrderId = null
        }
        // clean up drop areas map
        orderDropAreas = orderDropAreas.filterKeys { it != id }
        OrderStorage.save(context, orders)
    }


//    fun total(): Int =
//        selectedOrder()?.total ?: 0
}

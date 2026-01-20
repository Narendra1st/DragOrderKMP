package com.example.dragorderkmp.android.viewmodel

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.dragorderkmp.Order
import com.example.dragorderkmp.OrderItem
import com.example.dragorderkmp.android.storage.OrderStorage
import androidx.compose.ui.geometry.Rect

class OrderViewModel(private val context: Context) : ViewModel() {

    // Selection
    var selectedOrderId by mutableStateOf<String?>(null)
    var selectedTable by mutableStateOf<String?>(null)
    var selectedDropArea by mutableStateOf<Rect?>(null)

    fun selectOrder(id: String) {
        selectedOrderId = id
    }

    fun selectTable(tableId: String) {
        selectedTable = tableId
    }

    // Drag-drop areas
    var orderDropAreas = mutableStateMapOf<String, Rect>()

    // Data
    var orders = mutableStateListOf<Order>()

    // UI State
    var showPopup by mutableStateOf(false)
    var name by mutableStateOf("")
    var error by mutableStateOf("")

    // Helpers
    fun countInTable(table: String?): Int {
        if (table == null) return 0
        return orders.count { it.tableNo == table }
    }

    // Create seat/person (max 4 per table)
    fun createSeatForSelectedTable() {
        val table = selectedTable ?: return
        val count = countInTable(table)

        if (count >= 4) {
            error = "Only 4 seats allowed in $table"
            return
        }

        val seatNo = count + 1
        val seatName = if (name.isBlank()) "Seat-$seatNo" else name

        orders.add(
            Order(
                id = "$table-S$seatNo",
                name = seatName,
                tableNo = table,
                items = emptyList(),
                qty = 0,
                total = 0
            )
        )

        name = ""
        showPopup = false
    }
    fun removeOrder(id: String) {
        orders.removeAll { it.id == id }

        // selection clean
        if (selectedOrderId == id) selectedOrderId = null

        // drop area clean
        orderDropAreas.remove(id)
    }

    // Drag-drop add item
    fun addItemTo(orderId: String, item: OrderItem) {
        val index = orders.indexOfFirst { it.id == orderId }
        if (index == -1) return

        val old = orders[index]
        val newItems = old.items.toMutableList()
        newItems.add(item)

        orders[index] = old.copy(
            items = newItems,
            qty = newItems.size,
            total = newItems.sumOf { it.price }
        )
    }
}

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

class OrderViewModel(val context: Context) : ViewModel() {

    // Selection
    var selectedOrderId by mutableStateOf<String?>(null)
    var selectedTable by mutableStateOf<String?>(null)
    var selectedDropArea by mutableStateOf<Rect?>(null)

    fun selectOrder(id: String) {
        selectedOrderId = id
    }

    fun selectTable(tableId: String) {
        selectedTable = tableId
        // Clear old drop areas when switching tables
        orderDropAreas.clear()
    }

    // Drag-drop areas
    var orderDropAreas = mutableStateMapOf<String, Rect>()

    // Data
    var orders = mutableStateListOf<Order>()

    // UI State
    var showPayPopup by mutableStateOf(false)

    var showPopup by mutableStateOf(false)
    var name by mutableStateOf("")
    var error by mutableStateOf("")
    init {
        val saved = OrderStorage.load(context)
        if (saved.isNotEmpty()) {
            orders.addAll(saved)
        }
        selectTable("RT-01")
    }
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

        // Name is required
        if (name.trim().isEmpty()) {
            error = "Please enter a seat name"
            return
        }

        val seatNo = count + 1

        orders.add(
            Order(
                id = "$table-S$seatNo",
                name = name.trim(),
                tableNo = table,
                items = emptyList(),
                qty = 0,
                total = 0
            )
        )
        OrderStorage.save(context, orders)
        // Clear state after creation
        name = ""
        error = ""
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
        
        // Check if item already exists in order
        val existingItemIndex = newItems.indexOfFirst { it.name == item.name }
        
        if (existingItemIndex != -1) {
            // Item exists, increment qty
            val existingItem = newItems[existingItemIndex]
            newItems[existingItemIndex] = existingItem.copy(qty = existingItem.qty + 1)
        } else {
            // New item, add with qty = 1
            newItems.add(item.copy(qty = 1))
        }

        orders[index] = old.copy(
            items = newItems,
            qty = newItems.sumOf { it.qty },
            total = newItems.sumOf { it.price * it.qty }
        )
    }
}

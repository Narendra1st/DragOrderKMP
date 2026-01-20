package com.example.dragorderkmp.android.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dragorderkmp.android.viewmodel.OrderViewModel

@Composable
fun BillSection(vm: OrderViewModel) {
    val table = vm.selectedTable ?: return
    val order = vm.orders.firstOrNull { it.tableNo == table } ?: return

    Column(Modifier.padding(8.dp)) {
        Text("Current Person", style = MaterialTheme.typography.titleMedium)
        Text("ID: ${order.id}")
        Text("Name: ${order.name}")
        Text("Table: ${order.tableNo}")
        Text("Qty: ${order.qty}", color = MaterialTheme.colorScheme.primary)
    }
}

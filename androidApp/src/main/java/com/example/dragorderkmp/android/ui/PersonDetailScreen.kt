package com.example.dragorderkmp.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dragorderkmp.android.viewmodel.OrderViewModel

@Composable
fun PersonDetailScreen(orderId: String) {

    val context = LocalContext.current

    val vm: OrderViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OrderViewModel(context) as T
            }
        }
    )

    val order = vm.orders.find { it.id == orderId }

    if (order == null) {
        Text("Order not found", modifier = Modifier.padding(16.dp))
        return
    }

    Column(Modifier
        .fillMaxHeight()
        .background(Color(0xFFF3F1ED))
        .border(10.dp, Color.Black)
        .padding(16.dp)) {
        Text("Name: ${order.name}")
        Text("ID: ${order.id}")
        Text("Table: ${order.tableNo}")

        Spacer(Modifier.height(8.dp))
        Text("Items:")

        order.items.forEach {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text(it.name)
                Text("₹${it.price}")
            }
        }

        Divider(color = Color.Black,
            thickness = 2.dp,
            modifier = Modifier.padding(vertical = 8.dp))
        Text("Total: ₹${order.items.sumOf { it.price }}")
    }
}

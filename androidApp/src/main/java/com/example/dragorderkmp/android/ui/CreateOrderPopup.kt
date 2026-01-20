package com.example.dragorderkmp.android.ui
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dragorderkmp.android.viewmodel.OrderViewModel

@Composable
fun CreateOrderPopup(vm: OrderViewModel) {
    AlertDialog(
        onDismissRequest = { vm.showPopup = false },
        title = { Text("Create Person") },
        text = {
            Column {
                TextField(vm.orderId, { vm.orderId = it }, label = { Text("ID") })
                TextField(vm.name, { vm.name = it }, label = { Text("Name") })
                TextField(vm.tableNo, { vm.tableNo = it }, label = { Text("Table No") })
                val selected = vm.selectedOrder()

                if (selected != null) {
                    Spacer(Modifier.height(8.dp))
                    Text("Qty: ${selected.qty}")
                }

//                if (vm.selectedOrderId != null) {
//                    val order = vm.selectedOrder()
//                    Text("Qty: ${order?.qty ?: 0}")
//                }

            }
        },
        confirmButton = {
            Button(onClick = { vm.createOrder() }) {
                Text("Create")
            }
        }
    )
}


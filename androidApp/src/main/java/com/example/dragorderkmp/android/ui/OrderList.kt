package com.example.dragorderkmp.android.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dragorderkmp.android.viewmodel.OrderViewModel
import androidx.navigation.compose.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderList(vm: OrderViewModel, navController: NavController) {
    Column {
        Text("Persons / Orders", style = MaterialTheme.typography.titleMedium)

        vm.orders.forEach { order ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                        .onGloballyPositioned { coords ->
                            val pos = coords.positionInWindow()
                            val size = coords.size
                            val rect = androidx.compose.ui.geometry.Rect(
                                pos.x,
                                pos.y,
                                pos.x + size.width,
                                pos.y + size.height
                            )

                            // update map of orderId -> rect so drops can be detected without explicit selection
                            val newMap = vm.orderDropAreas.toMutableMap()
                            newMap[order.id] = rect
                            vm.orderDropAreas = newMap
                            // if this is the selected order, also set selectedDropArea for compatibility
                            if (vm.selectedOrderId == order.id) {
                                vm.selectedDropArea = rect
                            }
                    }
                    .combinedClickable(
                        onClick = {
                            vm.selectOrder(order.id)   // sirf select/highlight
                        },
                        onLongClick = {
                            vm.selectOrder(order.id)
                            navController.navigate("detail/${order.id}")
                        }
                    ),
                colors = CardDefaults.cardColors(
                    containerColor =
                    if (vm.selectedOrderId == order.id)
                        Color(0xFFB3E5FC)   // selected color
                    else
                        Color.White
                )
            ) {
                Row(
                    Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ID: ${order.id}", fontSize = 14.sp)
                        Text("Name: ${order.name}", fontSize = 14.sp)
                        Text("Table: ${order.tableNo}", fontSize = 14.sp)
                        Text(
                            "Qty: ${order.qty}",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 15.sp
                        )
                    }

                    IconButton(onClick = { vm.removeOrder(order.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove")
                    }
                }
            }
        }
    }
}

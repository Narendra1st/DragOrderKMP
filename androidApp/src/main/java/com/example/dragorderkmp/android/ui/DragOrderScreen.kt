package com.example.dragorderkmp.android.ui

import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dragorderkmp.OrderItem
import com.example.dragorderkmp.android.viewmodel.OrderViewModel


@Composable
fun DragOrderScreen(navController: NavController) {

    val context = LocalContext.current
    val vm: OrderViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OrderViewModel(context) as T
            }
        }
    )

    val availableItems = listOf(
        OrderItem("Pizza",120,"🍕"),
        OrderItem("Burger",80,"🍔"),
        OrderItem("Samosa",20,"🥟"),
        OrderItem("Cake",100,"🍰"),
        OrderItem("Tea",80,"🍵"),
        OrderItem("Coffee",80,"☕"),
        OrderItem("Milkshake",80,"🥤"),
        OrderItem("Ice Cream",80,"🍨"),
        OrderItem("Donut",80,"🍩"),
        OrderItem("Burrito",80,"🌯"),
        OrderItem("Taco",80,"🌮"),
        OrderItem("Chicken Nuggets",80,"🍗"),
        OrderItem("Sandwich",80,"🥪"),
        OrderItem("Hot Dog",80,"🌭"),
        OrderItem("Item15",150,"⭐")   // new item
    )


    var rightPanelOffset by remember { mutableStateOf(Offset.Zero) }

    Box(Modifier.fillMaxSize().background(Color(0xFF8D7070))) {

        Row(Modifier.fillMaxSize().padding(12.dp)) {

            // LEFT PANEL
            Column(
                Modifier
                    .weight(1f)
                    .background(Color(0xFFBD5A5A))
                    .padding(6.dp)
            ) {

                // Step 1: Table Select
                Row {
                    listOf("RT-01","RT-02","RT-03","RT-04").forEach { table ->
                        Button(
                            onClick = { vm.selectTable(table) },
                            colors = ButtonDefaults.buttonColors(
                                if (vm.selectedTable == table)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(table)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Step 2: Selected Table ka Common Layout
                val persons = vm.orders.filter { it.tableNo == vm.selectedTable }

                CommonTable4Persons(persons) { id, rect ->
                    vm.orderDropAreas[id] = rect
                }
            }

            Spacer(
                Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(Color(0xFF383734))
            )

            // RIGHT PANEL (ITEMS)
            Column(
                Modifier
                    .weight(0.5f)
                    .padding(6.dp)
                    .onGloballyPositioned { coords ->
                        rightPanelOffset = coords.positionInWindow()
                    }
            ) {
                Text("Items",style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                availableItems.chunked(2).forEach { rowItems ->
                    Row {
                        rowItems.forEach { item ->

                            var offset by remember { mutableStateOf(Offset.Zero) }
                            var itemGlobalOffset by remember { mutableStateOf(Offset.Zero) }

                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(6.dp)
                                    .onGloballyPositioned { coords ->
                                        itemGlobalOffset = coords.positionInWindow()
                                    }
                                    .offset { IntOffset(offset.x.toInt(), offset.y.toInt()) }
                                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                                    .pointerInput(item) {
                                        awaitEachGesture {
                                            val down = awaitFirstDown()
                                            offset = Offset.Zero
                                            val pointer = down.id
                                            var hasDragged = false

                                            while (true) {
                                                val event = awaitPointerEvent()
                                                val change = event.changes.firstOrNull { it.id == pointer } ?: break

                                                if (change.pressed) {
                                                    val delta = change.positionChange()
                                                    if (delta != Offset.Zero) hasDragged = true
                                                    offset += delta
                                                    if (delta != Offset.Zero) change.consume()
                                                } else break
                                            }

                                            val dropX = itemGlobalOffset.x + down.position.x + offset.x
                                            val dropY = itemGlobalOffset.y + down.position.y + offset.y

                                            if (hasDragged) {
                                                var matchedId: String? = null
                                                vm.orderDropAreas.forEach { (id, rect) ->
                                                    if (dropX in rect.left..rect.right && dropY in rect.top..rect.bottom) {
                                                        matchedId = id
                                                    }
                                                }

                                                if (matchedId != null) {
                                                    vm.addItemTo(matchedId!!, item)
                                                } else {
                                                    Toast.makeText(context, "Item not dropped on seat", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            offset = Offset.Zero
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = item.imageUrl,   // yahan emoji ya icon string hoga
                                        fontSize = 32.sp,
                                        modifier = Modifier.size(45.dp)
                                    )
                                    Text(item.name)
                                    Text("₹${item.price}")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Step 3: Popup
        if (vm.showPopup) {
            CreateOrderPopup(vm)
        }

        // “+” Button
        FloatingActionButton(
            onClick = {
                if (vm.selectedTable != null) {
                    vm.showPopup = true
                } else {
                    Toast.makeText(context, "Select Table First", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Seat")
        }
    }
}

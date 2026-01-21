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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dragorderkmp.OrderItem
import com.example.dragorderkmp.android.storage.OrderStorage
import com.example.dragorderkmp.android.viewmodel.OrderViewModel
import com.example.dragorderkmp.android.payment.PayPopup


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
        OrderItem("Item15",150,"⭐")
    )

    var rightPanelOffset by remember { mutableStateOf(Offset.Zero) }
    var draggedItem by remember { mutableStateOf<OrderItem?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var dragStartPos by remember { mutableStateOf(Offset.Zero) }
    var dragItemGlobalPos by remember { mutableStateOf(Offset.Zero) }

    Box(Modifier.fillMaxSize().background(Color(0xFF8D7070))) {

        Row(Modifier.fillMaxSize().padding(12.dp)) {

            // LEFT PANEL
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFFBD5A5A), RoundedCornerShape(10.dp))
                    .padding(6.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Step 1: Table Select
                Row(Modifier.fillMaxWidth()) {
                    listOf("RT-01", "RT-02", "RT-03", "RT-04").forEach { table ->
                        Button(
                            onClick = { vm.selectTable(table) },
                            colors = ButtonDefaults.buttonColors(
                                if (vm.selectedTable == table)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp),
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text(table, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Step 2: Selected Table ka Common Layout
                val persons = vm.orders.filter { it.tableNo == vm.selectedTable }

                CommonTable4Persons(persons) { id, rect ->
                    vm.orderDropAreas[id] = rect
                }
                val grandTotal =
                    persons.filter { vm.selectedTable == it.tableNo }.sumOf { it.total }
                Text("Total: ₹$grandTotal", style = MaterialTheme.typography.bodyMedium)
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
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(8.dp)
                    .onGloballyPositioned { coords ->
                        rightPanelOffset = coords.positionInWindow()
                    }
            ) {
                Text("Items", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    availableItems.chunked(2).forEach { rowItems ->
                        Row(Modifier.fillMaxWidth()) {
                            rowItems.forEach { item ->

                                var itemGlobalOffset by remember { mutableStateOf(Offset.Zero) }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(6.dp)
                                        .onGloballyPositioned { coords ->
                                            itemGlobalOffset = coords.positionInWindow()
                                        }
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .pointerInput(item) {
                                            awaitEachGesture {
                                                val down = awaitFirstDown()
                                                dragOffset = Offset.Zero
                                                dragStartPos = down.position
                                                dragItemGlobalPos = itemGlobalOffset
                                                draggedItem = null
                                                val pointer = down.id
                                                var hasDragged = false

                                                while (true) {
                                                    val event = awaitPointerEvent()
                                                    val change =
                                                        event.changes.firstOrNull { it.id == pointer }
                                                            ?: break

                                                    if (change.pressed) {
                                                        val delta = change.positionChange()
                                                        if (delta != Offset.Zero) {
                                                            hasDragged = true
                                                            draggedItem = item
                                                        }
                                                        dragOffset += delta
                                                        if (delta != Offset.Zero) change.consume()
                                                    } else break
                                                }

                                                val dropX =
                                                    itemGlobalOffset.x + down.position.x + dragOffset.x
                                                val dropY =
                                                    itemGlobalOffset.y + down.position.y + dragOffset.y

                                                if (hasDragged) {
                                                    var matchedId: String? = null
                                                    vm.orderDropAreas.forEach { (id, rect) ->
                                                        if (dropX in rect.left..rect.right && dropY in rect.top..rect.bottom) {
                                                            matchedId = id
                                                        }
                                                    }

                                                    if (matchedId != null) {
                                                        vm.addItemTo(matchedId!!, item)
                                                        Toast.makeText(
                                                            context,
                                                            "Added to ${matchedId!!}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "No seat found at drop location. Available: ${vm.orderDropAreas.keys.joinToString()}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                    OrderStorage.save(context, vm.orders)
                                                }
                                                dragOffset = Offset.Zero
                                                draggedItem = null
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.alpha(if (draggedItem == item) 0.3f else 1f)
                                    ) {
                                        Text(
                                            text = item.imageUrl,
                                            fontSize = 28.sp,
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Text(item.name, fontSize = 10.sp)
                                        Text("₹${item.price}", fontSize = 9.sp)
                                    }
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

        // Floating dragged item overlay
        if (draggedItem != null) {
            Box(
                modifier = Modifier
                    .offset {
                        val floatX = dragItemGlobalPos.x + dragStartPos.x + dragOffset.x - 50
                        val floatY = dragItemGlobalPos.y + dragStartPos.y + dragOffset.y - 50
                        IntOffset(floatX.toInt(), floatY.toInt())
                    }
                    .size(100.dp)
                    .graphicsLayer {
                        shadowElevation = 16f
                        scaleX = 1.1f
                        scaleY = 1.1f
                    }
                    .zIndex(1000f)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(10.dp)
                    )
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = draggedItem!!.imageUrl,
                        fontSize = 32.sp,
                        modifier = Modifier.size(45.dp)
                    )
                    Text(draggedItem!!.name)
                    Text("₹${draggedItem!!.price}")
                }
            }
        }

        // Create Seat Button
        Button(
            onClick = {
                if (vm.selectedTable != null) {
                    vm.showPopup = true
                } else {
                    Toast.makeText(context, "Select Table First", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text("Create Seat")
        }

        // Payment Button
        val grandTotal = vm.orders.filter { vm.selectedTable == it.tableNo }.sumOf { it.total }
        Button(
            onClick = {
                if (grandTotal > 0) {
                    vm.showPayPopup = true
                } else {
                    Toast.makeText(context, "Add items to table first", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("Pay ₹$grandTotal")
        }

        // Step 3: Popup
        if (vm.showPopup) {
            CreateOrderPopup(vm)
        }

        // Payment Popup
        if (vm.showPayPopup) {
            PayPopup(vm, grandTotal)
        }
    }
}
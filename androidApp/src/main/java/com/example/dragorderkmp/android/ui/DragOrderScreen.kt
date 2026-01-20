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
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dragorderkmp.OrderItem
import com.example.dragorderkmp.android.viewmodel.OrderViewModel
import coil.compose.AsyncImage


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
        OrderItem("Pizza",120,"https://pngimg.com/uploads/pizza/pizza_PNG44077.png"),
        OrderItem("Burger",80,"https://pngimg.com/uploads/burger_sandwich/burger_sandwich_PNG4135.png"),
        OrderItem("Samosa",20,"https://i.pinimg.com/736x/8c/d2/4f/8cd24f5121c62e3241dd66d5c52aa476.jpg"),
        OrderItem("Cake",100,"https://pngimg.com/uploads/cake/cake_PNG9692.png"),
        OrderItem("Tea",80,"https://pngimg.com/uploads/tea/tea_PNG16957.png"),
        OrderItem("Coffee",80,"https://pngimg.com/uploads/coffee/coffee_PNG17374.png"),
        OrderItem("Milkshake",80,"https://pngimg.com/uploads/milkshake/milkshake_PNG70.png"),
        OrderItem("Ice Cream",80,"https://pngimg.com/uploads/ice_cream/ice_cream_PNG20992.png"),
        OrderItem("Donut",80,"https://pngimg.com/uploads/donut/donut_PNG52.png"),
        OrderItem("Burrito",80,"https://pngimg.com/uploads/burrito/burrito_PNG35.png"),
        OrderItem("Taco",80,"https://pngimg.com/uploads/taco/taco_PNG28.png"),
        OrderItem("Chicken Nuggets",80,"https://pngimg.com/uploads/nuggets/nuggets_PNG43.png"),
        OrderItem("Sandwich",80,"https://pngimg.com/uploads/sandwich/sandwich_PNG59.png"),
        OrderItem("Hot Dog",80,"https://pngimg.com/uploads/hot_dog/hot_dog_PNG27.png"),
    )

    var rightPanelOffset by remember { mutableStateOf(Offset.Zero) }
    Box(Modifier.fillMaxSize().background(Color(0xFFEFEFEF))) {

        Row(Modifier.fillMaxSize().padding(12.dp)) {

            // LEFT PANEL
            Column(Modifier.weight(1f).padding(start = 6.dp, end = 6.dp)) {
                OrderList(vm, navController)   // nav pass
                Spacer(Modifier.height(8.dp))
//                BillSection(vm)
            }

            Spacer(
                Modifier
                    .fillMaxHeight()     // poori height lega
                    .width(1.dp)         // line ki thickness
                    .background(Color(0xFF383734))
            )

            // RIGHT PANEL (ITEMS)
            Column(
                Modifier
                    .weight(1.5f)
                    .padding(start = 6.dp, end = 6.dp)
                    .onGloballyPositioned { coords ->
                        val pos = coords.positionInWindow()
                        rightPanelOffset = Offset(pos.x, pos.y)
                    }
            ) {
                Text("Items", style = MaterialTheme.typography.titleMedium)
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
                                    .offset {
                                        IntOffset(offset.x.toInt(), offset.y.toInt())
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
                                            offset = Offset.Zero
                                            val pointer = down.id
                                            var hasDragged = false

                                            while (true) {
                                                val event = awaitPointerEvent()
                                                val change =
                                                    event.changes.firstOrNull { it.id == pointer }
                                                        ?: break

                                                if (change.pressed) {
                                                    val delta = change.positionChange()
                                                    if (delta != Offset.Zero) hasDragged = true
                                                    offset += delta
                                                    change.consumePositionChange()
                                                } else break
                                            }

                                            val dropX =
                                                itemGlobalOffset.x + down.position.x + offset.x
                                            val dropY =
                                                itemGlobalOffset.y + down.position.y + offset.y

                                            if (hasDragged) {
                                                // try matching against any known order drop areas
                                                var matchedOrderId: String? = null
                                                vm.orderDropAreas.forEach { (id, rect) ->
                                                    if (dropX >= rect.left && dropX <= rect.right && dropY >= rect.top && dropY <= rect.bottom) {
                                                        matchedOrderId = id
                                                    }
                                                }

                                                if (matchedOrderId != null) {
                                                    vm.addItemTo(matchedOrderId!!, item)
                                                } else {
                                                    // Build debug message: drop coords + known rects
                                                    val sb = StringBuilder()
                                                    sb.append("drop=(%.1f,%.1f) ".format(dropX, dropY))
                                                    sb.append("orders:")
                                                    vm.orderDropAreas.forEach { (id, rect) ->
                                                        sb.append(" $id=[%.0f,%.0f,%.0f,%.0f]".format(rect.left, rect.top, rect.right, rect.bottom))
                                                    }
                                                    val msg = sb.toString()
                                                    Toast.makeText(context, "item not added to cart: $msg", Toast.LENGTH_LONG).apply {
                                                        setGravity(Gravity.CENTER, 0, -200)
                                                        show()
                                                    }
                                                }
                                            }

                                            offset = Offset.Zero
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    AsyncImage(
                                        model = item.imageUrl,
                                        contentDescription = item.name,
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

        // Popup
        if (vm.showPopup) {
            CreateOrderPopup(vm)
        }

        // Floating Add Button
        FloatingActionButton(
            onClick = { vm.showPopup = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Order")
        }
    }
}

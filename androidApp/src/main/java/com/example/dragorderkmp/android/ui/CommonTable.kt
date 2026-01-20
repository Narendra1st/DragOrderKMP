
package com.example.dragorderkmp.android.ui
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dragorderkmp.Order   // ya Order ko Person jaisa use kar rahe ho

@Composable
fun CommonTable4Persons(
    persons: List<Order>,
    onPosition: (String, Rect) -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(8.dp).verticalScroll(rememberScrollState())) {

        Row {
            PersonCard(persons.getOrNull(0), onPosition, Modifier.weight(0.1f))
            PersonCard(persons.getOrNull(1), onPosition, Modifier.weight(0.1f))
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(6.dp)
                .background(Color(0xFF6A4A4A), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Common Order", color = Color.White)
        }

        Row {
            PersonCard(persons.getOrNull(2), onPosition, Modifier.weight(0.1f))
            PersonCard(persons.getOrNull(3), onPosition, Modifier.weight(0.1f))
        }
    }
}


@Composable
fun PersonCard(
    order: Order?,
    onPosition: (String, Rect) -> Unit,
    modifier: Modifier
) {
    if (order == null) {
        Box(
            modifier
                .height(100.dp)
                .padding(6.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
        )
        return
    }

    Box(
        modifier
            .padding(6.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
            .onGloballyPositioned { coords ->
                val pos = coords.positionInWindow()
                val size = coords.size
                onPosition(
                    order.id,
                    Rect(pos.x, pos.y, pos.x + size.width, pos.y + size.height)
                )
            }
            .padding(8.dp)
    ) {
        Column {
            Text("ID: ${order.id}")
            Text("Name: ${order.name}")
            Text("Table: ${order.tableNo}")
            Text("Qty: ${order.qty}")
        }
    }
}

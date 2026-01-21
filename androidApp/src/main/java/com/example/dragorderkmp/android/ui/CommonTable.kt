
package com.example.dragorderkmp.android.ui
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dragorderkmp.Order   // ya Order ko Person jaisa use kar rahe ho

@Composable
fun CommonTable4Persons(
    persons: List<Order>,
    modifier: Modifier = Modifier,
    onPosition: (String, Rect) -> Unit
) {
    Column(modifier.fillMaxWidth().padding(8.dp)) {

        Row {
            PersonCard(persons.getOrNull(0), onPosition, Modifier.weight(0.1f))
            PersonCard(persons.getOrNull(1), onPosition, Modifier.weight(0.1f))
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
                .height(140.dp)
                .padding(8.dp)
                .border(0.dp, Color.Gray, RoundedCornerShape(10.dp))
        )
        return
    }

    Box(
        modifier
            .padding(8.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .border(0.dp, Color.Black, RoundedCornerShape(10.dp))
            .onGloballyPositioned { coords ->
                val pos = coords.positionInWindow()
                val size = coords.size
                onPosition(
                    order.id,
                    Rect(pos.x, pos.y, pos.x + size.width, pos.y + size.height)
                )
            }
            .padding(12.dp)
    ) {
        Column {

            // Person Info
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("ID: ${order.id}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("Name: ${order.name}", fontSize = 10.sp)
                Text("Table: ${order.tableNo}", fontSize = 10.sp)
                Text("Qty: ${order.qty}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(10.dp))

            // Items Row
            if (order.items.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    order.items.forEach { item ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(50.dp)
                        ) {
                            Text(text = item.imageUrl, fontSize = 20.sp)
                            Text(item.name, fontSize = 10.sp, maxLines = 1)
                            Text("x${item.qty}", fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }

}

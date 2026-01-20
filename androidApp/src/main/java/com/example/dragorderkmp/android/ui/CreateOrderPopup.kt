package com.example.dragorderkmp.android.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.dragorderkmp.android.viewmodel.OrderViewModel

@Composable
fun CreateOrderPopup(vm: OrderViewModel) {
    val table = vm.selectedTable
    val count = vm.countInTable(table)

    AlertDialog(
        onDismissRequest = { vm.showPopup = false },
        title = { Text("Create Seat (Max 4)") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Table: $table")

                TextField(
                    value = vm.name,
                    onValueChange = { vm.name = it },
                    label = { Text("Person / Seat Name") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            vm.createSeatForSelectedTable()
                        }
                    )
                )

                if (count >= 4) {
                    Text(
                        "This table already has 4 seats",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = table != null && count < 4,
                onClick = { vm.createSeatForSelectedTable() }
            ) {
                Text("Create")
            }
        }
    )
}

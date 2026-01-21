package com.example.dragorderkmp.android.payment

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dragorderkmp.android.viewmodel.OrderViewModel

@Composable
fun PayPopup(vm: OrderViewModel, totalAmount: Int) {
    AlertDialog(
        onDismissRequest = { vm.showPayPopup = false },
        title = { Text("Select Payment Method") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Total Amount: ₹$totalAmount", fontWeight = FontWeight.Bold)

                Button(
                    onClick = {
                        vm.showPayPopup = false
                        Toast.makeText(
                            vm.context,
                            "Paid ₹$totalAmount via UPI",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pay with UPI")
                }

                Button(
                    onClick = {
                        vm.showPayPopup = false
                        Toast.makeText(
                            vm.context,
                            "Paid ₹$totalAmount via Bank",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pay with Bank")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = { vm.showPayPopup = false }) {
                Text("Cancel")
            }
        }
    )
}

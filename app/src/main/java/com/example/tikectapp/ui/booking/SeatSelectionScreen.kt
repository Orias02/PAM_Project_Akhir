package com.example.ticketapp.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    onBackClick: () -> Unit,
    onContinueClick: (List<String>) -> Unit
) {

    val seatPrice = 46000

    val selectedSeats = remember {
        mutableStateListOf<String>()
    }

    var showSuccessDialog by remember {
        mutableStateOf(false)
    }

    val unavailableSeats = listOf(
        "C12",
        "C11",
        "C9",
        "C8",
        "E12",
        "E11"
    )

    val rows = listOf(
        "A","B","C","D","E",
        "F","G","H","J","K","L"
    )

    val seats = rows.flatMap { row ->
        (7..16).map { number ->
            "$row$number"
        }
    }

    val totalPrice = selectedSeats.size * seatPrice

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopAppBar(
            title = {
                Text("Pilih Kursi")
            }
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(seats) { seat ->

                val isSelected =
                    selectedSeats.contains(seat)

                val isUnavailable =
                    unavailableSeats.contains(seat)

                val color = when {
                    isSelected -> Color(0xFF1EA7FF)
                    isUnavailable -> Color.LightGray
                    else -> Color(0xFF11295C)
                }

                Box(
                    modifier = Modifier
                        .size(
                            width = 60.dp,
                            height = 40.dp
                        )
                        .background(
                            color,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable(
                            enabled = !isUnavailable
                        ) {

                            if (isSelected) {
                                selectedSeats.remove(seat)
                            } else {
                                selectedSeats.add(seat)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = seat,
                        color = Color.White
                    )
                }
            }
        }

        Divider()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text = "Tempat Duduk",
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = if (selectedSeats.isEmpty())
                    "-"
                else
                    selectedSeats.joinToString(", ")
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Total Harga",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Rp$totalPrice"
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = {
                    showSuccessDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedSeats.isNotEmpty()
            ) {
                Text("Lanjut Pembayaran")
            }
        }
    }

    if (showSuccessDialog) {

        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
            },
            title = {
                Text("Pembayaran Berhasil")
            },
            text = {
                Text(
                    "Terima kasih telah melakukan pemesanan tiket bioskop."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onContinueClick(selectedSeats)
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
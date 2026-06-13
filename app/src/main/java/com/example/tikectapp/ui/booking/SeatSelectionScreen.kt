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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ticketapp.data.model.Seat
import com.example.ticketapp.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    movieTitle: String,
    selectedDate: String,
    selectedTime: String,
    selectedLocation: String,
    onBackClick: () -> Unit,
    onContinueClick: (List<String>) -> Unit
) {
    val seatPrice = 46000
    val coroutineScope = rememberCoroutineScope()

    val selectedSeats = remember { mutableStateListOf<String>() }
    var showSuccessDialog by remember { mutableStateOf(false) }
    val unavailableSeats = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        try {
            var fetchedSeats = SupabaseClientProvider.client.postgrest["seats"]
                .select {
                    filter {
                        ilike("movie_title", movieTitle.trim())
                        eq("show_date", selectedDate.trim())
                        eq("show_time", selectedTime.trim())
                        ilike("cinema_location", selectedLocation.trim())
                    }
                }
                .decodeList<Seat>()

            // JIKA DATABASE KOSONG, GENERATE MASTER KURSI BARU
            if (fetchedSeats.isEmpty()) {
                println("SUPABASE_LOG: Mengisi 110 kursi master baru ke database...")
                val rows = listOf("A","B","C","D","E","F","G","H","J","K","L")
                val newSeatsMaster = rows.flatMap { row ->
                    (7..16).map { num ->
                        Seat(
                            seat_number = "$row$num",
                            is_booked = false,
                            movie_title = movieTitle.trim(),
                            show_date = selectedDate.trim(),
                            show_time = selectedTime.trim(),
                            cinema_location = selectedLocation.trim()
                        )
                    }
                }

                SupabaseClientProvider.client.postgrest["seats"].insert(newSeatsMaster)

                // Ambil kembali data segar dari database setelah insert berhasil
                fetchedSeats = SupabaseClientProvider.client.postgrest["seats"]
                    .select {
                        filter {
                            ilike("movie_title", movieTitle.trim())
                            eq("show_date", selectedDate.trim())
                            eq("show_time", selectedTime.trim())
                            ilike("cinema_location", selectedLocation.trim())
                        }
                    }
                    .decodeList<Seat>()
            }

            val bookedSeatNumbers = fetchedSeats
                .filter { it.is_booked }
                .map { it.seat_number.uppercase().trim() }

            unavailableSeats.clear()
            unavailableSeats.addAll(bookedSeatNumbers)

            println("SUPABASE_FILTER_SUCCESS: Berhasil memuat ${fetchedSeats.size} data kursi. Kursi terkunci: $unavailableSeats")
        } catch (e: Exception) {
            println("SUPABASE_FILTER_ERROR: Gagal memuat jadwal. Pesan: ${e.message}")
            e.printStackTrace()
        }
    }

    val rows = listOf("A","B","C","D","E","F","G","H","J","K","L")
    val seats = rows.flatMap { row ->
        (7..16).map { number -> "$row$number" }
    }

    val totalPrice = selectedSeats.size * seatPrice

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Pilih Kursi") })

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = movieTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(text = "$selectedLocation | $selectedDate | Jam $selectedTime", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(seats) { seat ->
                val isSelected = selectedSeats.contains(seat)
                val isUnavailable = unavailableSeats.contains(seat.uppercase().trim())

                val color = when {
                    isSelected -> Color(0xFF1EA7FF)
                    isUnavailable -> Color.LightGray
                    else -> Color(0xFF11295C)
                }

                Box(
                    modifier = Modifier
                        .size(width = 60.dp, height = 40.dp)
                        .background(color, RoundedCornerShape(8.dp))
                        .clickable(enabled = !isUnavailable) {
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
                        color = if (isUnavailable) Color.DarkGray else Color.White
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
            Text(text = "Tempat Duduk Terpilih", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = if (selectedSeats.isEmpty()) "-" else selectedSeats.joinToString(", "))

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Total Harga", fontWeight = FontWeight.Bold)
            Text(text = "Rp$totalPrice")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showSuccessDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedSeats.isNotEmpty()
            ) {
                Text("Lanjut Pembayaran")
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Pembayaran Berhasil") },
            text = { Text("Terima kasih telah memesan tiket bioskop untuk film $movieTitle.") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                // Mengambil data teks mentah biasa dari state list compose
                                val seatsToBook = selectedSeats.map { it.trim() }

                                // Loop sekuensial yang andal menunggu transaksi server selesai satu-satu
                                for (seatName in seatsToBook) {
                                    SupabaseClientProvider.client.postgrest["seats"].update(
                                        update = { set("is_booked", true) }
                                    ) {
                                        filter {
                                            eq("seat_number", seatName)
                                            ilike("movie_title", movieTitle.trim())
                                            eq("show_date", selectedDate.trim())
                                            eq("show_time", selectedTime.trim())
                                            ilike("cinema_location", selectedLocation.trim())
                                        }
                                    }
                                }

                                println("SUPABASE_UPDATE_SUCCESS: Sukses merubah status $seatsToBook di Supabase.")
                                showSuccessDialog = false
                                onContinueClick(selectedSeats)
                            } catch (e: Exception) {
                                println("SUPABASE_UPDATE_ERROR: Gagal sinkronisasi data. Pesan: ${e.message}")
                                e.printStackTrace()
                                showSuccessDialog = false
                                onContinueClick(selectedSeats)
                            }
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
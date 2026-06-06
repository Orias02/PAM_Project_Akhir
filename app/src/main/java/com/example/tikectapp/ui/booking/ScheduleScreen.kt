package com.example.tikectapp.ui.booking


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScheduleScreen(
    onBackClick: () -> Unit,
    onBookingClick: () -> Unit
) {

    val dates = listOf(
        "5 Jun",
        "6 Jun",
        "7 Jun",
        "8 Jun"
    )

    val times = listOf(
        "10:00",
        "13:00",
        "16:00",
        "19:00"
    )

    val locations = listOf(
        "Malang Town Square",
        "Cyber Mall",
        "Dieng Plaza"
    )

    var selectedDate by remember {
        mutableStateOf("")
    }

    var selectedTime by remember {
        mutableStateOf("")
    }

    var selectedLocation by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            text = "🎬 Pilih Jadwal Film",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Pilih tanggal, jam tayang, dan lokasi bioskop",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // TANGGAL
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "📅 Pilih Tanggal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    dates.forEach { date ->

                        FilterChip(
                            selected = selectedDate == date,
                            onClick = {
                                selectedDate = date
                            },
                            label = {
                                Text(date)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // JAM TAYANG
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "🕒 Jam Tayang",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    times.forEach { time ->

                        FilterChip(
                            selected = selectedTime == time,
                            onClick = {
                                selectedTime = time
                            },
                            label = {
                                Text(time)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LOKASI
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "📍 Lokasi Bioskop",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                locations.forEach { location ->

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        RadioButton(
                            selected = selectedLocation == location,
                            onClick = {
                                selectedLocation = location
                            }
                        )

                        Text(
                            text = location,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // RINGKASAN
        if (
            selectedDate.isNotEmpty() &&
            selectedTime.isNotEmpty() &&
            selectedLocation.isNotEmpty()
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "🎟️ Ringkasan Booking",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Tanggal : $selectedDate")
                    Text("Jam : $selectedTime")
                    Text("Lokasi : $selectedLocation")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                onBookingClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            enabled =
                selectedDate.isNotEmpty() &&
                        selectedTime.isNotEmpty() &&
                        selectedLocation.isNotEmpty()
        ) {

            Text(
                text = "BOOKING NOW 🎟️"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
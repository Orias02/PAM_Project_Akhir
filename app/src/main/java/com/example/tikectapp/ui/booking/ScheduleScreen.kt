package com.example.tikectapp.ui.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ScheduleScreen(

    // lokasi yang tersedia untuk film ini
    availableLocations: List<String>,

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

    // semua lokasi bioskop
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
            .padding(16.dp)
    ) {

        Text(
            text = "Pilih Jadwal",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        // =========================
        // TANGGAL
        // =========================

        Text("Tanggal")

        dates.forEach { date ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedDate == date,
                        onClick = {
                            selectedDate = date
                        }
                    )
                    .padding(vertical = 4.dp)
            ) {

                RadioButton(
                    selected = selectedDate == date,
                    onClick = {
                        selectedDate = date
                    }
                )

                Text(date)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // =========================
        // JAM TAYANG
        // =========================

        Text("Jam Tayang")

        times.forEach { time ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedTime == time,
                        onClick = {
                            selectedTime = time
                        }
                    )
                    .padding(vertical = 4.dp)
            ) {

                RadioButton(
                    selected = selectedTime == time,
                    onClick = {
                        selectedTime = time
                    }
                )

                Text(time)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // =========================
        // LOKASI
        // =========================

        Text("Lokasi")

        locations.forEach { location ->

            // cek apakah lokasi tersedia
            val isAvailable =
                availableLocations.contains(location)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(

                        selected = selectedLocation == location,

                        enabled = isAvailable,

                        onClick = {

                            if (isAvailable) {

                                selectedLocation = location
                            }
                        }
                    )
                    .padding(vertical = 4.dp)
            ) {

                RadioButton(

                    selected = selectedLocation == location,

                    enabled = isAvailable,

                    onClick = {

                        if (isAvailable) {

                            selectedLocation = location
                        }
                    }
                )

                Text(

                    text = location,

                    // abu-abu kalau tidak tersedia
                    color =
                        if (isAvailable)
                            Color.Black
                        else
                            Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // =========================
        // BUTTON BOOKING
        // =========================

        Button(
            onClick = {
                onBookingClick()
            },

            modifier = Modifier.fillMaxWidth(),

            enabled =
                selectedDate.isNotEmpty() &&
                        selectedTime.isNotEmpty() &&
                        selectedLocation.isNotEmpty()
        ) {

            Text("BOOKING NOW")
        }
    }
}
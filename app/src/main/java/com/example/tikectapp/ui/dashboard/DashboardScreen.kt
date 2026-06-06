package com.example.ticketapp.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.ticketapp.data.model.CinemaLocation
import com.example.ticketapp.data.model.Movie
import com.example.ticketapp.ui.theme.NavyDark
import com.example.ticketapp.ui.theme.SageGreenDark
import com.example.ticketapp.ui.theme.SageGreenLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onMovieClick: (String) -> Unit,
    onAddMovieClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // tambah tombol semua
    val locations = listOf("Semua") +
            CinemaLocation.allLocations

    Scaffold(

        floatingActionButton = {

            if (uiState.isAdmin) {

                Column(
                    modifier = Modifier.padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.End
                ) {

                    FloatingActionButton(
                        onClick = onAddMovieClick
                    ) {

                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Movie"
                        )
                    }
                }
            }
        }

    ) { padding ->

        if (uiState.isLoading) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator()
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),

                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    DashboardHeader(
                        onLogoutClick = onLogoutClick
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    // =========================
                    // LOKASI HORIZONTAL
                    // =========================

                    Text(
                        text = "Lokasi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        items(locations) { location ->

                            val isSelected =
                                uiState.selectedLocation == location

                            FilterChip(

                                selected = isSelected,

                                onClick = {

                                    viewModel.changeLocation(location)
                                },

                                label = {

                                    Text(location)
                                }
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }

                // =========================
                // MOVIE LIST
                // =========================

                items(uiState.movies) { movie ->

                    MovieCard(
                        movie = movie,

                        onClick = {
                            onMovieClick(movie.id)
                        }
                    )
                }

                item {

                    Spacer(
                        modifier = Modifier.height(100.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    onLogoutClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically,

        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SageGreenLight),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    tint = SageGreenDark
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {

                Text(
                    text = "TicketApp",
                    style = MaterialTheme.typography.titleLarge,
                    color = NavyDark,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Now Showing",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        IconButton(
            onClick = onLogoutClick
        ) {

            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout"
            )
        }
    }
}

@Composable
private fun MovieCard(
    movie: Movie,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },

        shape = RoundedCornerShape(24.dp),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )

    ) {

        Column {

            Image(
                painter = rememberAsyncImagePainter(movie.imageUrl),
                contentDescription = movie.title,

                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),

                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = movie.genre,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NavyDark.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "⭐ ${movie.rating}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NavyDark
                )
            }
        }
    }
}
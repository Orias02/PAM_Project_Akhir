package com.example.ticketapp.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.ticketapp.data.model.Movie
import com.example.ticketapp.data.repository.AuthRepository
import com.example.ticketapp.data.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material3.ExperimentalMaterial3Api
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: String,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {

    val repository = remember { MovieRepository() }
    val authRepository = remember { AuthRepository() }

    var movie by remember {
        mutableStateOf<Movie?>(null)
    }

    var isAdmin by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {

        val result = repository.getMovieById(movieId)

        result.onSuccess {
            movie = it
        }

        val currentUser = authRepository.getCurrentUser()

        isAdmin = currentUser?.role == "admin"

        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(movie?.title ?: "Movie Detail")
                },

                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                actions = {

                    if (isAdmin) {

                        IconButton(
                            onClick = onEditClick
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Movie"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->

        if (isLoading) {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }

        } else {

            movie?.let {

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),

                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    Image(
                        painter = rememberAsyncImagePainter(it.imageUrl),
                        contentDescription = it.title,

                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),

                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = it.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(text = "Genre: ${it.genre}")
                    Text(text = "Duration: ${it.duration} minutes")
                    Text(text = "Price: Rp ${it.price}")
                    Text(text = "Rating: ⭐ ${it.rating}")

                    Text(
                        text = it.description,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Booking")
                    }

                    if (isAdmin) {

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = onEditClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text("Edit Movie")
                        }
                    }
                }
            }
        }
    }
}
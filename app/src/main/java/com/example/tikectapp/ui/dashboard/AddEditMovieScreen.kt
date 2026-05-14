package com.example.ticketapp.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ticketapp.data.model.Movie
import com.example.ticketapp.data.repository.MovieRepository
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMovieScreen(
    movieId: String? = null,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {

    val repository = remember { MovieRepository() }
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    val isEditMode = movieId != null

    Scaffold(
        topBar = {
            TopAppBar(

                title = {
                    Text(
                        if (isEditMode)
                            "Edit Movie"
                        else
                            "Add Movie"
                    )
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
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Genre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {

                    scope.launch {

                        val movie = Movie(
                            id = movieId ?: "",
                            title = title,
                            description = description,
                            genre = genre,
                            imageUrl = imageUrl,
                            rating = 4.5f,
                            duration = 120,
                            price = 50000.0
                        )

                        if (isEditMode) {
                            repository.updateMovie(movie)
                        } else {
                            repository.addMovie(movie)
                        }

                        onSaveSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (isEditMode)
                        "Update Movie"
                    else
                        "Save Movie"
                )
            }
        }
    }
}
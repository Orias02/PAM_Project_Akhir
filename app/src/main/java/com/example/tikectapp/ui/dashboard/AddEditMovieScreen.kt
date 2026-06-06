package com.example.ticketapp.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.ticketapp.ui.theme.*
import com.example.tikectapp.ui.dashboard.AddEditMovieViewModel

// ── Genre options ─────────────────────────────────────────────────────────────

private val genreOptions = listOf(
    "Action", "Adventure", "Animation", "Comedy", "Crime",
    "Documentary", "Drama", "Fantasy", "Horror", "Mystery",
    "Romance", "Sci-Fi", "Thriller"
)

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMovieScreen(
    movieId: String? = null,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AddEditMovieViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isEditMode = movieId != null

    // Load data jika mode edit
    LaunchedEffect(movieId) {
        if (movieId != null) {
            viewModel.loadMovie(movieId)
        }
    }

    // Navigasi setelah save/delete berhasil
    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) onSaveSuccess()
    }
    LaunchedEffect(uiState.isDeleteSuccess) {
        if (uiState.isDeleteSuccess) onSaveSuccess()
    }

    // State untuk dialog konfirmasi hapus
    var showDeleteDialog by remember { mutableStateOf(false) }
    // State untuk dropdown genre
    var genreDropdownExpanded by remember { mutableStateOf(false) }
    var movieSchedules by remember {
        mutableStateOf("")
    }
    OutlinedTextField(
        value = movieSchedules,
        onValueChange = {
            movieSchedules = it
        },
        label = {
            Text("Jadwal Film")
        }
    )

    // ── Delete Confirmation Dialog ────────────────────────────────────────────
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Hapus Film?",
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            },
            text = {
                Text(
                    text = "Film \"${uiState.title}\" akan dihapus secara permanen dari database. Tindakan ini tidak dapat dibatalkan.",
                    color = DarkGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteMovie()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }

        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Film" else "Tambah Film",
                        fontWeight = FontWeight.Bold,
                        color = NavyDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = NavyDark
                        )
                    }
                },
                actions = {
                    // Tombol hapus hanya di mode edit
                    if (isEditMode) {
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            enabled = !uiState.isDeleting
                        ) {
                            if (uiState.isDeleting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = ErrorRed
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus Film",
                                    tint = ErrorRed
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SageGreenDark)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF8F9FA))
        ) {

            // ── Error Banner ──────────────────────────────────────────────────
            AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.errorMessage?.let { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = ErrorRed.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = ErrorRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearError() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, null, tint = ErrorRed)
                            }
                        }
                    }
                }
            }

            // ── Preview Gambar ────────────────────────────────────────────────
            ImagePreviewSection(imageUrl = uiState.imageUrl)

            // ── Form Card ─────────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Judul section
                    FormSectionTitle(title = "Informasi Film")

                    // ── Title ─────────────────────────────────────────────────
                    FormTextField(
                        value = uiState.title,
                        onValueChange = viewModel::onTitleChange,
                        label = "Judul Film",
                        placeholder = "contoh: Avengers: Endgame",
                        leadingIcon = Icons.Default.Movie,
                        errorMessage = uiState.titleError,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        )
                    )

                    // ── Genre Dropdown ────────────────────────────────────────
                    Column {
                        Text(
                            text = "Genre",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (uiState.genreError != null) ErrorRed else DarkGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ExposedDropdownMenuBox(
                            expanded = genreDropdownExpanded,
                            onExpandedChange = { genreDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = uiState.genre,
                                onValueChange = viewModel::onGenreChange,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                placeholder = { Text("Pilih atau ketik genre") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Category,
                                        contentDescription = null,
                                        tint = if (uiState.genreError != null) ErrorRed else MediumGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = genreDropdownExpanded
                                    )
                                },
                                isError = uiState.genreError != null,
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SageGreenDark,
                                    unfocusedBorderColor = LightGray,
                                    errorBorderColor = ErrorRed,
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = genreDropdownExpanded,
                                onDismissRequest = { genreDropdownExpanded = false }
                            ) {
                                genreOptions.forEach { genre ->
                                    DropdownMenuItem(
                                        text = { Text(genre) },
                                        onClick = {
                                            viewModel.onGenreChange(genre)
                                            genreDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        if (uiState.genreError != null) {
                            Text(
                                text = uiState.genreError!!,
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }

                    // ── Row: Rating + Price ───────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Rating
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Rating (0-10)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (uiState.ratingError != null) ErrorRed else DarkGray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = uiState.rating,
                                onValueChange = viewModel::onRatingChange,
                                placeholder = { Text("8.5") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (uiState.ratingError != null) ErrorRed else StarYellow,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                isError = uiState.ratingError != null,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Next
                                ),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SageGreenDark,
                                    unfocusedBorderColor = LightGray,
                                    errorBorderColor = ErrorRed,
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (uiState.ratingError != null) {
                                Text(
                                    text = uiState.ratingError!!,
                                    color = ErrorRed,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                )
                            }
                        }

                        // Price
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Harga (Rp)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (uiState.priceError != null) ErrorRed else DarkGray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = uiState.price,
                                onValueChange = viewModel::onPriceChange,
                                placeholder = { Text("50000") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.LocalAtm,
                                        contentDescription = null,
                                        tint = if (uiState.priceError != null) ErrorRed else MediumGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                isError = uiState.priceError != null,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SageGreenDark,
                                    unfocusedBorderColor = LightGray,
                                    errorBorderColor = ErrorRed,
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (uiState.priceError != null) {
                                Text(
                                    text = uiState.priceError!!,
                                    color = ErrorRed,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                )
                            }
                        }
                    }

                    Divider(color = LightGray, thickness = 1.dp)
                    FormSectionTitle(title = "Media & Deskripsi")

                    // ── Image URL ─────────────────────────────────────────────
                    FormTextField(
                        value = uiState.imageUrl,
                        onValueChange = viewModel::onImageUrlChange,
                        label = "URL Gambar",
                        placeholder = "https://example.com/poster.jpg",
                        leadingIcon = Icons.Default.Image,
                        errorMessage = uiState.imageUrlError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Next
                        )
                    )

                    // ── Description ───────────────────────────────────────────
                    Column {
                        Text(
                            text = "Deskripsi",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (uiState.descriptionError != null) ErrorRed else DarkGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = uiState.description,
                            onValueChange = viewModel::onDescriptionChange,
                            placeholder = { Text("Tulis sinopsis singkat film...") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = null,
                                    tint = if (uiState.descriptionError != null) ErrorRed else MediumGray,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            isError = uiState.descriptionError != null,
                            minLines = 4,
                            maxLines = 6,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Default
                            ),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SageGreenDark,
                                unfocusedBorderColor = LightGray,
                                errorBorderColor = ErrorRed,
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (uiState.descriptionError != null) {
                            Text(
                                text = uiState.descriptionError!!,
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }

            // ── Tombol Save ───────────────────────────────────────────────────
            Button(
                onClick = { viewModel.saveMovie() },
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SageGreenDark,
                    contentColor = Color.White,
                    disabledContainerColor = SageGreenDark.copy(alpha = 0.6f)
                )
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isEditMode) "Mengupdate..." else "Menyimpan...",
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Icon(
                        imageVector = if (isEditMode) Icons.Default.Save else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditMode) "Update Film" else "Simpan Film",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Preview Gambar ────────────────────────────────────────────────────────────

@Composable
private fun ImagePreviewSection(imageUrl: String) {
    val isValidUrl = imageUrl.startsWith("http")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(SageGreenContainer),
        contentAlignment = Alignment.Center
    ) {
        if (isValidUrl) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Preview gambar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Overlay gelap tipis
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )
            // Label preview
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Text(
                    text = "Preview Poster",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ImageNotSupported,
                    contentDescription = null,
                    tint = SageGreenDark.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Preview poster akan muncul di sini",
                    style = MaterialTheme.typography.bodySmall,
                    color = SageGreenDark.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// ── Reusable Components ───────────────────────────────────────────────────────

@Composable
private fun FormSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = NavyDark,
        fontSize = 13.sp
    )
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = if (errorMessage != null) ErrorRed else DarkGray
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MediumGray) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (errorMessage != null) ErrorRed else MediumGray,
                    modifier = Modifier.size(20.dp)
                )
            },
            isError = errorMessage != null,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SageGreenDark,
                unfocusedBorderColor = LightGray,
                errorBorderColor = ErrorRed,
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}
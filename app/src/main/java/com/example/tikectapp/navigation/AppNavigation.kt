package com.example.ticketapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ticketapp.data.repository.AuthRepository
import com.example.ticketapp.ui.auth.AuthScreen
import com.example.ticketapp.ui.booking.SeatSelectionScreen
import com.example.ticketapp.ui.dashboard.AddEditMovieScreen
import com.example.ticketapp.ui.dashboard.DashboardScreen
import com.example.ticketapp.ui.dashboard.MovieDetailScreen
import com.example.tikectapp.ui.booking.ScheduleScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val authRepository = AuthRepository()

    NavHost(
        navController = navController,
        startDestination = "auth",
        modifier = modifier
    ) {
        // AUTH SCREEN
        composable("auth") {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        // DASHBOARD SCREEN
        composable("dashboard") {
            DashboardScreen(
                onMovieClick = { movieId ->
                    navController.navigate("detail/$movieId")
                },
                onAddMovieClick = {
                    navController.navigate("add_movie")
                },
                onLogoutClick = {
                    CoroutineScope(Dispatchers.Main).launch {
                        authRepository.signOut()
                        navController.navigate("auth") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    }
                }
            )
        }

        // DETAIL MOVIE SCREEN
        composable(
            route = "detail/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.StringType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""

            MovieDetailScreen(
                movieId = movieId,
                onBackClick = { navController.popBackStack() },
                onEditClick = { navController.navigate("edit_movie/$movieId") },
                // Menerima parameter title dari MovieDetailScreen untuk dioper ke Schedule
                onBookingClick = { movieTitle ->
                    navController.navigate("schedule/$movieTitle")
                }
            )
        }

        // ADD MOVIE SCREEN
        composable("add_movie") {
            AddEditMovieScreen(
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // EDIT MOVIE SCREEN
        composable(
            route = "edit_movie/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.StringType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")

            AddEditMovieScreen(
                movieId = movieId,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // SCHEDULE SCREEN
        composable(
            route = "schedule/{movieTitle}",
            arguments = listOf(navArgument("movieTitle") { type = NavType.StringType })
        ) { backStackEntry ->
            val movieTitle = backStackEntry.arguments?.getString("movieTitle") ?: "Default Movie"

            ScheduleScreen(
                movieTitle = movieTitle,
                onBackClick = { navController.popBackStack() },
                // Mengirim 3 data jadwal pilihan user ke screen pemilihan kursi
                onBookingClick = { date, time, location ->
                    navController.navigate("seat_selection/$movieTitle/$date/$time/$location")
                }
            )
        }

        // SEAT SELECTION SCREEN
        composable(
            route = "seat_selection/{movieTitle}/{selectedDate}/{selectedTime}/{selectedLocation}",
            arguments = listOf(
                navArgument("movieTitle") { type = NavType.StringType },
                navArgument("selectedDate") { type = NavType.StringType },
                navArgument("selectedTime") { type = NavType.StringType },
                navArgument("selectedLocation") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val movieTitle = backStackEntry.arguments?.getString("movieTitle") ?: ""
            val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""
            val selectedTime = backStackEntry.arguments?.getString("selectedTime") ?: ""
            val selectedLocation = backStackEntry.arguments?.getString("selectedLocation") ?: ""

            SeatSelectionScreen(
                movieTitle = movieTitle,
                selectedDate = selectedDate,
                selectedTime = selectedTime,
                selectedLocation = selectedLocation,
                onBackClick = { navController.popBackStack() },
                onContinueClick = { selectedSeats ->
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
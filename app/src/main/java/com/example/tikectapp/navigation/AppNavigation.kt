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

        // AUTH
        composable("auth") {

            AuthScreen(
                onLoginSuccess = {

                    navController.navigate("dashboard") {

                        popUpTo("auth") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // DASHBOARD
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

                            popUpTo("dashboard") {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }

        // DETAIL MOVIE
        composable(
            route = "detail/{movieId}",
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val movieId =
                backStackEntry.arguments?.getString("movieId") ?: ""

            MovieDetailScreen(

                movieId = movieId,

                onBackClick = {
                    navController.popBackStack()
                },

                onEditClick = {
                    navController.navigate("edit_movie/$movieId")
                },

                onBookingClick = {
                    navController.navigate("schedule")
                }
            )
        }

        // ADD MOVIE
        composable("add_movie") {

            AddEditMovieScreen(

                onBackClick = {
                    navController.popBackStack()
                },

                onSaveSuccess = {

                    navController.navigate("dashboard") {

                        popUpTo("dashboard") {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }

        // EDIT MOVIE
        composable(
            route = "edit_movie/{movieId}",
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val movieId =
                backStackEntry.arguments?.getString("movieId")

            AddEditMovieScreen(

                movieId = movieId,

                onBackClick = {
                    navController.popBackStack()
                },

                onSaveSuccess = {

                    navController.navigate("dashboard") {

                        popUpTo("dashboard") {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }

        // SCHEDULE SCREEN
        composable("schedule") {

            ScheduleScreen(

                onBackClick = {
                    navController.popBackStack()
                },

                onBookingClick = {

                    navController.navigate("seat_selection")
                }
            )
        }

        // SEAT SELECTION
        composable("seat_selection") {

            SeatSelectionScreen(

                onBackClick = {
                    navController.popBackStack()
                },

                onContinueClick = {

                    navController.navigate("dashboard") {

                        popUpTo("dashboard") {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
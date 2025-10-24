package com.example.myapplication.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.ui.navigation.Screen

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.MovieList.route) {
        composable(Screen.MovieList.route) {
            MovieListScreen { id ->
                navController.navigate(Screen.MovieDetail.createRoute(id))
            }
        }
        composable(
            Screen.MovieDetail.route,
            arguments = listOf(navArgument("movieId") { type = NavType.StringType })
        ) {
            val id = it.arguments?.getString("movieId")!!
            MovieDetailScreen(movieId = id) { navController.popBackStack() }
        }
    }
}

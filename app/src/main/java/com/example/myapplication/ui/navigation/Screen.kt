package com.example.myapplication.ui.navigation

sealed class Screen(val route: String) {
    object MovieList : Screen("movie_list")
    object MovieDetail : Screen("movie_detail/{movieId}") {
        fun createRoute(id: String) = "movie_detail/$id"
    }
}

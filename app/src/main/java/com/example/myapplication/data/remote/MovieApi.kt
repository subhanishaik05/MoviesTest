package com.example.myapplication.data.remote

import com.example.myapplication.data.model.Movie
import retrofit2.http.GET

interface MovieApi {
    @GET("movies")
    suspend fun getMovies(): List<Movie>
}

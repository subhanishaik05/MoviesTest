package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Movie
import com.example.myapplication.data.remote.MovieApi
import com.example.myapplication.data.room.MovieDao
import com.example.myapplication.data.room.MovieEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(private val api: MovieApi,
                                          private val dao: MovieDao
) {
    suspend fun getMovies(): Result<List<Movie>> {
        return try {
            val response = api.getMovies()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    fun getFavoritesFlow(): Flow<List<MovieEntity>> = dao.getAllFavoritesFlow()
    suspend fun addFavorite(movie: MovieEntity) = dao.insertFavorite(movie)
    suspend fun removeFavorite(movie: MovieEntity) = dao.deleteFavorite(movie)
    suspend fun isFavorite(id: String) = dao.isFavorite(id)

}

package com.example.myapplication.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Movie
import com.example.myapplication.data.repository.MovieRepository
import com.example.myapplication.data.room.MovieEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    fun loadFavoriteState(movieId: String) {
        viewModelScope.launch {
            _isFavorite.value = repository.isFavorite(movieId)
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            val entity = MovieEntity(movie.id, movie.title, movie.poster_url, movie.rating.imdb)
            if (_isFavorite.value) {
                repository.removeFavorite(entity)
                _isFavorite.value = false
            } else {
                repository.addFavorite(entity)
                _isFavorite.value = true
            }
        }
    }
}

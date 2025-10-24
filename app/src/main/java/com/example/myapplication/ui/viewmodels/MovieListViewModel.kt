package com.example.myapplication.ui.viewmodels


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Movie
import com.example.myapplication.data.model.Rating
import com.example.myapplication.data.repository.MovieRepository
import com.example.myapplication.data.room.MovieEntity
import com.example.myapplication.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortType { TITLE, RELEASE_DATE, RATING }
enum class MovieFilter { ALL, BOOKMARKED }

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val repository: MovieRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _allMovies = MutableStateFlow<List<Movie>>(emptyList())
    private val _favorites = MutableStateFlow<List<MovieEntity>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _sortType = MutableStateFlow(SortType.TITLE)
    private val _filter = MutableStateFlow(MovieFilter.ALL)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)

    val searchQuery = _searchQuery.asStateFlow()
    val sortType = _sortType.asStateFlow()
    val filter = _filter.asStateFlow()
    val errorMessage = _errorMessage.asStateFlow()
    val isLoading = _isLoading.asStateFlow()

    // Combined movies list
    val movies: StateFlow<List<Movie>> = combine(
        _allMovies, _favorites, _searchQuery, _sortType, _filter
    ) { allMovies, favs, query, sort, filter ->

        val list = if (filter == MovieFilter.ALL) {
            allMovies
        } else {
            favs.map {
                Movie(
                    id = it.id,
                    createdAt = 0,
                    title = it.title,
                    genre = emptyList(),
                    rating = Rating(it.rating),
                    release_date = 0,
                    poster_url = it.posterUrl,
                    duration_minutes = 0,
                    director = "",
                    cast = emptyList(),
                    box_office_usd = 0,
                    description = ""
                )
            }
        }

        // Search
        var filtered = if (query.isBlank()) list
        else list.filter { it.title.contains(query, ignoreCase = true) }

        // Sort
        when (sort) {
            SortType.TITLE -> filtered.sortedBy { it.title.lowercase() }
            SortType.RELEASE_DATE -> filtered.sortedByDescending { it.release_date }
            SortType.RATING -> filtered.sortedByDescending { it.rating.imdb }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadMovies()
        observeFavorites()
    }

    /** Loads movies safely with network check */
    private fun loadMovies() {
        viewModelScope.launch {
            if (!NetworkUtils.isNetworkAvailable(context)) {
                _errorMessage.value = "No internet connection"
                return@launch
            }

            _isLoading.value = true
            try {
                val result = repository.getMovies()
                if (result.isSuccess) {
                    _allMovies.value = result.getOrDefault(emptyList())
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Failed to load movies"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Unexpected error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Observes local bookmarked movies */
    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavoritesFlow().collect { favs ->
                _favorites.value = favs
            }
        }
    }

    // --- UI Event Handlers ---
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSortTypeChange(type: SortType) {
        _sortType.value = type
    }

    fun onFilterChange(filter: MovieFilter) {
        _filter.value = filter
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // Optional manual refresh (pull-to-refresh etc.)
    fun refreshMovies() {
        loadMovies()
    }
}

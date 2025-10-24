package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.model.Movie
import com.example.myapplication.ui.viewmodels.MovieFilter
import com.example.myapplication.ui.viewmodels.MovieListViewModel
import com.example.myapplication.ui.viewmodels.SortType
import java.util.Date

@Composable
fun MovieListScreen(
    viewModel: MovieListViewModel = hiltViewModel(),
    onMovieClick: (String) -> Unit
) {
    val movies by viewModel.movies.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val sort by viewModel.sortType.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        errorMessage != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(errorMessage!!, color = Color.Red)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.refreshMovies() }) {
                        Text("Retry")
                    }
                }
            }
        }

        else -> {

            Column(
                Modifier.fillMaxSize().padding(8.dp)
                    .statusBarsPadding().navigationBarsPadding()
            ) {

                // 🔍 Search
                OutlinedTextField(
                    value = query,
                    onValueChange = viewModel::onSearchQueryChange,
                    label = { Text("Search movies...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // ⚙️ Sort & Filter Dropdown
                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text("Sort: ${sort.name} | Show: ${filter.name}")
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Show All Movies") },
                            onClick = {
                                viewModel.onFilterChange(MovieFilter.ALL)
                                expanded = false
                            },
                            leadingIcon = {
                                if (filter == MovieFilter.ALL)
                                    Icon(Icons.Default.Check, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Show Bookmarked") },
                            onClick = {
                                viewModel.onFilterChange(MovieFilter.BOOKMARKED)
                                expanded = false
                            },
                            leadingIcon = {
                                if (filter == MovieFilter.BOOKMARKED)
                                    Icon(Icons.Default.Check, contentDescription = null)
                            }
                        )

                        Divider()

                        Text("Sort by", Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                        SortType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name.replace("_", " ")) },
                                onClick = {
                                    viewModel.onSortTypeChange(type)
                                    expanded = false
                                },
                                leadingIcon = {
                                    if (sort == type)
                                        Icon(Icons.Default.Check, contentDescription = null)
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // 🧾 Movie list
                if (movies.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No movies found", fontStyle = FontStyle.Italic)
                    }
                } else {
                    LazyColumn {
                        items(movies) { movie ->
                            MovieItem(movie = movie) { onMovieClick(movie.id) }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MovieItem(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(movie.poster_url),
                contentDescription = movie.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(movie.title, fontWeight = FontWeight.Bold)
                Text("Release: ${Date(movie.release_date * 1000)}")
                Text("⭐ ${movie.rating.imdb}")
            }
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null
            )
        }
    }
}



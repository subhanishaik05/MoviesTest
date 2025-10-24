package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.ui.viewmodels.MovieListViewModel
import com.example.myapplication.ui.viewmodels.MovieDetailViewModel
import java.util.Date

@Composable
fun MovieDetailScreen(
    movieId: String,
    listViewModel: MovieListViewModel = hiltViewModel(),
    detailViewModel: MovieDetailViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val movie = listViewModel.movies.collectAsState().value.find { it.id == movieId }
    val isFavorite by detailViewModel.isFavorite.collectAsState()

    LaunchedEffect(movieId) {
        detailViewModel.loadFavoriteState(movieId)
    }

    movie?.let {
        Column(Modifier.fillMaxSize().padding(16.dp)
            .statusBarsPadding()
            .navigationBarsPadding()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(it.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { detailViewModel.toggleFavorite(it) }) {
                    Icon(
                        imageVector = if (isFavorite)
                            Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray
                    )
                }
            }
            Image(
                painter = rememberAsyncImagePainter(it.poster_url),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(Modifier.height(12.dp))
            Text("⭐ ${it.rating.imdb}")
            Text("Release: ${Date(it.release_date * 1000)}")
            Text("Genre: ${it.genre.joinToString()}")
            Spacer(Modifier.height(8.dp))
            Text(it.description)
            Spacer(Modifier.height(8.dp))
            Text("Director: ${it.director}")
            Text("Cast: ${it.cast.joinToString()}")
        }
    }
}


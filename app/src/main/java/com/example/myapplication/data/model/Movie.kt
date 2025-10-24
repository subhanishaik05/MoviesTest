package com.example.myapplication.data.model

data class Movie(
    val id: String,
    val createdAt: Long,
    val title: String,
    val genre: List<String>,
    val rating: Rating,
    val release_date: Long,
    val poster_url: String,
    val duration_minutes: Int,
    val director: String,
    val cast: List<String>,
    val box_office_usd: Long,
    val description: String
)

data class Rating(val imdb: Double)

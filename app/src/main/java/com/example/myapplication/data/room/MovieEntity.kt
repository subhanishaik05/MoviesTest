package com.example.myapplication.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class MovieEntity(
    @PrimaryKey val id: String,
    val title: String,
    val posterUrl: String,
    val rating: Double
)

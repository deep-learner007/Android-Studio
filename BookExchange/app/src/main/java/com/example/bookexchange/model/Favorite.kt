package com.example.bookexchange.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val bookId: Long,
    val createdAt: Long = System.currentTimeMillis()
)

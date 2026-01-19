package com.example.bookexchange.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val isbn: String? = null,
    val description: String,
    val price: Double,
    val condition: String, // 全新、九成新、八成新、七成新
    val category: String, // 教材、小说、技术书籍、其他
    val images: String, // JSON array of image paths
    val sellerId: Long,
    val sellerName: String,
    val status: String = "available", // available, sold, reserved
    val viewCount: Int = 0,
    val favoriteCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val tags: String? = null // JSON array of tags
)

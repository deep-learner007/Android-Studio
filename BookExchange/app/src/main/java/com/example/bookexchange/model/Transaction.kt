package com.example.bookexchange.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val bookTitle: String,
    val buyerId: Long,
    val buyerName: String,
    val sellerId: Long,
    val sellerName: String,
    val price: Double,
    val status: String, // requested, accepted, completed, cancelled, rejected
    val requestMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

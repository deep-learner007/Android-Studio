package com.example.bookexchange.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderId: Long,
    val senderName: String,
    val receiverId: Long,
    val receiverName: String,
    val content: String,
    val relatedBookId: Long? = null,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

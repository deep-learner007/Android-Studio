package com.example.bookexchange.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verification_codes")
data class VerificationCode(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val email: String,
    val code: String,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 600000, // 10 minutes
    val isUsed: Boolean = false
)

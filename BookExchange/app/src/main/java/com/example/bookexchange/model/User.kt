package com.example.bookexchange.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val email: String,
    val password: String,
    val schoolEmail: String? = null,
    val isEmailVerified: Boolean = false,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val creditScore: Int = 100,
    val phoneNumber: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val isBanned: Boolean = false
)

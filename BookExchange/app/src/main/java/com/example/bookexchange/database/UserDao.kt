package com.example.bookexchange.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bookexchange.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long
    
    @Update
    suspend fun update(user: User)
    
    @Delete
    suspend fun delete(user: User)
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): User?
    
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
    
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
    
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?
    
    @Query("UPDATE users SET isEmailVerified = 1 WHERE id = :userId")
    suspend fun markEmailVerified(userId: Long)
    
    @Query("UPDATE users SET schoolEmail = :schoolEmail WHERE id = :userId")
    suspend fun updateSchoolEmail(userId: Long, schoolEmail: String)
    
    @Query("UPDATE users SET creditScore = :score WHERE id = :userId")
    suspend fun updateCreditScore(userId: Long, score: Int)
    
    @Query("SELECT * FROM users")
    fun getAllUsers(): LiveData<List<User>>
}

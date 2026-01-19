package com.example.bookexchange.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bookexchange.model.VerificationCode

@Dao
interface VerificationCodeDao {
    @Insert
    suspend fun insert(code: VerificationCode): Long
    
    @Query("SELECT * FROM verification_codes WHERE email = :email AND isUsed = 0 AND expiresAt > :currentTime ORDER BY createdAt DESC LIMIT 1")
    suspend fun getValidCode(email: String, currentTime: Long): VerificationCode?
    
    @Query("UPDATE verification_codes SET isUsed = 1 WHERE id = :codeId")
    suspend fun markAsUsed(codeId: Long)
    
    @Query("DELETE FROM verification_codes WHERE expiresAt < :currentTime")
    suspend fun deleteExpiredCodes(currentTime: Long)
    
    @Query("SELECT COUNT(*) FROM verification_codes WHERE email = :email AND createdAt > :sinceTime")
    suspend fun getCodesSentCount(email: String, sinceTime: Long): Int
}

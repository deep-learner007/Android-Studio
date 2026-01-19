package com.example.bookexchange.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bookexchange.model.Favorite

@Dao
interface FavoriteDao {
    @Insert
    suspend fun insert(favorite: Favorite): Long
    
    @Delete
    suspend fun delete(favorite: Favorite)
    
    @Query("SELECT * FROM favorites WHERE userId = :userId AND bookId = :bookId LIMIT 1")
    suspend fun getFavorite(userId: Long, bookId: Long): Favorite?
    
    @Query("SELECT bookId FROM favorites WHERE userId = :userId")
    fun getFavoriteBookIds(userId: Long): LiveData<List<Long>>
    
    @Query("DELETE FROM favorites WHERE userId = :userId AND bookId = :bookId")
    suspend fun removeFavorite(userId: Long, bookId: Long)
}

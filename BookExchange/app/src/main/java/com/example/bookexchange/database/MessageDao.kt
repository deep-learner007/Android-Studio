package com.example.bookexchange.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bookexchange.model.Message

@Dao
interface MessageDao {
    @Insert
    suspend fun insert(message: Message): Long
    
    @Update
    suspend fun update(message: Message)
    
    @Query("SELECT * FROM messages WHERE (senderId = :userId OR receiverId = :userId) ORDER BY createdAt DESC")
    fun getMessagesForUser(userId: Long): LiveData<List<Message>>
    
    @Query("SELECT * FROM messages WHERE (senderId = :userId AND receiverId = :otherUserId) OR (senderId = :otherUserId AND receiverId = :userId) ORDER BY createdAt ASC")
    fun getConversation(userId: Long, otherUserId: Long): LiveData<List<Message>>
    
    @Query("UPDATE messages SET isRead = 1 WHERE receiverId = :userId AND senderId = :senderId")
    suspend fun markAsRead(userId: Long, senderId: Long)
    
    @Query("SELECT COUNT(*) FROM messages WHERE receiverId = :userId AND isRead = 0")
    fun getUnreadCount(userId: Long): LiveData<Int>
}

package com.example.bookexchange.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bookexchange.model.Transaction

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction): Long
    
    @Update
    suspend fun update(transaction: Transaction)
    
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: Long): Transaction?
    
    @Query("SELECT * FROM transactions WHERE buyerId = :userId ORDER BY createdAt DESC")
    fun getTransactionsAsBuyer(userId: Long): LiveData<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE sellerId = :userId ORDER BY createdAt DESC")
    fun getTransactionsAsSeller(userId: Long): LiveData<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun getTransactionsForBook(bookId: Long): LiveData<List<Transaction>>
    
    @Query("UPDATE transactions SET status = :status, updatedAt = :updatedAt WHERE id = :transactionId")
    suspend fun updateStatus(transactionId: Long, status: String, updatedAt: Long)
    
    @Query("UPDATE transactions SET status = :status, completedAt = :completedAt WHERE id = :transactionId")
    suspend fun completeTransaction(transactionId: Long, status: String, completedAt: Long)
}

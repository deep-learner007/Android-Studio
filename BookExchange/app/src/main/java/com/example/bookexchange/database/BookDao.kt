package com.example.bookexchange.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.bookexchange.model.Book

@Dao
interface BookDao {
    @Insert
    suspend fun insert(book: Book): Long
    
    @Update
    suspend fun update(book: Book)
    
    @Delete
    suspend fun delete(book: Book)
    
    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: Long): Book?
    
    @Query("SELECT * FROM books WHERE status = 'available' ORDER BY createdAt DESC")
    fun getAllAvailableBooks(): LiveData<List<Book>>
    
    @Query("SELECT * FROM books WHERE sellerId = :userId ORDER BY createdAt DESC")
    fun getBooksBySeller(userId: Long): LiveData<List<Book>>
    
    @Query("SELECT * FROM books WHERE status = 'available' AND (title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    fun searchBooks(query: String): LiveData<List<Book>>
    
    @Query("SELECT * FROM books WHERE status = 'available' AND category = :category ORDER BY createdAt DESC")
    fun getBooksByCategory(category: String): LiveData<List<Book>>
    
    @Query("UPDATE books SET status = :status WHERE id = :bookId")
    suspend fun updateBookStatus(bookId: Long, status: String)
    
    @Query("UPDATE books SET viewCount = viewCount + 1 WHERE id = :bookId")
    suspend fun incrementViewCount(bookId: Long)
    
    @Query("UPDATE books SET favoriteCount = favoriteCount + 1 WHERE id = :bookId")
    suspend fun incrementFavoriteCount(bookId: Long)
    
    @Query("UPDATE books SET favoriteCount = favoriteCount - 1 WHERE id = :bookId AND favoriteCount > 0")
    suspend fun decrementFavoriteCount(bookId: Long)
}

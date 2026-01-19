package com.example.bookexchange.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.bookexchange.database.AppDatabase
import com.example.bookexchange.database.BookDao
import com.example.bookexchange.database.FavoriteDao
import com.example.bookexchange.model.Book
import com.example.bookexchange.model.Favorite

class BookRepository(context: Context) {
    private val bookDao: BookDao
    private val favoriteDao: FavoriteDao
    
    init {
        val database = AppDatabase.getDatabase(context)
        bookDao = database.bookDao()
        favoriteDao = database.favoriteDao()
    }
    
    fun getAllAvailableBooks(): LiveData<List<Book>> {
        return bookDao.getAllAvailableBooks()
    }
    
    fun searchBooks(query: String): LiveData<List<Book>> {
        return bookDao.searchBooks(query)
    }
    
    fun getBooksByCategory(category: String): LiveData<List<Book>> {
        return bookDao.getBooksByCategory(category)
    }
    
    fun getBooksBySeller(userId: Long): LiveData<List<Book>> {
        return bookDao.getBooksBySeller(userId)
    }
    
    suspend fun getBookById(bookId: Long): Book? {
        return bookDao.getBookById(bookId)
    }
    
    suspend fun addBook(book: Book): Long {
        return bookDao.insert(book)
    }
    
    suspend fun updateBook(book: Book) {
        bookDao.update(book)
    }
    
    suspend fun deleteBook(book: Book) {
        bookDao.delete(book)
    }
    
    suspend fun updateBookStatus(bookId: Long, status: String) {
        bookDao.updateBookStatus(bookId, status)
    }
    
    suspend fun incrementViewCount(bookId: Long) {
        bookDao.incrementViewCount(bookId)
    }
    
    suspend fun toggleFavorite(userId: Long, bookId: Long): Boolean {
        val existing = favoriteDao.getFavorite(userId, bookId)
        return if (existing != null) {
            favoriteDao.delete(existing)
            bookDao.decrementFavoriteCount(bookId)
            false // Removed from favorites
        } else {
            favoriteDao.insert(Favorite(userId = userId, bookId = bookId))
            bookDao.incrementFavoriteCount(bookId)
            true // Added to favorites
        }
    }
    
    fun getFavoriteBookIds(userId: Long): LiveData<List<Long>> {
        return favoriteDao.getFavoriteBookIds(userId)
    }
    
    suspend fun isFavorite(userId: Long, bookId: Long): Boolean {
        return favoriteDao.getFavorite(userId, bookId) != null
    }
}

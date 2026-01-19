package com.example.bookexchange.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.bookexchange.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var fabAddBook: FloatingActionButton
    private var currentUserId: Long = -1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user is logged in
        val prefs = getSharedPreferences("BookExchange", Context.MODE_PRIVATE)
        if (!prefs.contains("userId")) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        currentUserId = prefs.getLong("userId", -1)
        
        setContentView(R.layout.activity_main)
        
        initViews()
        setupListeners()
        
        // Load home fragment by default
        if (savedInstanceState == null) {
            loadFragment(BookListFragment())
        }
    }
    
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        fabAddBook = findViewById(R.id.fabAddBook)
        
        setSupportActionBar(toolbar)
    }
    
    private fun setupListeners() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(BookListFragment())
                    toolbar.title = "书籍市场"
                    fabAddBook.show()
                    true
                }
                R.id.navigation_favorites -> {
                    loadFragment(FavoritesFragment())
                    toolbar.title = "我的收藏"
                    fabAddBook.hide()
                    true
                }
                R.id.navigation_messages -> {
                    loadFragment(MessagesFragment())
                    toolbar.title = "消息"
                    fabAddBook.hide()
                    true
                }
                R.id.navigation_profile -> {
                    loadFragment(ProfileFragment())
                    toolbar.title = "我的"
                    fabAddBook.hide()
                    true
                }
                else -> false
            }
        }
        
        fabAddBook.setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))
        }
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}

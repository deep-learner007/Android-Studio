package com.example.bookexchange.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bookexchange.R
import com.example.bookexchange.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.ProgressBar
import android.widget.TextView

class LoginActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var registerTextView: TextView
    private lateinit var progressBar: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        // Check if user is already logged in
        val prefs = getSharedPreferences("BookExchange", Context.MODE_PRIVATE)
        if (prefs.contains("userId")) {
            startMainActivity()
            return
        }
        
        initViews()
        setupViewModel()
        setupListeners()
    }
    
    private fun initViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerTextView = findViewById(R.id.registerTextView)
        progressBar = findViewById(R.id.progressBar)
    }
    
    private fun setupViewModel() {
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        
        // Observe loading state
        authViewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            loginButton.isEnabled = !isLoading
        }
        
        // Observe toast messages
        authViewModel.toastMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                authViewModel.clearEvents()
            }
        }
        
        // Observe login success
        authViewModel.loginSuccess.observe(this) { user ->
            user?.let {
                // Save user session
                val prefs = getSharedPreferences("BookExchange", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putLong("userId", it.id)
                    putString("username", it.username)
                    putString("email", it.email)
                    apply()
                }
                
                startMainActivity()
            }
        }
    }
    
    private fun setupListeners() {
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            authViewModel.login(email, password)
        }
        
        registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    
    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

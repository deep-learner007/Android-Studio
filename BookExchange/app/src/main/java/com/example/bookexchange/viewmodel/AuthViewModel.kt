package com.example.bookexchange.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookexchange.model.User
import com.example.bookexchange.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)
    
    // LiveData for UI state
    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage
    
    private val _verifySuccess = MutableLiveData<Boolean?>()
    val verifySuccess: LiveData<Boolean?> = _verifySuccess
    
    private val _sendingCode = MutableLiveData<Boolean>(false)
    val sendingCode: LiveData<Boolean> = _sendingCode
    
    private val _verifyingCode = MutableLiveData<Boolean>(false)
    val verifyingCode: LiveData<Boolean> = _verifyingCode
    
    private val _loginSuccess = MutableLiveData<User?>()
    val loginSuccess: LiveData<User?> = _loginSuccess
    
    private val _registerSuccess = MutableLiveData<User?>()
    val registerSuccess: LiveData<User?> = _registerSuccess
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // For countdown timer
    private val _resendCountdown = MutableLiveData<Int>(0)
    val resendCountdown: LiveData<Int> = _resendCountdown
    
    fun sendSchoolEmailCode(userId: Long, schoolEmail: String) {
        val normalized = schoolEmail.trim()
        
        // Input validation
        if (normalized.isEmpty()) {
            _toastMessage.value = "请输入学校邮箱"
            return
        }
        
        if (!normalized.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))) {
            _toastMessage.value = "请输入有效的邮箱地址"
            return
        }
        
        // Prevent duplicate requests
        if (_sendingCode.value == true) {
            _toastMessage.value = "正在发送，请稍候"
            return
        }
        
        // Check countdown
        if (_resendCountdown.value!! > 0) {
            _toastMessage.value = "请等待${_resendCountdown.value}秒后重试"
            return
        }
        
        _sendingCode.value = true
        
        repository.sendEmailCode(userId, normalized, object : AuthRepository.AuthCallback {
            override fun onSuccess(message: String?) {
                _sendingCode.value = false
                _toastMessage.value = message ?: "验证码已发送"
                startCountdown(60) // 60 seconds countdown
            }
            
            override fun onError(errorCode: Int, message: String) {
                _sendingCode.value = false
                _toastMessage.value = getErrorMessage(errorCode, message)
            }
        })
    }
    
    fun verifySchoolEmailCode(userId: Long, schoolEmail: String, code: String) {
        val normalizedEmail = schoolEmail.trim()
        val trimmedCode = code.trim()
        
        // Input validation
        if (normalizedEmail.isEmpty() || trimmedCode.isEmpty()) {
            _toastMessage.value = "请输入邮箱与验证码"
            return
        }
        
        if (trimmedCode.length != 6 || !trimmedCode.all { it.isDigit() }) {
            _toastMessage.value = "请输入6位数字验证码"
            return
        }
        
        // Prevent duplicate requests
        if (_verifyingCode.value == true) {
            _toastMessage.value = "正在验证，请稍候"
            return
        }
        
        _verifyingCode.value = true
        
        repository.verifyEmailCode(userId, normalizedEmail, trimmedCode, object : AuthRepository.AuthCallback {
            override fun onSuccess(message: String?) {
                _verifyingCode.value = false
                _verifySuccess.value = true
                _toastMessage.value = message ?: "验证成功"
            }
            
            override fun onError(errorCode: Int, message: String) {
                _verifyingCode.value = false
                _verifySuccess.value = false
                _toastMessage.value = getErrorMessage(errorCode, message)
            }
        })
    }
    
    fun register(username: String, email: String, password: String, confirmPassword: String) {
        // Input validation
        if (username.trim().isEmpty() || email.trim().isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty()) {
            _toastMessage.value = "请填写所有字段"
            return
        }
        
        if (username.trim().length < 3) {
            _toastMessage.value = "用户名至少3个字符"
            return
        }
        
        if (!email.trim().matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))) {
            _toastMessage.value = "请输入有效的邮箱地址"
            return
        }
        
        if (password.length < 6) {
            _toastMessage.value = "密码至少6个字符"
            return
        }
        
        if (password != confirmPassword) {
            _toastMessage.value = "两次密码不一致"
            return
        }
        
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val result = repository.register(username.trim(), email.trim(), password)
                _isLoading.value = false
                
                if (result.isSuccess) {
                    _registerSuccess.value = result.getOrNull()
                    _toastMessage.value = "注册成功"
                } else {
                    _toastMessage.value = result.exceptionOrNull()?.message ?: "注册失败"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _toastMessage.value = "注册失败：${e.message}"
            }
        }
    }
    
    fun login(email: String, password: String) {
        // Input validation
        if (email.trim().isEmpty() || password.isEmpty()) {
            _toastMessage.value = "请输入邮箱和密码"
            return
        }
        
        if (!email.trim().matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))) {
            _toastMessage.value = "请输入有效的邮箱地址"
            return
        }
        
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                val result = repository.login(email.trim(), password)
                _isLoading.value = false
                
                if (result.isSuccess) {
                    _loginSuccess.value = result.getOrNull()
                    _toastMessage.value = "登录成功"
                } else {
                    _toastMessage.value = result.exceptionOrNull()?.message ?: "登录失败"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _toastMessage.value = "登录失败：${e.message}"
            }
        }
    }
    
    private fun startCountdown(seconds: Int) {
        _resendCountdown.value = seconds
        viewModelScope.launch {
            repeat(seconds) {
                kotlinx.coroutines.delay(1000)
                _resendCountdown.value = (_resendCountdown.value ?: 0) - 1
            }
        }
    }
    
    private fun getErrorMessage(errorCode: Int, defaultMessage: String): String {
        return when (errorCode) {
            AuthRepository.ERROR_EMPTY_FIELDS -> "请填写所有必填字段"
            AuthRepository.ERROR_INVALID_EMAIL -> "邮箱格式不正确"
            AuthRepository.ERROR_USER_EXISTS -> "用户已存在"
            AuthRepository.ERROR_USER_NOT_FOUND -> "用户不存在"
            AuthRepository.ERROR_WRONG_PASSWORD -> "密码错误"
            AuthRepository.ERROR_RATE_LIMIT -> "操作过于频繁，请稍后再试"
            AuthRepository.ERROR_INVALID_CODE -> "验证码错误"
            AuthRepository.ERROR_CODE_EXPIRED -> "验证码已过期"
            AuthRepository.ERROR_NETWORK -> "网络错误，请检查网络连接"
            else -> defaultMessage
        }
    }
    
    fun clearEvents() {
        _toastMessage.value = null
        _verifySuccess.value = null
        _loginSuccess.value = null
        _registerSuccess.value = null
    }
}

package com.example.bookexchange.repository

import android.content.Context
import com.example.bookexchange.database.AppDatabase
import com.example.bookexchange.database.UserDao
import com.example.bookexchange.database.VerificationCodeDao
import com.example.bookexchange.model.User
import com.example.bookexchange.model.VerificationCode
import com.example.bookexchange.util.AppExecutors
import java.security.MessageDigest
import kotlin.random.Random

class AuthRepository(context: Context) {
    private val userDao: UserDao
    private val verificationCodeDao: VerificationCodeDao
    private val executors: AppExecutors
    
    init {
        val database = AppDatabase.getDatabase(context)
        userDao = database.userDao()
        verificationCodeDao = database.verificationCodeDao()
        executors = AppExecutors.getInstance()
    }
    
    interface AuthCallback {
        fun onSuccess(message: String? = null)
        fun onError(errorCode: Int, message: String)
    }
    
    companion object {
        const val ERROR_EMPTY_FIELDS = 1001
        const val ERROR_INVALID_EMAIL = 1002
        const val ERROR_USER_EXISTS = 1003
        const val ERROR_USER_NOT_FOUND = 1004
        const val ERROR_WRONG_PASSWORD = 1005
        const val ERROR_RATE_LIMIT = 1006
        const val ERROR_INVALID_CODE = 1007
        const val ERROR_CODE_EXPIRED = 1008
        const val ERROR_NETWORK = 1009
        
        private const val MAX_CODES_PER_HOUR = 3
        private const val CODE_LENGTH = 6
    }
    
    suspend fun register(username: String, email: String, password: String): Result<User> {
        return try {
            // Check if user exists
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                return Result.failure(Exception("用户已存在"))
            }
            
            val existingUsername = userDao.getUserByUsername(username)
            if (existingUsername != null) {
                return Result.failure(Exception("用户名已被使用"))
            }
            
            // Hash password
            val hashedPassword = hashPassword(password)
            
            val user = User(
                username = username,
                email = email,
                password = hashedPassword
            )
            
            val userId = userDao.insert(user)
            val insertedUser = userDao.getUserById(userId)
            Result.success(insertedUser!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val hashedPassword = hashPassword(password)
            val user = userDao.login(email, hashedPassword)
            
            if (user == null) {
                Result.failure(Exception("邮箱或密码错误"))
            } else if (user.isBanned) {
                Result.failure(Exception("账号已被封禁"))
            } else {
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun sendEmailCode(userId: Long, schoolEmail: String, callback: AuthCallback) {
        executors.diskIO.execute {
            try {
                // Check rate limit
                val oneHourAgo = System.currentTimeMillis() - 3600000
                val codesSent = verificationCodeDao.getCodesSentCount(schoolEmail, oneHourAgo)
                
                if (codesSent >= MAX_CODES_PER_HOUR) {
                    executors.mainThread.execute {
                        callback.onError(ERROR_RATE_LIMIT, "发送次数过多，请1小时后再试")
                    }
                    return@execute
                }
                
                // Generate code
                val code = generateCode()
                
                // Save to database
                val verificationCode = VerificationCode(
                    userId = userId,
                    email = schoolEmail,
                    code = code
                )
                verificationCodeDao.insert(verificationCode)
                
                // Simulate email sending (in real app, use email service)
                Thread.sleep(1000)
                
                // Clean up expired codes
                verificationCodeDao.deleteExpiredCodes(System.currentTimeMillis())
                
                executors.mainThread.execute {
                    callback.onSuccess("验证码已发送到 $schoolEmail")
                }
            } catch (e: Exception) {
                executors.mainThread.execute {
                    callback.onError(ERROR_NETWORK, "发送失败：${e.message}")
                }
            }
        }
    }
    
    fun verifyEmailCode(userId: Long, schoolEmail: String, code: String, callback: AuthCallback) {
        executors.diskIO.execute {
            try {
                val currentTime = System.currentTimeMillis()
                val validCode = verificationCodeDao.getValidCode(schoolEmail, currentTime)
                
                when {
                    validCode == null -> {
                        executors.mainThread.execute {
                            callback.onError(ERROR_CODE_EXPIRED, "验证码已过期或不存在")
                        }
                    }
                    validCode.code != code -> {
                        executors.mainThread.execute {
                            callback.onError(ERROR_INVALID_CODE, "验证码错误")
                        }
                    }
                    else -> {
                        // Mark code as used
                        verificationCodeDao.markAsUsed(validCode.id)
                        
                        // Update user
                        userDao.updateSchoolEmail(userId, schoolEmail)
                        userDao.markEmailVerified(userId)
                        
                        executors.mainThread.execute {
                            callback.onSuccess("学校邮箱验证成功")
                        }
                    }
                }
            } catch (e: Exception) {
                executors.mainThread.execute {
                    callback.onError(ERROR_NETWORK, "验证失败：${e.message}")
                }
            }
        }
    }
    
    private fun generateCode(): String {
        return (1..CODE_LENGTH)
            .map { Random.nextInt(0, 10) }
            .joinToString("")
    }
    
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

package com.example.bookexchange.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors private constructor(
    val diskIO: Executor,
    val networkIO: Executor,
    val mainThread: Executor
) {
    companion object {
        private val LOCK = Any()
        private var sInstance: AppExecutors? = null
        
        fun getInstance(): AppExecutors {
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = AppExecutors(
                        Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        MainThreadExecutor()
                    )
                }
            }
            return sInstance!!
        }
    }
    
    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}

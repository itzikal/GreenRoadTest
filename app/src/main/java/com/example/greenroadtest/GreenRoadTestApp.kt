package com.example.greenroadtest

import android.app.Application
import com.example.greenroadtest.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class GreenRoadTestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin()
    }


    private fun startKoin(){
        // start Koin (di) context
        org.koin.core.context.startKoin {
            androidContext(this@GreenRoadTestApp)
            androidLogger(Level.ERROR)

            modules(
                appModule
            )
        }
    }

}
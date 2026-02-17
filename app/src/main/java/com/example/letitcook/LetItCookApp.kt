package com.example.letitcook

import android.app.Application
import android.content.Context

class LetItCookApp : Application() {

    object Globals {
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.appContext = applicationContext
    }
}
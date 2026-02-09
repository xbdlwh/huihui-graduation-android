package com.example.huihu_app

import android.app.Application



class MainApp: Application() {
     lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
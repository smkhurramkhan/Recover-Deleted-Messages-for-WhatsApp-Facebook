package com.test.recovermessages.services

import android.app.Service
import android.os.FileObserver
import android.content.Intent
import android.os.IBinder

class StartGetting : Service() {
    var fileObserver: FileObserver? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, i: Int, i2: Int): Int {
        return super.onStartCommand(intent, i, i2)
    }

    override fun onCreate() {
        super.onCreate()
        val fileObserver = fileObserver
        fileObserver?.stopWatching()
    }
}
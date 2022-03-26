package com.test.recovermessages

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION
import androidx.multidex.MultiDexApplication

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        channelId = getString(R.string.app_name)
        if (VERSION.SDK_INT >= 26) {
            val notificationChannel = NotificationChannel(
                getString(R.string.app_name),
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = getString(R.string.app_name)
            (getSystemService(NotificationManager::class.java) as NotificationManager).createNotificationChannel(
                notificationChannel
            )
        }
    }

    override fun getApplicationContext(): Context {
        return super.getApplicationContext()
    }

    companion object {
        var channelId = ""
        var instance: App? = null
        var adCount = 0
    }
}
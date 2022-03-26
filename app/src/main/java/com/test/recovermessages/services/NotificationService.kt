package com.test.recovermessages.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.test.recovermessages.App
import com.test.recovermessages.R
import com.test.recovermessages.activities.HomeActivity

class NotificationService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, i: Int, i2: Int): Int {
        startForeground(
            1, NotificationCompat.Builder(
                this, App.channelId
            )
                .setContentTitle("Service Running")
                .setContentText("Waiting for a deleted messege")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(
                    PendingIntent.getActivity(
                        this,
                        1,
                        Intent(this, HomeActivity::class.java),
                        0
                    )
                ).build()
        )
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onTaskRemoved(intent: Intent) {
        super.onTaskRemoved(intent)
    }
}
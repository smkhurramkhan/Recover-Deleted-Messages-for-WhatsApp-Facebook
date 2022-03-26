package com.test.recovermessages.utils

import com.test.recovermessages.App
import com.test.recovermessages.R
import android.graphics.BitmapFactory
import android.app.PendingIntent
import android.content.Intent
import com.test.recovermessages.activities.MainActivity
import android.os.Build
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import java.lang.Exception

class sendNotification {
    fun sendBackground(context: Context, contentTitle: String?, contentText: String?) {
        try {
            val setContentIntent = NotificationCompat.Builder(context, App.channelId)
                .setContentTitle(contentTitle as CharSequence?)
                .setContentText(contentText as CharSequence?)
                .setSmallIcon(R.drawable.ic_noti)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.logo))
                .setBadgeIconType(R.drawable.logo)
                .setAutoCancel(true)
                .setPriority(1)
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        101,
                        Intent(context, MainActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
            if (Build.VERSION.SDK_INT >= 24) {
                setContentIntent.priority = 5
            }
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(
                0,
                setContentIntent.build()
            )
            Log.d("notisendlog", "snd")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
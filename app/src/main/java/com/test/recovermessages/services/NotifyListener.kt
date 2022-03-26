package com.test.recovermessages.services

import android.content.*
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Environment
import android.os.FileObserver
import android.os.IBinder
import android.provider.Settings.Secure
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.test.recovermessages.R
import com.test.recovermessages.db.RecentNumberDB
import com.test.recovermessages.services.NotifyListener
import com.test.recovermessages.utils.SaveFiles
import com.test.recovermessages.utils.SaveMsg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class NotifyListener : NotificationListenerService() {
    var broadcastReceiver: BroadcastReceiver
    var context: Context? = null
    var onserving = false
    private var packs: ArrayList<String> = arrayListOf()

    private inner class observer(str: String?) : FileObserver(str, ALL_EVENTS) {
        override fun onEvent(i: Int, str: String?) {
            val str2 = "filedellog"
            if (i == 256 || i == 128 && str != ".probe") {
                val stringBuilder = StringBuilder()
                stringBuilder.append("create File path--> ")
                stringBuilder.append(str)
                Log.d(str2, stringBuilder.toString())
                try {
                    SaveFiles().save(str, context)
                } catch (e: Exception) {
                    val stringBuilder2 = StringBuilder()
                    stringBuilder2.append("create error: ")
                    stringBuilder2.append(e.toString())
                    Log.d(str2, stringBuilder2.toString())
                }
            }
            if (i and 512 != 0 || i and 1024 != 0) {
                val sb3 = StringBuilder()
                sb3.append("dlete File path--> ")
                sb3.append(str)
                Log.d(str2, sb3.toString())
                try {
                    SaveFiles().move(str, context)
                } catch (i2: Exception) {
                    val sb4 = StringBuilder()
                    sb4.append("del error: ")
                    sb4.append(i2.toString())
                    Log.d(str2, sb4.toString())
                }
            }
        }

        init {
            Log.d("filedellog", "start")
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d("notilogm", "bind")
        return super.onBind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d("unblog", "unb ")
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("notilogm", "on create")
        context = applicationContext
        isNotificationServiceRunning
        updateList()
        if (VERSION.SDK_INT < 23) {
            startonserving()
        } else if (check()) {
            startonserving()
        }
        LocalBroadcastManager.getInstance(context!!).registerReceiver(
            broadcastReceiver, IntentFilter(
                context!!.getString(R.string.noti_obserb)
            )
        )
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("notilogm", "on connect")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d("notilogm", "on dis connect")
    }

    override fun onStartCommand(intent: Intent, i: Int, i2: Int): Int {
        Log.d("notilogm", "on cresate")
        tryReconnectService()
        return START_STICKY
    }

    private fun startonserving() {
        val file = File(
            Environment.getExternalStorageDirectory().absolutePath,
            "WhatsApp/Media/WhatsApp Images"
        )
        val view_deleted_messages_Noti_observer = observer
        view_deleted_messages_Noti_observer?.stopWatching()
        observer = observer(file.path)
        observer?.startWatching()
    }

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification) {
        super.onNotificationPosted(statusBarNotification)
        val str = "notilogm"
        Log.d(str, "on posted")
        val stringBuilder = StringBuilder()
        stringBuilder.append("")
        stringBuilder.append(packs.size)
        Log.d(str, stringBuilder.toString())
        val it: Iterator<*> = packs.iterator()
        while (it.hasNext()) {
            Log.d("plog", (it.next() as String?)!!)
        }
        try {
            val packageName = statusBarNotification.packageName
            if (packs!!.contains(packageName)) {
                val extras = statusBarNotification.notification.extras
                val string = extras.getString(NotificationCompat.EXTRA_TITLE)
                val string1 = extras.getCharSequence(NotificationCompat.EXTRA_TEXT).toString()
                val stringBuilder2 = StringBuilder()
                stringBuilder2.append("on posted pack: ")
                stringBuilder2.append(string)
                Log.d(str, stringBuilder2.toString())
                SaveMsg(applicationContext, string, string1, packageName)
            }
        } catch (statusBarNotification2: Exception) {
            val stringBuilder1 = StringBuilder()
            stringBuilder1.append("error: ")
            stringBuilder1.append(statusBarNotification2.toString())
            Log.d(str, stringBuilder1.toString())
        }
    }

    override fun onNotificationRemoved(statusBarNotification: StatusBarNotification) {
        super.onNotificationRemoved(statusBarNotification)
        Log.d("notilogm", "on removed")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("notilogm", "on destroy")
    }

    override fun onTaskRemoved(intent: Intent) {
        super.onTaskRemoved(intent)
        Log.d("notilogm", "on task removed")
    }

    @RequiresApi(api = 23)
    private fun check(): Boolean {
        return context!!.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED
    }


    private fun updateList() {
        CoroutineScope(Dispatchers.Main).launch {
            packs.clear()
            doInBackground()
        }
    }

    private fun doInBackground() {
        CoroutineScope(Dispatchers.IO).launch {
            packs = RecentNumberDB(context).allPackages
        }
    }

    fun tryReconnectService() {
        toggleNotificationListenerService()
        if (VERSION.SDK_INT >= 24) {
            requestRebind(ComponentName(applicationContext, NotifyListener::class.java))
        }
    }

    private fun toggleNotificationListenerService() {
        val packageManager = packageManager
        packageManager.setComponentEnabledSetting(
            ComponentName(this, NotifyListener::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        packageManager.setComponentEnabledSetting(
            ComponentName(this, NotifyListener::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    val isNotificationServiceRunning: Boolean
        get() {
            val string =
                Secure.getString(context!!.contentResolver, "enabled_notification_listeners")
            val packageName: CharSequence = context!!.packageName
            if (string != null) {
                val contains = string.contains(packageName)
                if (contains) {
                    return contains
                }
            }
            return false
        }

    companion object {
        private var observer: observer? = null
    }

    init {
        onserving = false
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                try {
                    Log.d("onserlog", "received")
                    val stringExtra = intent.getStringExtra(context.getString(R.string.noti_obserb))
                    val equals = stringExtra == "true"
                    if (equals) {
                        if (!onserving) {
                            startonserving()
                            onserving = equals
                        }
                    } else if (stringExtra == "update") {
                        updateList()
                    } else {
                        onserving = equals
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }
}
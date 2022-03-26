package com.test.recovermessages.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings.Secure
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.test.recovermessages.R

class Utils(private val context: Activity) {
    @RequiresApi(api = 23)
    fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            "android.permission.WRITE_EXTERNAL_STORAGE"
        ) == PackageManager.PERMISSION_GRANTED
    }

    val isNotificationEnabled: Boolean
        get() = Secure.getString(context.contentResolver, "enabled_notification_listeners")
            .contains(context.packageName)
    val isNeedGrantPermission: Boolean
        get() {
            var b = true
            try {
                if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                        context,
                        "android.permission.WRITE_EXTERNAL_STORAGE"
                    ) != 0
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            context,
                            "android.permission.WRITE_EXTERNAL_STORAGE"
                        )
                    ) {
                        val format = String.format(
                            context.getString(R.string.format_request_permision),
                            context.getString(R.string.app_name)
                        )
                        val alertDialogBuilder = AlertDialog.Builder(context)
                        alertDialogBuilder.setTitle("Permission Required!" as CharSequence)
                        alertDialogBuilder.setCancelable(false)
                        alertDialogBuilder.setMessage(format as CharSequence).setNeutralButton(
                            "Grant" as CharSequence
                        ) { _, _ ->
                            ActivityCompat.requestPermissions(
                                context,
                                arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"),
                                111
                            )
                        }.setNegativeButton(
                            "Cancel" as CharSequence
                        ) { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            context.finish()
                        }
                        alertDialogBuilder.show()
                        return b
                    }
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"),
                        111
                    )
                    return b
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            b = false
            return b
        }

}
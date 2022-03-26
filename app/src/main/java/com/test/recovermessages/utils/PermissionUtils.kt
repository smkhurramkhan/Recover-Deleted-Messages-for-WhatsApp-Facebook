package com.test.recovermessages.utils

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class PermissionUtils {
    companion object {

        @JvmStatic
        @RequiresApi(Build.VERSION_CODES.R)
        fun hManageExternalStoragePermission(): Boolean {

            Environment.isExternalStorageManager().let {
                return when (it) {
                    true -> {
                       // Timber.d("Has permission")
                        true
                    }
                    else -> {
                       // Timber.d("Does not have permission")
                        false
                    }
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.R)
        fun hAskForManageExternalStoragePermission(appCompatActivity: AppCompatActivity) {
            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.fromParts(
                    "package",
                    appCompatActivity.packageName,
                    "Any name"
                )
                appCompatActivity.startActivity(this)
            }
        }
    }
}
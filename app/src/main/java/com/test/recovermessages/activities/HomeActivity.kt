package com.test.recovermessages.activities

import android.app.AlertDialog
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.service.notification.NotificationListenerService
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.test.recovermessages.App
import com.test.recovermessages.R
import com.test.recovermessages.db.RecentNumberDB
import com.test.recovermessages.fragments.UsersFragment
import com.test.recovermessages.fragments.WAFragment
import com.test.recovermessages.services.NotifyListener
import com.test.recovermessages.utils.AdmobHelper
import com.test.recovermessages.utils.PermissionUtils.Companion.hAskForManageExternalStoragePermission
import com.test.recovermessages.utils.PermissionUtils.Companion.hManageExternalStoragePermission
import java.io.File

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var drawer: DrawerLayout? = null
    private var pager: ViewPager? = null
    private var waFragmentGb: WAFragment? = null
    private var waFragmentW: WAFragment? = null
    private var waFragmentWb: WAFragment? = null
    private var relativeLayout: RelativeLayout? = null
    private val PERMISSION_REQUEST_CODE = 111
    private fun ask() {
        try {
            if (Build.VERSION.SDK_INT >= 22) {
                startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            } else {
                startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun createFolder() {
        try {
            val file = File(
                Environment.getExternalStorageDirectory().absolutePath,
                getString(R.string.app_name)
            )
            if (!file.exists()) {
                file.mkdir()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun navigationrelated() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer!!.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.itemIconTintList = null
    }

    private fun setupPager() {
        try {
            pager = findViewById<View>(R.id.pager) as ViewPager
            val tabLayout = findViewById<View>(R.id.tab) as TabLayout
            val allPackages = RecentNumberDB(this).allPackages
            for (i in allPackages.indices) {
                val packageManager = packageManager
                var packageInfo: PackageInfo? = null
                packageInfo = packageManager.getPackageInfo(
                    (allPackages[i] as String),
                    PackageManager.GET_META_DATA
                )
                tabLayout.addTab(
                    tabLayout.newTab()
                        .setText(packageManager.getApplicationLabel(packageInfo.applicationInfo))
                        .setIcon(packageManager.getApplicationIcon(packageInfo.applicationInfo))
                )
            }
            tabLayout.isTabIndicatorFullWidth = false
            tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            tabLayout.setTabTextColors(
                resources.getColor(R.color.dinwhite),
                resources.getColor(R.color.white)
            )
            pager!!.adapter = FragAdapter(
                supportFragmentManager, allPackages
            )
            if (intent.getIntExtra("pos", 0) == 1) {
                pager!!.currentItem = 1
            }
            tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    if (App.adCount % 4 == 0) {
                        AdmobHelper.showInterstitialAd(this@HomeActivity, AdmobHelper.ADSHOWN)
                    }
                    App.adCount++
                    pager!!.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            //new TabLayout.TabLayoutOnPageChangeListener(tabLayout)
            pager!!.addOnPageChangeListener((TabLayoutOnPageChangeListener(tabLayout) as OnPageChangeListener))
            findViewById<View>(R.id.progressBar2).visibility = View.GONE
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private inner class FragAdapter internal constructor(
        fragmentManager: FragmentManager?,
        private val packs: ArrayList<String>
    ) : FragmentStatePagerAdapter(
        fragmentManager!!
    ) {
        override fun getItem(i: Int): Fragment {
            var fragment: Fragment? = null
            for (i2 in packs.indices) {
                if (packs[i].contains("com.whatsapp")) {
                    waFragmentW = WAFragment()
                        .newInstance(packs[i])
                    fragment = waFragmentW
                } else if (packs[i].contains("com.whatsapp.w4b")) {
                    waFragmentWb = WAFragment()
                        .newInstance(packs[i])
                    fragment = waFragmentWb
                } else if (packs[i].contains("com.gbwhatsapp")) {
                    waFragmentGb = WAFragment()
                        .newInstance(packs[i])
                    fragment = waFragmentGb
                } else {
                    fragment = UsersFragment().newInstance(packs[i])
                }
            }
            return fragment!!
        }

        override fun getCount(): Int {
            return packs.size
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

    override fun onBackPressed() {
        val drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val isDrawerOpen = drawerLayout.isDrawerOpen(GravityCompat.START)
        if (isDrawerOpen) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Handler().postDelayed({
            navigationrelated()
            tryReconnectService()
            setupPager()
        }, 1)
        relativeLayout = findViewById<View>(R.id.mainContainer) as RelativeLayout
        AdmobHelper.showSmartBannerAd(this@HomeActivity, relativeLayout, AdmobHelper.ADSHOWN)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        if (itemId == R.id.share) {
            val intent = Intent("android.intent.action.SEND")
            intent.type = "text/plain"
            val sb =
                resources.getString(R.string.share) + " https://play.google.com/store/apps/details?id=" + packageName
            intent.putExtra("android.intent.extra.TEXT", sb)
            startActivity(intent)
        } else if (itemId == R.id.rate) {
            val intent = Intent("android.intent.action.VIEW")
            val sb = "https://play.google.com/store/apps/details?id=$packageName"
            intent.data = Uri.parse(sb)
            startActivity(intent)
        } else if (itemId == R.id.contact) {
            val intent3 = Intent(
                "android.intent.action.SENDTO",
                Uri.fromParts("mailto", getString(R.string.email), null)
            )
            intent3.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name))
            startActivity(intent3)
        } else if (itemId == R.id.help) {
            if (App.adCount % 4 == 0) {
                AdmobHelper.showInterstitialAd(this@HomeActivity, AdmobHelper.ADSHOWN)
            }
            startActivity(Intent(this, Tutorial::class.java))
        } else if (itemId == R.id.restart) {
            ask()
        } else if (itemId == R.id.manage) {
            if (App.adCount % 4 == 0) {
                AdmobHelper.showInterstitialAd(this@HomeActivity, AdmobHelper.ADSHOWN)
            }
            val intent = Intent(this, Setup::class.java)
            intent.putExtra("key", 1)
            finish()
            startActivity(intent)
        }
        (findViewById<View>(R.id.drawer_layout) as DrawerLayout).closeDrawer(
            GravityCompat.START
        )
        return true
    }

    override fun onRequestPermissionsResult(i: Int, strArr: Array<String>, iArr: IntArray) {
        super.onRequestPermissionsResult(i, strArr, iArr)
        if (i == PERMISSION_REQUEST_CODE && iArr.size > 0) {
            if (iArr[0] == 0) {
                createFolder()
                val intent = Intent(getString(R.string.files))
                intent.putExtra(
                    getString(R.string.files),
                    getString(R.string.remove_permission_framgent)
                )
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!hManageExternalStoragePermission()) {
                        hShowManageExternalStorageDialog()
                    }
                }
            } else if (iArr[0] == RESULT_OK) {
                val format = String.format(
                    getString(R.string.format_request_permision),
                    getString(R.string.app_name)
                )
                val alertDialogBuilder = AlertDialog.Builder(this@HomeActivity)
                alertDialogBuilder.setTitle("Permission Required!" as CharSequence)
                alertDialogBuilder.setCancelable(false)
                alertDialogBuilder.setMessage(format as CharSequence).setNeutralButton(
                    "Grant" as CharSequence,
                    { dialogInterface, n ->
                        ActivityCompat.requestPermissions(
                            this@HomeActivity,
                            arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"),
                            111
                        )
                    }).setNegativeButton(
                    "Cancel" as CharSequence,
                    { dialogInterface, n ->
                        dialogInterface.dismiss()
                        finish()
                    })
                alertDialogBuilder.show()
            }
        }
    }

    fun tryReconnectService() {
        toggleNotificationListenerService()
        if (Build.VERSION.SDK_INT >= 24) {
            NotificationListenerService.requestRebind(
                ComponentName(
                    applicationContext,
                    NotifyListener::class.java
                )
            )
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private fun hShowManageExternalStorageDialog() {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle("Manage storage permission")
        alertBuilder.setMessage(getString(R.string.we_need_manage_storage_permission))
        alertBuilder.setPositiveButton(
            android.R.string.ok
        ) { dialog: DialogInterface?, which: Int -> hAskForManageExternalStoragePermission(this) }
        val alert = alertBuilder.create()
        alert.show()
    }
}
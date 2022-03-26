package com.test.recovermessages.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.test.recovermessages.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (checkSetupFinished()) {
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this@MainActivity, Setup::class.java))
            finish()
        }
    }

    private fun checkSetupFinished(): Boolean {
        return getSharedPreferences("SETUP", 0).getBoolean("setup", false)
    }
}
package com.udimuhaits.nutrifit.ui.splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.ui.getstarted.ContainerActivity
import com.udimuhaits.nutrifit.ui.login.LoginActivity

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val isOpened = sharedPreferences.getBoolean("isOpened", false)

        if (!isOpened) {
            sharedPreferences.edit().apply {
                putBoolean("isOpened", true)
                apply()
                navigateToContainer()
            }
        } else {
            navigateToLogin()
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun navigateToContainer() {
        Handler().postDelayed({
            val intent = Intent(this, ContainerActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }

    private fun navigateToLogin() {
        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}
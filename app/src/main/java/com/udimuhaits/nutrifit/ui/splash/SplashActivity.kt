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
import com.udimuhaits.nutrifit.ui.form.FormActivity
import com.udimuhaits.nutrifit.ui.getstarted.ContainerActivity
import com.udimuhaits.nutrifit.ui.login.LoginActivity

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPreferences = this.getSharedPreferences("sharedPrefStarted", Context.MODE_PRIVATE)
        val isOpened = sharedPreferences.getBoolean("alreadyGettingStarted", false)
        val isStarted = sharedPreferences.getBoolean("isStarted", false)

        sharedPreferences = this.getSharedPreferences("sharedPrefLogin", Context.MODE_PRIVATE)
        val isLogin = sharedPreferences.getBoolean("isLogin", false)


        if (!isOpened) {
            sharedPreferences.apply {
                navigateToContainer()
            }
        } else if (!isStarted) {
            sharedPreferences.edit().apply {
                putBoolean("isStarted", true)
                navigateToLogin()
                apply()
            }
        } else if (!isLogin) {
            sharedPreferences.edit().apply {
                putBoolean("isLogin", true)
                navigateToForm()
                apply()
            }
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
        }, 2000)
    }

    private fun navigateToLogin() {
        Handler().postDelayed({
            // for developing skip login
//            startActivity(Intent(this, HomeActivity::class.java))
//            finish()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }, 2000)
    }

    private fun navigateToForm() {
        Handler().postDelayed({
            val intent = Intent(this, FormActivity::class.java)
            startActivity(intent)
        }, 2000)
    }
}
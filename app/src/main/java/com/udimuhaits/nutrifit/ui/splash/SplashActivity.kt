package com.udimuhaits.nutrifit.ui.splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.databinding.ActivitySplashBinding
import com.udimuhaits.nutrifit.ui.form.FormInputActivity
import com.udimuhaits.nutrifit.ui.getstarted.ContainerActivity
import com.udimuhaits.nutrifit.ui.getstarted.StartedFragment.Companion.PREFS_ONBOARDING
import com.udimuhaits.nutrifit.ui.home.HomeActivity
import com.udimuhaits.nutrifit.ui.login.LoginActivity
import com.udimuhaits.nutrifit.ui.login.LoginActivity.Companion.PREFS_STARTED

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val topAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.top_animation_splash
        )
    }
    private val bottomAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.bottom_animation_splash
        )
    }

    private val leftAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.left_animation_splash
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgLogo.startAnimation(topAnimation)
        binding.tvSplashScreen.startAnimation(leftAnimation)
        binding.imgUdimuhaits.startAnimation(bottomAnimation)

        sharedPreferences = this.getSharedPreferences(PREFS_ONBOARDING, Context.MODE_PRIVATE)
        val isStarted = sharedPreferences.getBoolean("isStarted", false)

        sharedPreferences = this.getSharedPreferences(PREFS_STARTED, Context.MODE_PRIVATE)
        val isLogin = sharedPreferences.getBoolean("isLogin", false)
        val isSave = sharedPreferences.getBoolean("isSave", false)

        if (!isStarted) {
            navigateToContainer()
        } else if (!isLogin) {
            navigateToLogin()
        } else if (!isSave) {
            navigateToForm()
        } else {
            navigateToHome()
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
        }, 3000)
    }

    private fun navigateToLogin() {
        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

    private fun navigateToForm() {
        Handler().postDelayed({
            val intent = Intent(this, FormInputActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

    private fun navigateToHome() {
        Handler().postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}
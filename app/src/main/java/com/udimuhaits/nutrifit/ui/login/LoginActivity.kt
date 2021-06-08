package com.udimuhaits.nutrifit.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.databinding.ActivityLoginBinding
import com.udimuhaits.nutrifit.ui.form.FormInputActivity
import com.udimuhaits.nutrifit.ui.form.FormViewModel
import com.udimuhaits.nutrifit.ui.home.HomeActivity
import com.udimuhaits.nutrifit.utils.userPreference

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 100
        const val PREFS_STARTED = "sharedPrefStarted"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var gsc: GoogleSignInClient
    private lateinit var fAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private val loginViewModel: LoginViewModel by viewModels()
    private val formViewModel: FormViewModel by viewModels()
    private var isBackPressed = false

    private val bottomAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.bottom_animation_onboarding
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
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvHealthy.startAnimation(leftAnimation)
        binding.imgNutrifit.startAnimation(leftAnimation)
        binding.tvDescription.startAnimation(leftAnimation)
        binding.btnLoginGoogle.startAnimation(bottomAnimation)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString((R.string.default_web_client_id)))
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)

        binding.btnLoginGoogle.setOnClickListener {
            val intent = gsc.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }

        fAuth = FirebaseAuth.getInstance()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val sat = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (sat.isSuccessful) {
                try {
                    val gsa = sat.getResult(ApiException::class.java)
                    if (gsa != null) {
                        binding.progressBar.visibility = View.VISIBLE
                        val authCredential = GoogleAuthProvider.getCredential(gsa.idToken, null)
                        fAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    sharedPreferences =
                                        this.getSharedPreferences(
                                            PREFS_STARTED,
                                            Context.MODE_PRIVATE
                                        )
                                    sharedPreferences.edit().apply {
                                        putBoolean("isLogin", true)
                                        checkProfile()
                                        apply()
                                    }
                                } else {
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this,
                                        "Authentication Failed : " + task.exception?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            } else {
                Log.d("cek", sat.exception.toString())
            }
        }
    }

    private fun checkProfile() {
        val account = fAuth.currentUser
        val aUsername = account?.displayName
        val aEmail = account?.email
        val aProfilePic = account?.photoUrl

        loginViewModel.postUser(aUsername, aEmail, aProfilePic.toString()).observe(this, { users ->
            this.userPreference().edit().apply {
                putString("token", users.accessToken.toString())
                users.userId?.let { putInt("user_id", it) }
                apply()
            }
            formViewModel.getUser(users.accessToken, users.userId).observe(this, { data ->
                if (data.birthDate != null || data.height != null || data.weight != null) {
                    navigateToHome()
                } else {
                    navigateToForm()
                }
            })
        })
    }

    private fun navigateToHome() {
        sharedPreferences = this.getSharedPreferences(PREFS_STARTED, Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putBoolean("isSave", true)
            val intent = Intent(
                applicationContext,
                HomeActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            apply()
        }
    }

    private fun navigateToForm() {
        val intent =
            Intent(this, FormInputActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (isBackPressed) {
            super.onBackPressed()
        }
        isBackPressed = true
        Toast.makeText(this, getString(R.string.back), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ isBackPressed = false }, 2000)
    }
}
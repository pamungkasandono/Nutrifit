package com.udimuhaits.nutrifit.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.databinding.ActivityLoginBinding
import com.udimuhaits.nutrifit.ui.form.FormActivity
import com.udimuhaits.nutrifit.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 100
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var gsc: GoogleSignInClient
    private lateinit var fAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString((R.string.default_web_client_id)))
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)
        sharedPreferences = this.getSharedPreferences("sharedPrefLogin", Context.MODE_PRIVATE)

        binding.btnLoginGoogle.setOnClickListener {
            sharedPreferences.edit().apply {
                putBoolean("isLogin", true)
                apply()
            }
            val intent = gsc.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }

        fAuth = FirebaseAuth.getInstance()
        val fUser = fAuth.currentUser

        if (fUser != null) {
            val intent = Intent(this, FormActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
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
                        fAuth.signInWithCredential(authCredential).addOnCompleteListener(this, OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(this, FormActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            } else {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(this, "Authentication Failed : " + task.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
package com.udimuhaits.nutrifit.ui.form

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.databinding.ActivityFormInputBinding
import com.udimuhaits.nutrifit.ui.home.HomeActivity
import com.udimuhaits.nutrifit.ui.login.LoginViewModel
import com.udimuhaits.nutrifit.utils.getDate
import com.udimuhaits.nutrifit.utils.userPreference
import java.text.SimpleDateFormat
import java.util.*

class FormInputActivity : AppCompatActivity() {

    companion object {
        const val PREFS_SAVE = "sharedPrefSave"
    }

    private lateinit var binding: ActivityFormInputBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var gsc: GoogleSignInClient
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var dateFormatter: SimpleDateFormat
    private lateinit var sharedPreferences: SharedPreferences
    private var isBackPressed = false

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gsc = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        fAuth = FirebaseAuth.getInstance()
        dateFormatter = SimpleDateFormat("yyyy-MM-dd")

        postUser()

        binding.edtDate.setOnClickListener {
            showDateDialog()
        }

        setEnabledButton()

    }

    private fun postUser() {
        val account = fAuth.currentUser
        val aUsername = account?.displayName
        val aEmail = account?.email
        val aProfilePic = account?.photoUrl

        val viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[LoginViewModel::class.java]

        viewModel.postUser(aUsername, aEmail, aProfilePic.toString()).observe(this, { users ->

            // set token to prefrence
            this.userPreference().edit().apply {
                putString("token", users.accessToken.toString())
                users.userId?.let { putInt("user_id", it) }
                apply()
            }

            users.apply {
                binding.edtUsername.setText(username)
                binding.edtEmail.setText(email)
                Glide
                    .with(this@FormInputActivity)
                    .load(profilePic)
                    .into(binding.imgProfile)
            }

            viewModel.getUser(users.accessToken).observe(this, { body ->
                body.apply {
                    Toast.makeText(applicationContext, detail, Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, status, Toast.LENGTH_SHORT).show()
                }
            })
            binding.btnSaveProfile.setOnClickListener {
                val birthDate = binding.edtDate.text.toString()
                val height = binding.edtHeight.text.toString()
                val weight = binding.edtWeight.text.toString()
                showAlertDialog(
                    users.userId,
                    users.accessToken,
                    birthDate,
                    height.toInt(),
                    weight.toDouble(),
                    users.profilePic
                )
//                val kalori_harian = 88.4 + (13.7 * weight.toInt()) + (4.8 * height.toInt()) - (5.8 * (getDate() - birthDate))

//                this.userPreference().edit().apply {
//                    putString("kalori_harian", )
//                    apply()
//                }
            }
        })

        viewModel.isLoading.observe(this, { loading ->
            binding.progressBar.visibility =
                if (loading) android.view.View.VISIBLE else android.view.View.GONE
        })
    }

    private fun showDateDialog() {
        val calendar = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(
            this,
            { view, year, month, dayOfMonth ->
                val newDate = Calendar.getInstance()
                newDate.set(year, month, dayOfMonth)
                binding.edtDate.setText(dateFormatter.format(newDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun setEnabledButton() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val birthDate = binding.edtDate.text
                val height = binding.edtHeight.text
                val weight = binding.edtWeight.text

                binding.btnSaveProfile.isEnabled =
                    !birthDate?.isEmpty()!! && !height?.isEmpty()!! && !weight?.isEmpty()!!
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.edtDate.addTextChangedListener(textWatcher)
        binding.edtHeight.addTextChangedListener(textWatcher)
        binding.edtWeight.addTextChangedListener(textWatcher)
        binding.btnSaveProfile.isEnabled = false
    }

    private fun showAlertDialog(
        userId: Int?,
        token: String?,
        birthDate: String?,
        height: Int?,
        weight: Double?,
        imageProfile: String?
    ) {
        val viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[FormViewModel::class.java]

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialog_title)
        builder.setMessage(R.string.message_save)
        builder.setIcon(R.drawable.ic_save)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            Toast.makeText(
                applicationContext,
                "Welcome to nutirift. Start your better live journey right now!",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.putUser(userId, token, birthDate, height, weight)
            sharedPreferences = this.getSharedPreferences(PREFS_SAVE, Context.MODE_PRIVATE)
            sharedPreferences.edit().apply {
                putBoolean("isSave", true)
                putString("saveImage", imageProfile)
                val intent = Intent(applicationContext, HomeActivity::class.java)
                intent.putExtra("imageProfile", imageProfile)
                startActivity(intent)
                finish()
                apply()
            }
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            Toast.makeText(applicationContext, "Cancel saved profile", Toast.LENGTH_SHORT).show()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onBackPressed() {
        if (isBackPressed) {
            super.onBackPressed()
        }
        isBackPressed = true
        Toast.makeText(this, "Tekan sekali lagi untuk kembali", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ isBackPressed = false }, 2000)
    }
}
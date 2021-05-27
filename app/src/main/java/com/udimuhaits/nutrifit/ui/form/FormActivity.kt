package com.udimuhaits.nutrifit.ui.form

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
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
import com.udimuhaits.nutrifit.databinding.ActivityFormBinding
import com.udimuhaits.nutrifit.ui.home.HomeActivity
import com.udimuhaits.nutrifit.ui.login.LoginViewModel
import com.udimuhaits.nutrifit.utils.userPreference
import java.text.SimpleDateFormat
import java.util.*

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var gsc: GoogleSignInClient
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var dateFormatter: SimpleDateFormat

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gsc = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        fAuth = FirebaseAuth.getInstance()
        dateFormatter = SimpleDateFormat("yyyy-MM-dd")

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
                apply()
            }

            users.apply {
                binding.edtUsername.setText(username)
                binding.edtEmail.setText(email)
                Glide
                    .with(this@FormActivity)
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
                    weight.toInt(),
                    users.profilePic
                )
            }
        })

        viewModel.isLoading.observe(this, { loading ->
            binding.progressBar.visibility =
                if (loading) android.view.View.VISIBLE else android.view.View.GONE
        })

        binding.edtDate.setOnClickListener {
            showDateDialog()
        }

        setEnabledButton()

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
        weight: Int?,
        imageProfile: String?
    ) {
        val viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[LoginViewModel::class.java]

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
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("imageProfile", imageProfile)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            Toast.makeText(applicationContext, "Cancel saved profile", Toast.LENGTH_SHORT).show()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}
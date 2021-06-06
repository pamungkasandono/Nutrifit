package com.udimuhaits.nutrifit.ui.form

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.databinding.ActivityFormUpdateBinding
import com.udimuhaits.nutrifit.ui.home.HomeActivity
import com.udimuhaits.nutrifit.ui.login.LoginViewModel
import java.text.SimpleDateFormat
import java.util.*

class FormUpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormUpdateBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var dateFormatter: SimpleDateFormat
    private val loginViewModel: LoginViewModel by viewModels()
    private val formViewModel: FormViewModel by viewModels()

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth = FirebaseAuth.getInstance()
        dateFormatter = SimpleDateFormat("yyyy-MM-dd")

        binding.edtDate.setOnClickListener {
            showDateDialog()
        }

        putAndGetUser()

        setEnabledButton()
    }

    private fun putAndGetUser() {
        val account = fAuth.currentUser
        val aUsername = account?.displayName
        val aEmail = account?.email
        val aProfilePic = account?.photoUrl

        loginViewModel.postUser(aUsername, aEmail, aProfilePic.toString()).observe(this, { users ->
            formViewModel.getUser(users.accessToken, users.userId).observe(this, { update ->
                Glide
                    .with(this)
                    .load(update.profilePic)
                    .into(binding.imgProfile)
                binding.edtUsername.setText(aUsername)
                binding.edtEmail.setText(aEmail)
                binding.edtDate.setText(update.birthDate)
                binding.edtHeight.setText(update.height.toString())
                binding.edtWeight.setText(update.weight.toString())
            })

            binding.btnUpdateProfile.setOnClickListener {
                val birthDate = binding.edtDate.text.toString()
                val height = binding.edtHeight.text.toString()
                val weight = binding.edtWeight.text.toString()
                formViewModel.putUser(
                    users.userId,
                    users.accessToken,
                    birthDate,
                    height.toInt(),
                    weight.toDouble()
                )
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, getString(R.string.success_update), Toast.LENGTH_SHORT)
                    .show()
            }
        })
        formViewModel.isLoading.observe(this, { loading ->
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

                binding.btnUpdateProfile.isEnabled =
                    !birthDate?.isEmpty()!! && !height?.isEmpty()!! && !weight?.isEmpty()!!
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.edtDate.addTextChangedListener(textWatcher)
        binding.edtHeight.addTextChangedListener(textWatcher)
        binding.edtWeight.addTextChangedListener(textWatcher)
        binding.btnUpdateProfile.isEnabled = false
    }
}
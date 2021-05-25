package com.udimuhaits.nutrifit.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.udimuhaits.nutrifit.databinding.ActivityHomeBinding
import com.udimuhaits.nutrifit.ui.detail.DetailActivity
import com.udimuhaits.nutrifit.ui.login.LoginActivity
import com.udimuhaits.nutrifit.ui.login.LoginViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var isBackPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var asdd: List<String> = listOf()

        val image = intent.getStringExtra("imageProfile")
        Glide
            .with(this)
            .load(image)
            .into(binding.imgProfile)


//        binding.searchBox.setText("1 serving of Pizza 1 serving of Water 1 serving of Noodles 1 serving of Cake 1 serving of Meat balls")
//        binding.searchBox.setText("Pizza Water Noodles Cake Ramen")

        binding.btnSearch.setOnClickListener {
            val serving = " 1 serving of "
            val searchData = binding.searchBox.text
            val dataQuery = searchData.toString()
            asdd = dataQuery.split(" ")
            var mDataQuery = ""
            asdd.forEach {
                mDataQuery += serving + it
            }

            Log.d("asdasd home", mDataQuery)

            val query = mDataQuery
            Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.QUERY, query.toString())
                startActivity(this)
            }
        }
    }

    override fun onBackPressed() {
        if (isBackPressed) {
            super.onBackPressed()
        }

        isBackPressed = true
        Toast.makeText(this, "Tekan sekali lagi untuk kembali", Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable { isBackPressed = false }, 2000)
    }
}
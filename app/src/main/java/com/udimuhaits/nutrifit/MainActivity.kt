package com.udimuhaits.nutrifit

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udimuhaits.nutrifit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var isBackPressed = false
    private lateinit var binding: ActivityMainBinding

    // added new

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSearch.setOnClickListener {
            startActivity(Intent(this, DetailActivity::class.java))
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
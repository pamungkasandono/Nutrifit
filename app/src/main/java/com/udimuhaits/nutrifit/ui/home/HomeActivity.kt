package com.udimuhaits.nutrifit.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udimuhaits.nutrifit.databinding.ActivityMainBinding
import com.udimuhaits.nutrifit.ui.detail.DetailActivity

class HomeActivity : AppCompatActivity() {
    private var isBackPressed = false
    private lateinit var binding: ActivityMainBinding

    // added new

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var asdd: List<String> = listOf()

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
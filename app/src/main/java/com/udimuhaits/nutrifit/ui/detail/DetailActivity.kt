package com.udimuhaits.nutrifit.ui.detail

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.databinding.ActivityDetailBinding
import com.udimuhaits.nutrifit.utils.areYouSure
import com.udimuhaits.nutrifit.utils.toast
import com.udimuhaits.nutrifit.utils.toastLong


class DetailActivity : AppCompatActivity() {
    private lateinit var detailBinding: ActivityDetailBinding

    companion object {
        const val QUERY = "QUERY"
        const val WITH_IMAGE = "WITH_IMAGE"
        const val IMAGE_PATH = "IMAGE_PATH"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)

        val detailAdapter = DetailAdapter()

        val viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[DetailViewModel::class.java]

        val intentData = intent.extras
        val query = intentData?.getString(QUERY)
        val isComeWithImage = intentData?.getBoolean(WITH_IMAGE)

        if (isComeWithImage == true) {
            Glide.with(this)
                .load(intentData.getString(IMAGE_PATH))
                .into(detailBinding.imageView4)
        } else {
            detailBinding.imageView4.visibility = View.GONE
        }

        detailBinding.progressBar1.visibility = View.VISIBLE
        // data dari viewModel di kirim ke adapter
        if (query != null) {
            viewModel.getListFood(query).observe(this) {
                detailBinding.progressBar1.apply {
                    this.post {
                        this.visibility = View.GONE
                    }
                }
                this.toast("done")
                detailAdapter.setData(it)
                detailAdapter.notifyDataSetChanged()
            }
        }

        detailAdapter.getTotalListener(object : DetailAdapter.InterfaceListener {
            override fun totalSendToDetail(
                totalServing: String,
                totalCalories: String,
                totalCarbo: String,
                totalProtein: String,
                totalFat: String,
                totalCholesterol: String
            ) {
                with(detailBinding) {
                    this.totalServing.post {
                        this.totalServing.text = resources.getString(
                            R.string.nutrition_placeholder_in_g,
                            String.format("%.1f", totalServing.toFloat())
                        )
                        this.totalServing.setOnClickListener {
                            this@DetailActivity.toastLong("This values is total Serving")
                        }
                    }
                    this.totalCalories.post {
                        this.totalCalories.text = resources.getString(
                            R.string.nutrition_placeholder_in_cal,
                            String.format("%.1f", totalCalories.toFloat())
                        )
                        this.totalCalories.setOnClickListener {
                            this@DetailActivity.toastLong("This values is total Calories")
                        }
                        Log.d("asdasd", totalCalories)
                    }
                    this.totalCarbo.post {
                        this.totalCarbo.text = resources.getString(
                            R.string.nutrition_placeholder_in_g,
                            String.format("%.1f", totalCarbo.toFloat())
                        )
                        this.totalCarbo.setOnClickListener {
                            this@DetailActivity.toastLong("This values is total Carbo")
                        }
                    }
                    this.totalProtein.post {
                        this.totalProtein.text = resources.getString(
                            R.string.nutrition_placeholder_in_g,
                            String.format("%.1f", totalProtein.toFloat())
                        )
                        this.totalProtein.setOnClickListener {
                            this@DetailActivity.toastLong("This values is total Protein")
                        }
                    }
                    this.totalFat.post {
                        this.totalFat.text = resources.getString(
                            R.string.nutrition_placeholder_in_g,
                            String.format("%.1f", totalFat.toFloat())
                        )
                        this.totalFat.setOnClickListener {
                            this@DetailActivity.toastLong("This values is total Fat")
                        }
                    }
                    this.totalCholesterol.post {
                        this.totalCholesterol.text = resources.getString(
                            R.string.nutrition_placeholder_in_mg,
                            String.format("%.1f", totalCholesterol.toFloat())
                        )
                        this.totalCholesterol.setOnClickListener {
                            this@DetailActivity.toastLong("This values is total Cholesterol")
                        }
                    }
                }
            }
        })

        with(detailBinding.rvNutrition) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = detailAdapter
        }
    }

    override fun onBackPressed() {
        this.areYouSure("Close this session will delete your list").apply {
            setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
                Intent().apply {
                    this.putExtra("isSuccess", true)
                    setResult(RESULT_OK, this)
                }
                finish()
            }
            show()
        }
    }
}
package com.udimuhaits.nutrifit.ui.detail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.data.CNEntity
import com.udimuhaits.nutrifit.data.FoodDataDailyConsumptionItem
import com.udimuhaits.nutrifit.data.MenuListEntity
import com.udimuhaits.nutrifit.databinding.ActivityDetailBinding
import com.udimuhaits.nutrifit.network.NutrifitApiConfig
import com.udimuhaits.nutrifit.ui.home.HomeActivity
import com.udimuhaits.nutrifit.utils.areYouSure
import com.udimuhaits.nutrifit.utils.toast
import com.udimuhaits.nutrifit.utils.toastLong
import com.udimuhaits.nutrifit.utils.userPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {
    private lateinit var detailBinding: ActivityDetailBinding
    private var arrayData = ArrayList<CNEntity>()
    private var arrayMenuList = ArrayList<MenuListEntity>()
    private var imageID: String? = null
    private var isProsesLoadData = true
    private var saveIt = true
    private var newDataUpdated = false
    private val detailViewModel: DetailViewModel by viewModels()

    private var totalServing = 0F
    private var totalCalories = 0F
    private var totalCarbo = 0F
    private var totalProtein = 0F
    private var totalFat = 0F
    private var totalCholesterol = 0F

    companion object {
        const val QUERY = "QUERY"
        const val WITH_IMAGE = "WITH_IMAGE"
        const val IMAGE_PATH = "IMAGE_PATH"
        const val IMAGE_ID = "IMAGE_ID"
        const val ARRAYLIST = "ARRAYLIST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)

        detailBinding.fabOption1.setOnClickListener {
            this.toast(getString(R.string.remove_menu_history))
            saveIt = false
            detailBinding.fab.collapse()
        }

        detailBinding.fabOption2.setOnClickListener {
            Intent(this, HomeActivity::class.java).apply {
                putExtra("fab_code", "image")
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(this)
                if (saveIt) {
                    saveToHistory()
                    newDataUpdated = true
                }
                finish()
            }
        }

        detailBinding.fabOption3.setOnClickListener {
            Intent(this, HomeActivity::class.java).apply {
                putExtra("fab_code", "manual")
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(this)
                if (saveIt) {
                    saveToHistory()
                    newDataUpdated = true
                }
                finish()
            }
        }

        val detailAdapter = DetailAdapter()

        val intentData = intent.extras
        val query = intentData?.getString(QUERY)
        val isComeWithImage = intentData?.getBoolean(WITH_IMAGE)
        imageID = intentData?.getString(IMAGE_ID, null)
        arrayMenuList =
            intentData?.getParcelableArrayList<MenuListEntity>(ARRAYLIST) as ArrayList<MenuListEntity>

        if (isComeWithImage == true) {
            Glide.with(this)
                .load(intentData.getString(IMAGE_PATH))
                .into(detailBinding.imageView4)
        } else {
            detailBinding.imageView4.visibility = View.GONE
        }

        detailBinding.progressBar1.visibility = View.VISIBLE
        if (query != null) {
            detailViewModel.getListFood(query).observe(this) {
                if (it.isEmpty()) {
                    detailBinding.fabOption1.visibility = View.GONE
                    saveIt = false
                }

                detailBinding.progressBar1.apply {
                    this.post {
                        this.visibility = View.GONE
                    }
                }

                detailAdapter.setData(it)
                detailAdapter.notifyDataSetChanged()
                arrayData.addAll(it)
                isProsesLoadData = false

                for (data in it) {
                    totalServing += data.servingSizeG.toFloat()
                    totalCalories += data.calories.toFloat()
                    totalCarbo += data.carbohydratesTotalG.toFloat()
                    totalProtein += data.proteinG.toFloat()
                    totalFat += data.fatTotalG.toFloat()
                    totalCholesterol += data.cholesterolMg.toFloat()
                }

                with(detailBinding) {
                    this.totalServing.post {
                        this.totalServing.text = resources.getString(
                            R.string.nutrition_placeholder_in_g,
                            String.format("%.1f", this@DetailActivity.totalServing)
                        )
                        this.totalServing.setOnClickListener {
                            this@DetailActivity.toastLong(getString(R.string.value_serving))
                        }
                    }
                    this.totalCalories.post {
                        this.totalCalories.text = resources.getString(
                            R.string.nutrition_placeholder_in_cal,
                            String.format("%.1f", this@DetailActivity.totalCalories)
                        )
                        this.totalCalories.setOnClickListener {
                            this@DetailActivity.toastLong(getString(R.string.value_calories))
                        }
                    }
                    this.totalCarbo.post {
                        this.totalCarbo.text = resources.getString(
                            R.string.nutrition_placeholder_in_g,
                            String.format("%.1f", this@DetailActivity.totalCarbo)
                        )
                        this.totalCarbo.setOnClickListener {
                            this@DetailActivity.toastLong(getString(R.string.value_carbo))
                        }
                    }
                    this.totalProtein.post {
                        this.totalProtein.text = resources.getString(
                            R.string.nutrition_placeholder_in_g,
                            String.format("%.1f", this@DetailActivity.totalProtein)
                        )
                        this.totalProtein.setOnClickListener {
                            this@DetailActivity.toastLong(getString(R.string.value_protein))
                        }
                    }
                    this.totalFat.post {
                        this.totalFat.text = resources.getString(
                            R.string.nutrition_placeholder_in_g,
                            String.format("%.1f", this@DetailActivity.totalFat)
                        )
                        this.totalFat.setOnClickListener {
                            this@DetailActivity.toastLong(getString(R.string.value_fat))
                        }
                    }
                    this.totalCholesterol.post {
                        this.totalCholesterol.text = resources.getString(
                            R.string.nutrition_placeholder_in_mg,
                            String.format("%.1f", this@DetailActivity.totalCholesterol)
                        )
                        this.totalCholesterol.setOnClickListener {
                            this@DetailActivity.toastLong(getString(R.string.value_cholesterol))
                        }
                    }
                }
            }
        }

        with(detailBinding.rvNutrition) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = detailAdapter
        }
    }

    private fun saveToHistory() {
        val foodDataDailyConsumptionItem = ArrayList<FoodDataDailyConsumptionItem>()
        val failedFoodData = ArrayList<FoodDataDailyConsumptionItem>()

        val userID = this.userPreference().getInt("user_id", 0)

        for (item in arrayData.indices) {
            if (arrayData[item].name == arrayMenuList[item].name) {
                foodDataDailyConsumptionItem.add(
                    FoodDataDailyConsumptionItem(
                        arrayData[item].name,
                        arrayData[item].fatTotalG.toFloat(),
                        arrayData[item].fiberG.toFloat(),
                        arrayMenuList[item].value,
                        arrayData[item].calories.toFloat(),
                        arrayData[item].fatSaturatedG.toFloat(),
                        arrayData[item].sodiumMg.toFloat(),
                        "",
                        userID,
                        arrayData[item].servingSizeG.toFloat(),
                        arrayData[item].proteinG.toFloat(),
                        arrayData[item].cholesterolMg.toFloat(),
                        "",
                        arrayData[item].carbohydratesTotalG.toFloat(),
                        arrayData[item].sugarG.toFloat(),
                        imageID
                    )
                )
            } else {
                failedFoodData.add(
                    FoodDataDailyConsumptionItem(
                        arrayData[item].name,
                        arrayData[item].fatTotalG.toFloat(),
                        arrayData[item].fiberG.toFloat(),
                        arrayMenuList[item].value,
                        arrayData[item].calories.toFloat(),
                        arrayData[item].fatSaturatedG.toFloat(),
                        arrayData[item].sodiumMg.toFloat(),
                        "",
                        userID,
                        arrayData[item].servingSizeG.toFloat(),
                        arrayData[item].proteinG.toFloat(),
                        arrayData[item].cholesterolMg.toFloat(),
                        "",
                        arrayData[item].carbohydratesTotalG.toFloat(),
                        arrayData[item].sugarG.toFloat(),
                        imageID
                    )
                )
            }
        }

        val token = this.userPreference().getString("token", "")

        NutrifitApiConfig.getNutrifitApiService(token).postHistory(
            foodDataDailyConsumptionItem
        ).enqueue(object : Callback<List<FoodDataDailyConsumptionItem>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<List<FoodDataDailyConsumptionItem>>,
                response: Response<List<FoodDataDailyConsumptionItem>>
            ) {
                this@DetailActivity.toastLong(getString(R.string.list_add))
            }

            override fun onFailure(
                call: Call<List<FoodDataDailyConsumptionItem>>, t: Throwable
            ) {
                this@DetailActivity.toast(t.message.toString())
            }
        })
    }

    override fun onBackPressed() {
        if (!isProsesLoadData) {
            this.areYouSure(getString(R.string.go_back_home)).apply {
                setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok)) { _, _ ->
                    if (saveIt) {
                        saveToHistory()
                        newDataUpdated = true
                    }
                    Intent().apply {
                        this.putExtra("isSuccess", newDataUpdated)
                        setResult(RESULT_OK, this)
                    }
                    arrayData.clear()
                    finish()
                }
                show()
            }
        } else {
            this.toast(getString(R.string.please_wait))
        }
    }
}
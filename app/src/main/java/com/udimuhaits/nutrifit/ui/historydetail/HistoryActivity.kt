package com.udimuhaits.nutrifit.ui.historydetail

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.databinding.ActivityHistoryBinding
import com.udimuhaits.nutrifit.ui.login.LoginViewModel
import com.udimuhaits.nutrifit.utils.*

class HistoryActivity : AppCompatActivity() {
    private lateinit var hisBind: ActivityHistoryBinding
    private val historyDetailViewModel: HistoryDetailViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var fAuth: FirebaseAuth
    private var dailyCalories = 0F
    private lateinit var barChart: BarChart
    private var totalServing = 0F
    private var totalCalories = 0F
    private var totalCarbo = 0F
    private var totalProtein = 0F
    private var totalFat = 0F
    private var totalCholesterol = 0F
    private var totalQuantity = 0

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hisBind = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(hisBind.root)

        fAuth = FirebaseAuth.getInstance()

        barChart = findViewById(R.id.chart)

        historyAdapter()
    }

    private fun historyAdapter() {
        dailyCalories = this.userPreference().getFloat("dailyCalories", 10F)

        val intentData = intent.extras?.getString("date")
        hisBind.title.text = resources.getString(
            R.string.string_you_have_eaten,
            if (intentData == getDate()) getString(R.string.today) else "${getString(R.string.on)} ${intentData?.stringToDate()},"
        )
        hisBind.title1.text = resources.getString(
            R.string.string_more_detail_with_your_food_journey_s,
            "${getString(R.string.on)} ${intentData?.stringToDate()}"
        )

        hisBind.subTitle1.text =
            resources.getString(
                R.string.string_your_daily_calories_is_s_kcal,
                dailyCalories.toString()
            )
        val historyDetailAdapter = HistoryDetailAdapter()

        val account = fAuth.currentUser
        val aUsername = account?.displayName
        val aEmail = account?.email
        val aProfilePic = account?.photoUrl

        loginViewModel.postUser(aUsername, aEmail, aProfilePic.toString()).observe(this, { users ->
            historyDetailViewModel.getHistoryDetail(users.userId, users.accessToken, intentData)
                .observe(this) {
                    historyDetailAdapter.setData(it)
                    historyDetailAdapter.notifyDataSetChanged()

                    for (data in it) {
                        totalServing += data.servingSize.toFloat()
                        totalCalories += data.calories.toFloat()
                        totalCarbo += data.carbonhydrates.toFloat()
                        totalProtein += data.protein.toFloat()
                        totalFat += data.totalFat.toFloat()
                        totalCholesterol += data.cholesterol.toFloat()
                        totalQuantity += data.quantity
                    }

                    val dailyCarbo = totalCarbo / dailyCalories * 100
                    val dailyProtein = totalProtein / dailyCalories * 100
                    val dailyFatTotal = totalFat / dailyCalories * 100

                    chartBar(dailyCarbo, dailyProtein, dailyFatTotal)

                    with(hisBind) {
                        this.tvServingTotal.post {
                            this.tvServingTotal.text = resources.getString(
                                R.string.nutrition_placeholder_in_g,
                                String.format("%.1f", totalServing)
                            )
                        }
                        this.tvCaloriesTotal.post {
                            this.tvCaloriesTotal.text = resources.getString(
                                R.string.nutrition_placeholder_in_cal,
                                String.format("%.1f", totalCalories)
                            )
                        }
                        this.tvCarboTotal.post {
                            this.tvCarboTotal.text = resources.getString(
                                R.string.nutrition_placeholder_in_g,
                                String.format("%.1f", totalCarbo)
                            )
                        }
                        this.tvProteinTotal.post {
                            this.tvProteinTotal.text = resources.getString(
                                R.string.nutrition_placeholder_in_g,
                                String.format("%.1f", totalProtein)
                            )
                        }
                        this.tvFatTotal.post {
                            this.tvFatTotal.text = resources.getString(
                                R.string.nutrition_placeholder_in_g,
                                String.format("%.1f", totalFat)
                            )
                        }
                        this.tvCholesterolTotal.post {
                            this.tvCholesterolTotal.text = resources.getString(
                                R.string.nutrition_placeholder_in_mg,
                                String.format("%.1f", totalCholesterol)
                            )
                        }
                        this.subTitle.post {
                            this.subTitle.text = resources.getString(
                                R.string.string_this_day_you_have_eat_about_s_foods_beverages_nwith_total_calories_is_scal,
                                totalQuantity.toString(),
                                String.format("%.1f", totalCalories)
                            )
                        }
                    }
                }

            with(hisBind.recyclerView) {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = historyDetailAdapter
            }
        })
    }

    private fun chartBar(dailyCarbo: Float, dailyProtein: Float, dailyFatTotal: Float) {
        barChart.description.isEnabled = false
        barChart.axisLeft.axisMaximum = 100f
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.spaceTop = 35f
        barChart.axisRight.isEnabled = false
        barChart.axisRight.isEnabled = false

        barChart.legend.apply {
            isEnabled = true
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(true)
        }

        val normal = ArrayList<BarEntry>()
        normal.add(BarEntry(0F, 50F))
        normal.add(BarEntry(1F, 20F))
        normal.add(BarEntry(2F, 30F))

        val dialy = ArrayList<BarEntry>()
        dialy.add(BarEntry(0F, dailyCarbo))
        dialy.add(BarEntry(1F, dailyProtein))
        dialy.add(BarEntry(2F, dailyFatTotal))

        val normalBarDataSet = BarDataSet(normal, "Normal")
        normalBarDataSet.color = resources.getColor(R.color.orange_nutrifit)

        val dailyBarDataSet = BarDataSet(dialy, "Daily")
        dailyBarDataSet.color = Color.BLUE

        val xAxisLabel: ArrayList<String> = arrayListOf("Carbo", "Protein", "Fat")

        barChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(xAxisLabel)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            isGranularityEnabled = true
            setCenterAxisLabels(true)
            setDrawGridLines(true)
            spaceMin = 0f
        }

        barChart.isDragEnabled = true
        barChart.setVisibleXRangeMaximum(3F)

        val barWidth = 0.4f
        val barSpace = 0.03f
        val groupSpace = 0.14f

        val groupBar = BarData(normalBarDataSet, dailyBarDataSet)

        groupBar.barWidth = barWidth
        barChart.data = groupBar

        barChart.xAxis.axisMaximum =
            (0 + barChart.barData.getGroupWidth(groupSpace, barSpace) * 3)

        barChart.groupBars(0f, groupSpace, barSpace)
        barChart.animateXY(100, 500)
    }
}

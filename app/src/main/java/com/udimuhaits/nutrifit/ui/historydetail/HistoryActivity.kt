package com.udimuhaits.nutrifit.ui.historydetail

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.databinding.ActivityHistoryBinding
import com.udimuhaits.nutrifit.utils.*


class HistoryActivity : AppCompatActivity() {
    private lateinit var hisBind: ActivityHistoryBinding
    private var harianCalories = 0F
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

        barChart = findViewById(R.id.chart)

        harianCalories = this.userPreference().getFloat("dailyCalories", 10F)

        val intentData = intent.extras?.getString("date")
        hisBind.title.text = resources.getString(
            R.string.string_you_have_eaten,
            if (intentData == getDate()) "Today" else "On ${intentData?.stringToDate()},"
        )
        hisBind.title1.text = resources.getString(
            R.string.string_more_detail_with_your_food_journey_s,
            "On ${intentData?.stringToDate()}"
        )

        hisBind.subTitle1.text =
            resources.getString(
                R.string.string_your_daily_calories_is_s_kcal,
                harianCalories.toString()
            )

        val historyDetailAdapter = HistoryDetailAdapter()

        val viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[HistoryDetailViewModel::class.java]

        viewModel.getHistoryDetail(this, intentData).observe(this) {
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

            val harianCarbo = totalCarbo / harianCalories * 100
            val harianProtein = totalProtein / harianCalories * 100
            val harianFatTotal = totalFat / harianCalories * 100

            chartBar(harianCarbo, harianProtein, harianFatTotal)

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

    }

    private fun chartBar(harianCarbo: Float, harianProtein: Float, harianFatTotal: Float) {

        this@HistoryActivity.toastLong("Carbo $harianCarbo")
        this@HistoryActivity.toastLong("Protein $harianProtein")
        this@HistoryActivity.toastLong("Fat $harianFatTotal")

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

        val harian = ArrayList<BarEntry>()
        harian.add(BarEntry(0F, harianCarbo))
        harian.add(BarEntry(1F, harianProtein))
        harian.add(BarEntry(2F, harianFatTotal))

        val normalBarDataSet = BarDataSet(normal, "Normal")
        normalBarDataSet.color = resources.getColor(R.color.orange_nutrifit)

        val harianBarDataSet = BarDataSet(harian, "Daily")
        harianBarDataSet.color = Color.BLUE

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

        val groupBar = BarData(normalBarDataSet, harianBarDataSet)

        groupBar.barWidth = barWidth
        barChart.data = groupBar

        barChart.xAxis.axisMaximum =
            (0 + barChart.barData.getGroupWidth(groupSpace, barSpace) * 3)

        barChart.groupBars(0f, groupSpace, barSpace)
        barChart.animateXY(100, 500)
    }
}

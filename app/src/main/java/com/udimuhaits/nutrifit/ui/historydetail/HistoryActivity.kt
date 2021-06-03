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
import com.udimuhaits.nutrifit.utils.getDate
import com.udimuhaits.nutrifit.utils.stringToDate
import com.udimuhaits.nutrifit.utils.toast


class HistoryActivity : AppCompatActivity() {
    private lateinit var hisBind: ActivityHistoryBinding
    private val harianCalories = 2000F

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hisBind = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(hisBind.root)

        val barChart = findViewById<BarChart>(R.id.chart)

        val intentData = intent.extras?.getString("date")
//        val intentData = "2021-06-02".stringToDate()
        hisBind.title.text = resources.getString(
            R.string.string_you_have_eaten,
            if (intentData == getDate()) "Today" else "On ${intentData?.stringToDate()},"
        )
        hisBind.title1.text = resources.getString(
            R.string.string_more_detail_with_your_food_journey_s,
            "On ${intentData?.stringToDate()}"
        )

        val historyDetailAdapter = HistoryDetailAdapter()

        val viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[HistoryDetailViewModel::class.java]

        viewModel.getHistoryDetail(this, intentData).observe(this) {
            Log.d("asdasd result", it.toString())
            this.toast("done")
            historyDetailAdapter.setData(it)
            historyDetailAdapter.notifyDataSetChanged()
        }

        with(hisBind.recyclerView) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = historyDetailAdapter
        }

        historyDetailAdapter.getTotalListener(object : HistoryDetailAdapter.InterfaceListener {
            override fun totalSendToDetail(
                totalServing: String,
                totalCalories: String,
                totalCarbo: String,
                totalProtein: String,
                totalFat: String,
                totalCholesterol: String,
                totalQuantity: String
            ) {
                with(hisBind) {
                    val harianCarbo = totalCarbo.toFloat() / harianCalories * 100
                    val harianProtein = totalProtein.toFloat() / harianCalories * 100
                    val harianFatTotal = totalFat.toFloat() / harianCalories * 100

//                    val harianCarbo = carbohydrate / harianCalories * 100
//                    val harianProtein = protein / harianCalories * 100
//                    val harianFatTotal = fat / harianCalories * 100

//                    this.toastLong("Carbo $harianCarbo")
//                    this.toastLong("Protein $harianProtein")
//                    this.toastLong("Fat $harianFatTotal")

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

//                    val normalBarDataSet = BarDataSet(normal, "Normal")
//                    val harianBarDataSet = BarDataSet(harian, "Daily")
//
//                    barChart.notifyDataSetChanged()
//                    barChart.data = BarData(normalBarDataSet, harianBarDataSet)
//                    barChart.invalidate()

                    this.tvServingTotal.post {
                        this.tvServingTotal.text = resources.getString(
                            R.string.nutrition_placeholder_in_g,
                            String.format("%.1f", totalServing.toFloat())
                        )
                    }
                    this.tvCaloriesTotal.post {
                        this.tvCaloriesTotal.text = resources.getString(
                            R.string.nutrition_placeholder_in_cal,
                            String.format("%.1f", totalCalories.toFloat())
                        )
                    }
                    this.tvCarboTotal.post {
                        this.tvCarboTotal.text = resources.getString(
                            R.string.nutrition_placeholder_in_g,
                            String.format("%.1f", totalCarbo.toFloat())
                        )
                    }
                    this.tvProteinTotal.post {
                        this.tvProteinTotal.text = resources.getString(
                            R.string.nutrition_placeholder_in_g,
                            String.format("%.1f", totalProtein.toFloat())
                        )
                    }
                    this.tvFatTotal.post {
                        this.tvFatTotal.text = resources.getString(
                            R.string.nutrition_placeholder_in_g,
                            String.format("%.1f", totalFat.toFloat())
                        )
                    }
                    this.tvCholesterolTotal.post {
                        this.tvCholesterolTotal.text = resources.getString(
                            R.string.nutrition_placeholder_in_mg,
                            String.format("%.1f", totalCholesterol.toFloat())
                        )
                    }
                    this.subTitle.post {
                        this.subTitle.text = resources.getString(
                            R.string.string_this_day_you_have_eat_about_s_foods_beverages_nwith_total_calories_is_scal,
                            totalQuantity,
                            String.format("%.1f", totalCalories.toFloat())
                        )
                    }
                }
            }
        })


        // dummy data
        // di bawah hasil perhitungan
        /*   val normalServingSize = 0
           // Kalori normal harian pria dewasa
           val normalCalories = 2500
           // Manusia membutuhkan karbohidrat sebanyak 45-65 persen dari total kalori yang didapatkan tiap hari.
           // perhitungan di bawah masih ambigu
           val normalCarbo = (50 / 100) * normalCalories
           val normalProtein = (20 / 100) * normalCalories
           val normalFatTotal = (30 / 100) * normalCalories
           // Rata-rata orang membutuhkan 1100 miligram kolesterol per hari. Pada kebanyakan orang, 70–75 persen asupan kolesterol biasanya diproduksi oleh organ hati, sedangkan sisanya didapat dari makanan yang dikonsumsi sehari-hari.
           val normalCholesterol = 1100 / 1000*/

        // dummy nutrisi harian


//        Log.i("asdasd", barChart.barData.getGroupWidth(groupSpace, barSpace).toString())

    }
}

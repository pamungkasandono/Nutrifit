package com.udimuhaits.nutrifit.ui.historydetail

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.data.ResponseJourneyItem
import com.udimuhaits.nutrifit.databinding.ItemListOnHistoryBinding

class HistoryDetailAdapter : RecyclerView.Adapter<HistoryDetailAdapter.DetailViewHolder>() {
    private var mResponseJourneyItem = ArrayList<ResponseJourneyItem>()
    private lateinit var totalListener: InterfaceListener
    private var totalServing = 0F
    private var totalCalories = 0F
    private var totalCarbo = 0F
    private var totalProtein = 0F
    private var totalFat = 0F
    private var totalCholesterol = 0F
    private var totalQuantity = 0
    private var testCount = 0

    fun setData(list: List<ResponseJourneyItem>?) {
        if (list == null) return
        this.mResponseJourneyItem.clear()
        Log.d("history data", list.toString())
        this.mResponseJourneyItem.addAll(list)
    }

    fun getTotalListener(interfaceListener: InterfaceListener) {
        this.totalListener = interfaceListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): DetailViewHolder {
        val itemListBinding =
            ItemListOnHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(itemListBinding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(mResponseJourneyItem[position])
    }

    override fun getItemCount(): Int = mResponseJourneyItem.size

    inner class DetailViewHolder(private val binding: ItemListOnHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(journey: ResponseJourneyItem) {
            with(binding) {
                if (adapterPosition % 2 == 0) {
                    view.setBackgroundColor(itemView.context.resources.getColor(R.color.blue_transparent))
                } else {
                    view.setBackgroundColor(itemView.context.resources.getColor(R.color.white))
                }
                foodName.text = journey.foodName
                howMuch.text = "${journey.quantity}x"
                eatTime.text = journey.timeFoodConsumed.substring(11, 16)

                totalServing += journey.servingSize.toFloat()
                totalCalories += journey.calories.toFloat()
                totalCarbo += journey.carbonhydrates.toFloat()
                totalProtein += journey.protein.toFloat()
                totalFat += journey.totalFat.toFloat()
                totalCholesterol += journey.cholesterol.toFloat()
                totalQuantity += journey.quantity
                testCount++
                if ((adapterPosition + 1) == mResponseJourneyItem.size) {
                    Log.d("asdasd", "process done awal 0 akhir ${testCount++}")
                    totalListener.totalSendToDetail(
                        totalServing.toString(),
                        totalCalories.toString(),
                        totalCarbo.toString(),
                        totalProtein.toString(),
                        totalFat.toString(),
                        totalCholesterol.toString(),
                        totalQuantity.toString()
                    )
                    totalServing = 0F
                    totalCalories = 0F
                    totalCarbo = 0F
                    totalProtein = 0F
                    totalFat = 0F
                    totalCholesterol = 0F
                    totalQuantity = 0
                }
            }
        }
    }

    interface InterfaceListener {
        fun totalSendToDetail(
            totalServing: String,
            totalCalories: String,
            totalCarbo: String,
            totalProtein: String,
            totalFat: String,
            totalCholesterol: String,
            totalQuantity: String
        )
    }
}
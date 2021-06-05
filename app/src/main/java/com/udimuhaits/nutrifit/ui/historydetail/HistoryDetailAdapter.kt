package com.udimuhaits.nutrifit.ui.historydetail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.data.ResponseJourneyItem
import com.udimuhaits.nutrifit.databinding.ItemListOnHistoryBinding

class HistoryDetailAdapter : RecyclerView.Adapter<HistoryDetailAdapter.DetailViewHolder>() {
    private var mResponseJourneyItem = ArrayList<ResponseJourneyItem>()

    fun setData(list: List<ResponseJourneyItem>?) {
        if (list == null) return
        this.mResponseJourneyItem.clear()
        this.mResponseJourneyItem.addAll(list)
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
            }
        }
    }
}
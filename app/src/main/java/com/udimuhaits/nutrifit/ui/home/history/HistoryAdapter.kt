package com.udimuhaits.nutrifit.ui.home.history

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.data.ItemHistoryEntity
import com.udimuhaits.nutrifit.databinding.ItemHistoryBinding
import com.udimuhaits.nutrifit.ui.historydetail.HistoryActivity
import com.udimuhaits.nutrifit.utils.getDate


class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    private var mHistoryResponse = ArrayList<ItemHistoryEntity>()
    private var testCount = 0
//    private lateinit var clickListener: InterfaceListener

    fun setData(list: List<ItemHistoryEntity>?) {
        if (list == null) return
        this.mHistoryResponse.clear()
        Log.d("asdasd History", list.toString())
        this.mHistoryResponse.addAll(list)
    }

//    fun getClickListener(interfaceListener: InterfaceListener) {
////        this.clickListener = interfaceListener
//    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryViewHolder {
        val itemListBinding =
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(itemListBinding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(mHistoryResponse[position])
    }

    override fun getItemCount(): Int = mHistoryResponse.size

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(HR: ItemHistoryEntity) {
            with(binding) {
                Log.i("asdasd got history", HR.toString())
//                if (HR.dateTimeConsumed == dateTemp) {
//                    historyTitleItem.append("$comma${HR.foodName}")
//                    comma = ", "
//                } else {
//                    dateTemp = HR.dateTimeConsumed
//                    historyTitleItem.text = HR.foodName
//                }
                when {
                    getDate() == HR.date -> {
                        textViewToday.visibility = View.VISIBLE
                        textViewYesterday.visibility = View.VISIBLE
                    }
                    adapterPosition == 0 -> {
                        textViewToday.visibility = View.VISIBLE
                        textViewYesterday2.visibility = View.VISIBLE
                        imageView2.visibility = View.VISIBLE
                    }
                    else -> {
                        imageView2.visibility = View.GONE
                        textViewToday.visibility = View.GONE
                        textViewYesterday.visibility = View.GONE
                        textViewYesterday2.visibility = View.GONE
                    }
                }

                imageView.setOnClickListener {
                    // clickListener.onClicked(adapterPosition, HR.date)
                    Intent(itemView.context, HistoryActivity::class.java).apply {
                        this.putExtra("date", HR.date)
                        itemView.context.startActivity(this)
                    }
                }

                if (mHistoryResponse.size == 1) {
                    textViewYesterday.visibility = View.GONE
                }

                historyTitleItem.text = HR.foodName
                historyDate.text = HR.date
                Glide.with(binding.root.context)
                    .load(HR.imagePath)
                    .error(R.drawable.img_no_avaliable)
                    .into(imageView)

                if ((adapterPosition + 1) == mHistoryResponse.size) {
                    Log.d("asdasd", "process done awal 0 akhir ${testCount++}")
                }
            }
        }
    }

    interface InterfaceListener {
        fun onClicked(position: Int, date: String)
    }
}
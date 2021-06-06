package com.udimuhaits.nutrifit.ui.detail

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.tomergoldst.tooltips.ToolTip
import com.tomergoldst.tooltips.ToolTipsManager
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.data.CNEntity
import com.udimuhaits.nutrifit.databinding.ItemListFoodBinding

class DetailAdapter : RecyclerView.Adapter<DetailAdapter.DetailViewHolder>() {
    private var mCNEntity = ArrayList<CNEntity>()

    fun setData(list: List<CNEntity>?) {
        if (list == null) return
        this.mCNEntity.clear()
        this.mCNEntity.addAll(list)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailViewHolder {
        val itemListBinding =
            ItemListFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(itemListBinding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(mCNEntity[position])
    }

    override fun getItemCount(): Int = mCNEntity.size

    inner class DetailViewHolder(private val binding: ItemListFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var tooltipManager: ToolTipsManager
        fun bind(CN: CNEntity) {
            with(binding) {
                foodTitle.text = CN.name
                tvDataServingSize.text =
                    root.resources.getString(R.string.nutrition_placeholder_in_g, CN.servingSizeG)
                tvDataCalories.text =
                    root.resources.getString(R.string.nutrition_placeholder_in_cal, CN.calories)
                tvDataCarbo.text =
                    root.resources.getString(
                        R.string.nutrition_placeholder_in_g, CN.carbohydratesTotalG
                    )
                tvDataProtein.text =
                    root.resources.getString(R.string.nutrition_placeholder_in_g, CN.proteinG)
                tvDataFatTotal.text =
                    root.resources.getString(R.string.nutrition_placeholder_in_g, CN.fatTotalG)
                tvDataCholesterol.text =
                    root.resources.getString(R.string.nutrition_placeholder_in_mg, CN.cholesterolMg)

                tooltipManager = ToolTipsManager()

                btnInfoServing.setOnClickListener {
                    val viewAnchor = btnInfoServing
                    val msg = itemView.context.getString(R.string.tooltip_calories)
                    displayTooltip(viewAnchor, msg)
                }
                btnInfoCalories.setOnClickListener {
                    val viewAnchor = btnInfoCalories
                    val msg = itemView.context.getString(R.string.tooltip_calories)
                    displayTooltip(viewAnchor, msg)
                }
                btnInfoProtein.setOnClickListener {
                    val viewAnchor = btnInfoProtein
                    val msg = itemView.context.getString(R.string.tooltip_protein)
                    displayTooltip(viewAnchor, msg)
                }
                btnInfoCarbo.setOnClickListener {
                    val viewAnchor = btnInfoCarbo
                    val msg = itemView.context.getString(R.string.tooltip_carbo)
                    displayTooltip(viewAnchor, msg)
                }
                btnInfoFat.setOnClickListener {
                    val viewAnchor = btnInfoFat
                    val msg = itemView.context.getString(R.string.tooltip_fat)
                    displayTooltip(viewAnchor, msg)
                }
                btnInfoCholesterol.setOnClickListener {
                    val viewAnchor = btnInfoCholesterol
                    val msg = itemView.context.getString(R.string.tooltip_cholesterol)
                    displayTooltip(viewAnchor, msg)
                }
            }
        }

        private fun displayTooltip(imageAnchor: ImageView, msg: String) {
            tooltipManager.dismissAll()
            val position = ToolTip.POSITION_RIGHT_TO
            ToolTip.Builder(
                binding.root.context, imageAnchor, binding.constraintLayout, msg, position
            )
                .apply {
                    setAlign(ToolTip.ALIGN_CENTER)
                    setBackgroundColor(Color.BLACK)
                    tooltipManager.show(this.build())
                }
        }
    }
}
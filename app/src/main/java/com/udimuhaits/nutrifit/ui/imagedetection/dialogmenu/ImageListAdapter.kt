package com.udimuhaits.nutrifit.ui.imagedetection.dialogmenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.data.MenuListEntity
import com.udimuhaits.nutrifit.databinding.ItemMenuImageBinding
import com.udimuhaits.nutrifit.utils.toast

class ImageListAdapter : RecyclerView.Adapter<ImageListAdapter.ImageListViewHolder>() {
    private val mData = ArrayList<MenuListEntity>()
    private lateinit var changeListener: InterfaceListener
    private lateinit var checkedListener: InterfaceListener
    private var itemCheckedCount = 0

    fun setData(item: ArrayList<MenuListEntity>) {
        mData.clear()
        mData.addAll(item)
    }

    fun setOnDataChangeListener(interfaceListener: InterfaceListener) {
        this.changeListener = interfaceListener
    }

    fun getCheckedState(interfaceListener: InterfaceListener) {
        this.checkedListener = interfaceListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewHolder {
        val itemBinding =
            ItemMenuImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageListViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class ImageListViewHolder(private val itemBinding: ItemMenuImageBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(popupEntity: MenuListEntity) {
            with(itemBinding) {
                textView5.text = popupEntity.value.toString()

                checkBox2.apply {
                    this.text = popupEntity.name
                    this.isChecked = popupEntity.isChecked

                    if (this.isChecked) {
                        itemCheckedCount += 1
                    }
                }

                if (itemCheckedCount == mData.size) {
                    checkedListener.onAllChecked(true)
                } else {
                    checkedListener.onAllChecked(false)
                }

                checkBox2.setOnClickListener {
                    changeListener.onSomeDataClicked(
                        adapterPosition, popupEntity.name, popupEntity.value, checkBox2.isChecked
                    )
                }

                btnIncrease.setOnClickListener {
                    val newValue = popupEntity.value + 1
                    if (newValue > 10) {
                        root.context.toast(itemView.context.getString(R.string.max_portion))
                    } else {
                        changeListener.onSomeDataClicked(
                            adapterPosition, popupEntity.name, newValue, popupEntity.isChecked
                        )
                    }
                }

                btnDecrease.setOnClickListener {
                    val newValue = popupEntity.value - 1
                    if (newValue < 1) {
                        root.context.toast(itemView.context.getString(R.string.min_portion))
                    } else {
                        changeListener.onSomeDataClicked(
                            adapterPosition, popupEntity.name, newValue, popupEntity.isChecked
                        )
                    }
                }
            }
        }
    }

    interface InterfaceListener {
        fun onSomeDataClicked(position: Int, name: String, newValue: Int, isChecked: Boolean) {}
        fun onAllChecked(state: Boolean) {}
    }
}

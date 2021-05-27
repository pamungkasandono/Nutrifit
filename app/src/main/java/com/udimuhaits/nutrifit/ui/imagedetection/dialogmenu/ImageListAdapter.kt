package com.udimuhaits.nutrifit.ui.imagedetection.dialogmenu

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udimuhaits.nutrifit.databinding.ItemMenuImageBinding
import com.udimuhaits.nutrifit.utils.toast

class ImageListAdapter : RecyclerView.Adapter<ImageListAdapter.ImageListViewHolder>() {
    private val mData = ArrayList<ImageMenuListData>()
    private lateinit var changeListener: InterfaceListener
    private lateinit var checkedListener: InterfaceListener
    private var itemCheckedCount = 0

    fun setData(item: ArrayList<ImageMenuListData>) {
        mData.clear()
        mData.addAll(item)
        Log.d("asdasd", mData.toString())
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
        fun bind(popupData: ImageMenuListData) {
            with(itemBinding) {
                textView5.text = popupData.value.toString()

                checkBox2.apply {
                    this.text = popupData.name
                    this.isChecked = popupData.isChecked

                    if (this.isChecked) {
                        itemCheckedCount += 1
                    }
                }

                if (itemCheckedCount == mData.size) {
                    // jika per item ter ceklis semua
                    checkedListener.onAllChecked(true)
//                    Toast.makeText(root.context, "All item checked manually", Toast.LENGTH_SHORT).show()
                } else {
                    checkedListener.onAllChecked(false)
//                    Toast.makeText(root.context, "Some item not checked", Toast.LENGTH_SHORT).show()
                }

                // jika proses sudah selesai maka set itemCheckedCount ke 0
                if ((adapterPosition + 1) == mData.size) {
                    itemCheckedCount = 0
                    Log.d("asdasd", "process done")
                }

                checkBox2.setOnClickListener {
                    changeListener.onSomeDataClicked(
                        adapterPosition, popupData.name, popupData.value, checkBox2.isChecked
                    )
                }

                btnIncrease.setOnClickListener {
                    val newValue = popupData.value + 1
                    if (newValue > 10) {
                        root.context.toast("maximum porsi")
                    } else {
                        changeListener.onSomeDataClicked(
                            adapterPosition, popupData.name, newValue, popupData.isChecked
                        )
                    }
                }

                btnDecrease.setOnClickListener {
                    val newValue = popupData.value - 1
                    if (newValue < 1) {
                        root.context.toast("minimum portion is 1")
                    } else {
                        changeListener.onSomeDataClicked(
                            adapterPosition, popupData.name, newValue, popupData.isChecked
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

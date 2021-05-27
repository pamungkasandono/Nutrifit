package com.udimuhaits.nutrifit.ui.home.dialogmenu

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.udimuhaits.nutrifit.databinding.ItemMenuManualBinding
import com.udimuhaits.nutrifit.utils.toast

class DialogManualAdapter() : RecyclerView.Adapter<DialogManualAdapter.PopupViewHolder>() {
    private val mData = ArrayList<ListManualEntity>()
    private lateinit var deleteListener: InterfaceListener
    private lateinit var dataChangeListener: InterfaceListener

    fun setData(item: ArrayList<ListManualEntity>) {
        mData.clear()
        mData.addAll(item)
    }

    fun setOnDataChangeListener(interfaceListener: InterfaceListener) {
        this.dataChangeListener = interfaceListener
    }

    fun setOnDeleteListener(interfaceListener: InterfaceListener) {
        this.deleteListener = interfaceListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PopupViewHolder {
        val itemBinding =
            ItemMenuManualBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PopupViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PopupViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class PopupViewHolder(private val itemBinding: ItemMenuManualBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(listManualEntity: ListManualEntity) {
            with(itemBinding) {
                textView.text = listManualEntity.name
                textView2.text = listManualEntity.value.toString()

                imageButton.setOnClickListener {
                    // mData.removeAt(adapterPosition) /* jangan ubah data dari adapter, ubah data dari sumber */
                    // notifyDataSetChanged()
                    deleteListener.onDeleteClick(adapterPosition)
                }

                imageButton2.setOnClickListener {
                    val increaseValue = listManualEntity.value + 1
                    if (increaseValue > 10) {
                        root.context.toast("maximum porsi")
                    } else {
                        dataChangeListener.onValueChange(
                            adapterPosition, listManualEntity.name, increaseValue
                        )
                    }
                }

                imageButton3.setOnClickListener {
                    val decreaseValue = listManualEntity.value - 1
                    if (decreaseValue < 1) {
                        root.context.toast("minimum portion is 1")
                    } else {
                        dataChangeListener.onValueChange(
                            adapterPosition, listManualEntity.name, decreaseValue
                        )
                    }
                }
            }
        }
    }

    interface InterfaceListener {
        fun onValueChange(position: Int, name: String, newValue: Int) {}
        fun onDeleteClick(position: Int) {}
    }
}
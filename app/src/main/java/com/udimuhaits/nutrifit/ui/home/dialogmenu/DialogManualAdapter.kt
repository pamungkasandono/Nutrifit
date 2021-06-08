package com.udimuhaits.nutrifit.ui.home.dialogmenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.data.MenuListEntity
import com.udimuhaits.nutrifit.databinding.ItemMenuManualBinding
import com.udimuhaits.nutrifit.utils.toast

class DialogManualAdapter : RecyclerView.Adapter<DialogManualAdapter.PopupViewHolder>() {
    private val mData = ArrayList<MenuListEntity>()
    private lateinit var deleteListener: InterfaceListener
    private lateinit var dataChangeListener: InterfaceListener

    fun setData(item: ArrayList<MenuListEntity>) {
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
        fun bind(listManualEntity: MenuListEntity) {
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
                        root.context.toast(itemView.context.getString(R.string.max_portion))
                    } else {
                        dataChangeListener.onValueChange(
                            adapterPosition, listManualEntity.name, increaseValue
                        )
                    }
                }

                imageButton3.setOnClickListener {
                    val decreaseValue = listManualEntity.value - 1
                    if (decreaseValue < 1) {
                        root.context.toast(itemView.context.getString(R.string.min_portion))
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
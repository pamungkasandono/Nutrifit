package com.udimuhaits.nutrifit.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.databinding.ActivityHomeBinding
import com.udimuhaits.nutrifit.databinding.DialogMenuManualBinding
import com.udimuhaits.nutrifit.ui.detail.DetailActivity
import com.udimuhaits.nutrifit.ui.home.dialogmenu.DialogManualAdapter
import com.udimuhaits.nutrifit.ui.home.dialogmenu.ListManualEntity
import com.udimuhaits.nutrifit.utils.Global.forcePortrait

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityHomeBinding
    private var isBackPressed = false

    private val arrayListManual = ArrayList<ListManualEntity>()
    private val limitTotalMenu = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var asdd: List<String> = listOf()

        val imageUser = intent.getStringExtra("imageProfile")
        Glide.with(this)
            .load(imageUser)
            .into(binding.imgProfile)

        // perubahan
        forcePortrait(this)

        var visibilityButtonState = true
        binding.imgProfile.setOnClickListener {
            if (visibilityButtonState) {
                visibilityButtonState = !visibilityButtonState
                binding.button.visibility = View.VISIBLE
                binding.button1.visibility = View.VISIBLE
            } else {
                visibilityButtonState = !visibilityButtonState
                binding.button.visibility = View.GONE
                binding.button1.visibility = View.GONE
            }
        }

//        binding.searchBox.setText("1 serving of Pizza 1 serving of Water 1 serving of Noodles 1 serving of Cake 1 serving of Meat balls")
//        binding.searchBox.setText("Pizza Water Noodles Cake Ramen")

        binding.btnSearch.setOnClickListener {
//            val serving = " 1 serving of "
//            val searchData = binding.searchBox.text
//            val dataQuery = searchData.toString()
//            asdd = dataQuery.split(" ")
//            var mDataQuery = ""
//            asdd.forEach {
//                mDataQuery += serving + it
//            }
//
//            Log.d("asdasd home", mDataQuery)
//

            manualDialog(arrayListManual)
        }

        binding.searchBox.setOnClickListener {
            Toast.makeText(this, "Search box", Toast.LENGTH_SHORT).show()
            manualDialog(arrayListManual)
        }

        //./ end of perubahan
    }

    @SuppressLint("SetTextI18n")
    private lateinit var menuManualBinding: DialogMenuManualBinding
    private val dialogManualAdapter = DialogManualAdapter()
    private lateinit var dialog: AlertDialog

    private fun manualDialog(arrayManualList: ArrayList<ListManualEntity>) {
        val dialogBuilder = AlertDialog.Builder(this)
        menuManualBinding = DialogMenuManualBinding.inflate(LayoutInflater.from(this))

        menuManualBinding.recyclerView2.layoutManager = LinearLayoutManager(this)
        menuManualBinding.recyclerView2.adapter = dialogManualAdapter

        dialogBuilder.setView(menuManualBinding.root)
        dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setTitle(resources.getString(R.string.text_sudah_makan_apa_hari_ini))
        dialog.show()

        // handling kalau arraynya kosong
        if (arrayManualList.size >= 1) {
            dialogManualAdapter.setData(arrayManualList)
            dialogManualAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "No data yet.", Toast.LENGTH_SHORT).show()
        }

        // set disable state
        setDisable()

        // button check to add new item
        menuManualBinding.btnAddNewItem.setOnClickListener(this)

        // button red minus to close the adding new item box section
        menuManualBinding.btnCancelAdding.setOnClickListener(this)

        // prevent keyboard from hiding automatically
        menuManualBinding.inputItemName.setOnEditorActionListener { _, _, _ ->
            if (setDisable()) {
                addNewItem()
            }
            true
        }

        // button to save the data to convert from array to string
        menuManualBinding.btnSaveData.setOnClickListener(this)

        // listener on delete array and update the data list
        dialogManualAdapter.setOnDeleteListener(object : DialogManualAdapter.InterfaceListener {
            override fun onDeleteClick(position: Int) {
                if (arrayManualList.size >= limitTotalMenu) {
                    Toast.makeText(this@HomeActivity, "now max", Toast.LENGTH_SHORT).show()
                    menuManualBinding.inputMenuSection.apply {
                        this.visibility = View.VISIBLE
                        this.animate().alpha(1f).duration = 200
                    }
                }
                Toast.makeText(this@HomeActivity, "now max tp boong", Toast.LENGTH_SHORT).show()
                arrayManualList.removeAt(position)
                dialogManualAdapter.setData(arrayManualList)
                dialogManualAdapter.notifyDataSetChanged()
            }
        })

        // listener to increase the value and update the data list
        dialogManualAdapter.setOnDataChangeListener(object : DialogManualAdapter.InterfaceListener {
            override fun onValueChange(position: Int, name: String, newValue: Int) {
                arrayManualList[position] = ListManualEntity(name, newValue)
                dialogManualAdapter.setData(arrayManualList)
                dialogManualAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_cancel_adding -> {
                menuManualBinding.inputItemName.apply {
                    this.setText("")
                    this.requestFocus()
                }
            }
            R.id.btn_add_new_item -> addNewItem()
            R.id.btn_save_data -> {
                // function to convert array to string
                val result = arrayManualToString()
                when {
                    result.isNotEmpty() -> {
                        Intent(this, DetailActivity::class.java).apply {
                            putExtra(DetailActivity.QUERY, result)
                            startActivity(this)
                        }
                    }
                    else -> Toast.makeText(
                        this, "You haven't eat anything yet?", Toast.LENGTH_SHORT
                    ).show()
                }
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()

                dialog.dismiss()
                Toast.makeText(this, arrayListManual.size.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var handle: Boolean = false

    private fun setDisable(): Boolean {
        // set default
        menuManualBinding.btnAddNewItem.apply {
            this.setColorFilter(Color.GRAY)
            this.isEnabled = false
        }

        menuManualBinding.inputItemName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (s.length >= 2) {
                    menuManualBinding.btnAddNewItem.apply {
                        this.post {
                            this.clearColorFilter()
                            this.isEnabled = true
                        }
                    }
                    menuManualBinding.inputItemName.apply {
                        this.post {
                            this.backgroundTintList =
                                ColorStateList.valueOf(Color.rgb(76, 239, 155))
                        }
                    }
                    handle = true
                } else {
                    menuManualBinding.btnAddNewItem.apply {
                        this.post {
                            this.setColorFilter(Color.GRAY)
                            this.isEnabled = false
                        }
                    }
                    menuManualBinding.inputItemName.apply {
                        this.post {
                            this.backgroundTintList =
                                ColorStateList.valueOf(Color.GRAY)
                        }
                    }
                    handle = false
                }
            }
        })
        return handle
    }

    private fun addNewItem(): Boolean {
        val text = menuManualBinding.inputItemName.text
        var check = false
        for (matchName in arrayListManual) {
            if (matchName.name == text.toString()) {
                check = true
                break
            }
        }
        if (check) {
            Toast.makeText(this@HomeActivity, "Menu $text sudah ada.", Toast.LENGTH_SHORT)
                .show()
            return false
        } else {
            arrayListManual.add(ListManualEntity(text.toString(), 1))
            dialogManualAdapter.setData(arrayListManual)
            dialogManualAdapter.notifyDataSetChanged()
            menuManualBinding.inputItemName.apply {
                this.setText("")
                this.requestFocus()
            }
        }
        if (arrayListManual.size >= limitTotalMenu) {
            menuManualBinding.inputMenuSection.apply {
                this.visibility = View.GONE
                this.animate().alpha(0f).duration = 200
            }
            Toast.makeText(this, "maximum menu reached", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun arrayManualToString(): String {
        var text = ""
        for (data in arrayListManual) {
            text += "${data.value} serving of ${data.name} "
        }
        return text
    }

    override fun onBackPressed() {
        if (isBackPressed) {
            super.onBackPressed()
        }

        isBackPressed = true
        Toast.makeText(this, "Tekan sekali lagi untuk kembali", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ isBackPressed = false }, 2000)
    }
}
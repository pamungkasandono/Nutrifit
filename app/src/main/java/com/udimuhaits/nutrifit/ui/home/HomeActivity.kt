package com.udimuhaits.nutrifit.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.databinding.ActivityHomeBinding
import com.udimuhaits.nutrifit.databinding.DialogChooseImageBinding
import com.udimuhaits.nutrifit.databinding.DialogMenuManualBinding
import com.udimuhaits.nutrifit.ui.detail.DetailActivity
import com.udimuhaits.nutrifit.ui.home.dialogmenu.DialogManualAdapter
import com.udimuhaits.nutrifit.ui.home.dialogmenu.ListManualEntity
import com.udimuhaits.nutrifit.ui.imagedetection.ImageDetection
import com.udimuhaits.nutrifit.utils.forcePortrait
import com.udimuhaits.nutrifit.utils.toast
import com.udimuhaits.nutrifit.utils.toastLong
import com.udimuhaits.nutrifit.utils.writeIsGranted

@SuppressLint("SetTextI18n")
class HomeActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityHomeBinding
    private var isBackPressed = false
    private val arrayListManual = ArrayList<ListManualEntity>()
    private val limitTotalMenu = 15
    private lateinit var menuManualBinding: DialogMenuManualBinding
    private val dialogManualAdapter = DialogManualAdapter()
    private lateinit var dialog: AlertDialog
    private var setDisabledState: Boolean = false

    companion object {
        const val FROM_DETAIL = 100
        const val FROM_IMAGE_DETECTION = 200
        const val PICK_IMAGE = 201
        const val TAKE_PICTURE = 202
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (this.writeIsGranted()) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 10
            )
/*
            Snackbar.make(
                uploadBinding.root, "Camera not have permission", Snackbar.LENGTH_INDEFINITE
            )
                .setAction("How") {
                    Toast.makeText(
                        this,
                        "Anda perlu buka perngaturan -> aplikasi -> Nutrifit -> Izin -> Penyimpanan",
                        Toast.LENGTH_LONG
                    ).show()
                }.show()
*/
        }

        arrayListManual.add(ListManualEntity("cake", 2))
        arrayListManual.add(ListManualEntity("rice", 1))
        arrayListManual.add(ListManualEntity("fries", 1))
        val imageUser = intent.getStringExtra("imageProfile")
        Glide.with(this)
            .load(imageUser)
            .into(binding.imgProfile)

        // fix portrait
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

        binding.btnSearch.setOnClickListener {
            manualDialog(arrayListManual)
        }

        binding.searchBox.setOnClickListener {
            this.toast("Search box")
            manualDialog(arrayListManual)
        }

        binding.selectImage.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this).create()
            alertDialog.setTitle("Choose your picture from")
            val dialogImageOptionsBinding =
                DialogChooseImageBinding.inflate(LayoutInflater.from(this))
            alertDialog.setView(dialogImageOptionsBinding.root)

            dialogImageOptionsBinding.selectGallery.setOnClickListener {
                if (this.writeIsGranted()) {
                    // Permission is not granted
                    this.toastLong("To use this feature you have to grant the permission!")
                } else {
                    Intent(this, ImageDetection::class.java).apply {
                        this.putExtra("youChoose", PICK_IMAGE)
                        startActivityForResult(this, FROM_IMAGE_DETECTION)
                    }
                }
                alertDialog.dismiss()
            }

            dialogImageOptionsBinding.selectCamera.setOnClickListener {
                if (this.writeIsGranted()) {
                    // Permission is not granted
                    this.toastLong("To use this feature you have to grant the permission!")
                } else {
                    Intent(this, ImageDetection::class.java).apply {
                        this.putExtra("youChoose", TAKE_PICTURE)
                        startActivityForResult(this, FROM_IMAGE_DETECTION)
                    }
                }
                alertDialog.dismiss()
            }

            alertDialog.show()

        }
        //./ end of perubahan
    }

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
            this.toast("No data yet.")
        }

        // cek jika array penuh
        if (arrayListManual.size >= limitTotalMenu) {
            menuManualBinding.inputMenuSection.apply {
                this.visibility = View.INVISIBLE
                this.animate().alpha(0f).duration = 200
            }
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
                    this@HomeActivity.toast("maximum reached")
                    menuManualBinding.inputMenuSection.apply {
                        this.visibility = View.VISIBLE
                        this.animate().alpha(1f).duration = 200
                    }
                }
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
                            putExtra(DetailActivity.WITH_IMAGE, false)
                            startActivityForResult(this, FROM_DETAIL)
                        }
                    }
                    else -> this.toast("You haven't eat anything yet?")
                }
                dialog.dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Log.d("asdasd requestcode", requestCode.toString())
            when (requestCode) {
                FROM_DETAIL -> {
                    if (data?.getBooleanExtra("isSuccess", false) == true) {
                        this.toast("success")
                        arrayListManual.clear()
                        dialogManualAdapter.setData(arrayListManual)
                        dialogManualAdapter.notifyDataSetChanged()
                    }
                }
                FROM_IMAGE_DETECTION -> {
                    val status = data?.getBooleanExtra("isSuccess", false)
                    this.toast("from image $status")
                }
            }
        }
    }

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
                    setDisabledState = true
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
                    setDisabledState = false
                }
            }
        })
        return setDisabledState
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
            this.toast("Menu $text sudah ada.")
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
                this.visibility = View.INVISIBLE
                this.animate().alpha(0f).duration = 200
            }
            this.toast("maximum menu reached")
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
        this.toast("Tekan sekali lagi untuk kembali")
        Handler().postDelayed({ isBackPressed = false }, 2000)
    }
}



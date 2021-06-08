package com.udimuhaits.nutrifit.ui.imagedetection

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.data.MenuListEntity
import com.udimuhaits.nutrifit.data.ResponseImageML
import com.udimuhaits.nutrifit.databinding.ActivityImageDetectionBinding
import com.udimuhaits.nutrifit.databinding.DialogMenuImageBinding
import com.udimuhaits.nutrifit.network.NutrifitApiConfig
import com.udimuhaits.nutrifit.ui.detail.DetailActivity
import com.udimuhaits.nutrifit.ui.home.HomeActivity
import com.udimuhaits.nutrifit.ui.home.dialogmenu.DialogManualAdapter
import com.udimuhaits.nutrifit.ui.imagedetection.dialogmenu.ImageListAdapter
import com.udimuhaits.nutrifit.ui.login.LoginViewModel
import com.udimuhaits.nutrifit.utils.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("SetTextI18n")
class ImageDetection : AppCompatActivity(), UploadRequestBody.UploadCallback, View.OnClickListener {
    private lateinit var imageBinding: ActivityImageDetectionBinding
    private var isTakePicture = false
    private var imageMenuList = ArrayList<MenuListEntity>()
    private var manualMenuList = ArrayList<MenuListEntity>()
    private var arrayMenuList = ArrayList<MenuListEntity>()
    private lateinit var menuImageBinding: DialogMenuImageBinding
    private val imageListAdapter = ImageListAdapter()
    private val popupAdapter = DialogManualAdapter()
    private lateinit var imageMenuDialog: androidx.appcompat.app.AlertDialog
    private val limitMenuItem = 15
    private lateinit var menuData: ArrayList<MenuListEntity>
    private lateinit var imagePath: String
    private var imageID: String? = null
    private val arrTempName = arrayListOf<String>()
    private val alreadyData = arrayListOf<String>()
    private lateinit var alertDialog: AlertDialog
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var fAuth: FirebaseAuth
    private var historySaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageBinding = ActivityImageDetectionBinding.inflate(layoutInflater)
        setContentView(imageBinding.root)

        fAuth = FirebaseAuth.getInstance()

        forcePortrait(this)

        alertDialog = AlertDialog.Builder(this).create()

        when (intent.extras?.getInt("youChoose")) {
            HomeActivity.PICK_IMAGE -> {
                this.toast(getString(R.string.pick))
                imageBinding.retakeButton.text = getString(R.string.change_image)
                imageBinding.title.text = getString(R.string.choose_image)
                isTakePicture = false
                chooseImage()
            }
            HomeActivity.TAKE_PICTURE -> {
                this.toast(getString(R.string.take))
                imageBinding.retakeButton.text = getString(R.string.retake_picture)
                imageBinding.title.text = getString(R.string.taken_picture)
                isTakePicture = true
                takePicture()
            }
        }

        imageBinding.retakeButton.setOnClickListener {
            if (isTakePicture) {
                this.areYouSure(getString(R.string.question_retake_picture)).apply {
                    setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok)) { _, _ ->
                        imageMenuList.clear()
                        manualMenuList.clear()
                        arrTempName.clear()
                        alreadyData.clear()
                        imageListAdapter.notifyDataSetChanged()
                        takePicture()
                    }
                    show()
                }
            } else {
                this.areYouSure(getString(R.string.question_change_image)).apply {
                    setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok)) { _, _ ->
                        imageMenuList.clear()
                        manualMenuList.clear()
                        arrTempName.clear()
                        alreadyData.clear()
                        imageListAdapter.notifyDataSetChanged()
                        chooseImage()
                    }
                    show()
                }
            }
        }

        imageBinding.backButton.setOnClickListener {
            this.areYouSure(getString(R.string.close_session)).apply {
                setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok)) { _, _ ->
                    Intent().apply {
                        this.putExtra("isSuccess", true)
                        setResult(RESULT_OK, this)
                    }
                    finish()
                }
                show()
            }
        }

        imageBinding.checkButton.setOnClickListener {
            this.toast(getString(R.string.add_list))
            imageMenuDialog(imageMenuList)
        }
    }

    private fun chooseImage() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpeg")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, 20)
        }
    }

    private fun takePicture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            startActivityForResult(it, 21)
        }
    }

    // ML IMAGE PROCESSING
    private fun uploadImage(file: File) {
        imageBinding.progressBar.progress = 0
        layerVisibility(true)
        val body = UploadRequestBody(file, "image", this)

        val account = fAuth.currentUser
        val aUsername = account?.displayName
        val aEmail = account?.email
        val aProfilePic = account?.photoUrl

        loginViewModel.postUser(aUsername, aEmail, aProfilePic.toString()).observe(this, { users ->
            NutrifitApiConfig.getNutrifitApiService(users.accessToken).uploadImage(
                MultipartBody.Part.createFormData("image_url", file.name, body)
            ).enqueue(object : Callback<ResponseImageML> {
                override fun onResponse(
                    call: Call<ResponseImageML>,
                    response: Response<ResponseImageML>
                ) {
                    imageBinding.progressBar.progress = 100
                    layerVisibility(false)
                    var responsePrediction = ""
                    if (!response.body()?.prediction.isNullOrEmpty()) {

                        for (predict in response.body()?.prediction!!) {
                            alreadyData.add(predict.name)
                        }

                        var asd = ""
                        var comma = ""
                        for (filterData in alreadyData.sorted()) {
                            if (filterData == asd) {
                                val i = arrTempName.indexOf(asd)
                                var getValue = imageMenuList[i].value
                                getValue += 1
                                imageMenuList[i] = MenuListEntity(asd, getValue, true)
                            } else {
                                asd = filterData
                                responsePrediction += "$comma $asd"
                                comma = ", "
                                arrTempName.add(asd)
                                imageMenuList.add(MenuListEntity(asd, 1, true))
                            }
                        }
                        imageListAdapter.notifyDataSetChanged()
                    } else {
                        responsePrediction = getString(R.string.no_food_detected)
                    }
                    imageBinding.result.text =
                        resources.getString(R.string.str_result_s, responsePrediction)

                    imagePath = response.body()?.imageProperty?.imageUrl.toString()
                    imageID = response.body()?.imageProperty?.id.toString()
                }

                override fun onFailure(call: Call<ResponseImageML>, t: Throwable) {
                    this@ImageDetection.toast(t.message.toString())
                }
            })
        })
    }

    private fun layerVisibility(visible: Boolean) {
        with(imageBinding) {
            if (visible) {
                linearLayoutLayer.visibility = View.VISIBLE
                result.visibility = View.INVISIBLE
                backButton.visibility = View.INVISIBLE
                retakeButton.visibility = View.INVISIBLE
                checkButton.visibility = View.INVISIBLE
            } else {
                linearLayoutLayer.visibility = View.GONE
                result.visibility = View.VISIBLE
                backButton.visibility = View.VISIBLE
                retakeButton.visibility = View.VISIBLE
                checkButton.visibility = View.VISIBLE
            }
        }

    }

    override fun onProgressUpdate(percentage: Int) {
        imageBinding.progressBar.progress = percentage
    }

    private fun imageMenuDialog(menuDataList: ArrayList<MenuListEntity>) {
        menuData = menuDataList
        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
        menuImageBinding = DialogMenuImageBinding.inflate(LayoutInflater.from(this))

        menuImageBinding.recyclerView3.layoutManager = LinearLayoutManager(this)
        menuImageBinding.recyclerView3.adapter = imageListAdapter

        dialogBuilder.setView(menuImageBinding.root)
        imageMenuDialog = dialogBuilder.create()
        imageMenuDialog.apply {
            setCanceledOnTouchOutside(false)
            setTitle(getString(R.string.already_listed))
            show()
        }

        menuImageBinding.recyclerView2.layoutManager = LinearLayoutManager(this)
        menuImageBinding.recyclerView2.adapter = popupAdapter

        // handling kalau arraynya kosong
        imageListAdapter.setData(menuData)
        imageListAdapter.notifyDataSetChanged()

        // untuk add item baru
        popupAdapter.setData(manualMenuList)
        popupAdapter.notifyDataSetChanged()

        if (menuData.size < 1) {
            this.toast(getString(R.string.no_data_image))
        }

        // set disable state
        setDisable()

        // checkbox select all
        menuImageBinding.checkBox.apply {
            setOnClickListener {
                val newData = ArrayList<MenuListEntity>()
                for (data in menuData) {
                    newData.add(MenuListEntity(data.name, data.value, this.isChecked))
                }

                imageListAdapter.setData(newData)
                menuData = newData
                this@ImageDetection.imageMenuList = newData
                imageListAdapter.notifyDataSetChanged()
            }
        }

        // button check to add new item
        menuImageBinding.btnAddNewItem.setOnClickListener(this)

        // button red minus to close the adding new item box section
        menuImageBinding.btnCancelAdding.setOnClickListener(this)

        // button save data
        menuImageBinding.btnSaveData.setOnClickListener(this)

        // prevent keyboard from hiding automatically
        menuImageBinding.inputItemName.setOnEditorActionListener { _, _, _ ->
            if (setDisable()) {
                addNewItem()
            }
            true
        }

        // listener to all data change and update the data list
        // just change the value increase or decrease
        imageListAdapter.setOnDataChangeListener(object : ImageListAdapter.InterfaceListener {
            override fun onSomeDataClicked(
                position: Int, name: String, newValue: Int, isChecked: Boolean
            ) {
                if (!isChecked) {
                    menuImageBinding.checkBox.isChecked = false
                }
                menuData[position] = MenuListEntity(name, newValue, isChecked)
                imageListAdapter.setData(menuData)
                imageListAdapter.notifyDataSetChanged()
            }
        })

        imageListAdapter.getCheckedState(object : ImageListAdapter.InterfaceListener {
            override fun onAllChecked(state: Boolean) {
                if (state) {
                    menuImageBinding.checkBox.isChecked = state
                }
            }
        })

        // listener on delete array and update the data list
        popupAdapter.setOnDeleteListener(object : DialogManualAdapter.InterfaceListener {
            override fun onDeleteClick(position: Int) {
                if (manualMenuList.size >= limitMenuItem) {
                    this@ImageDetection.toast(getString(R.string.now_max))
                    menuImageBinding.inputMenuSection.apply {
                        this.visibility = View.VISIBLE
                        this.animate().alpha(1f).duration = 200
                    }
                }
                manualMenuList.removeAt(position)
                popupAdapter.setData(manualMenuList)
                popupAdapter.notifyDataSetChanged()
            }
        })

        // listener to increase the value and update the data list
        popupAdapter.setOnDataChangeListener(object : DialogManualAdapter.InterfaceListener {
            override fun onValueChange(position: Int, name: String, newValue: Int) {
                manualMenuList[position] = MenuListEntity(name, newValue)
                popupAdapter.setData(manualMenuList)
                popupAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add_new_item -> {
                addNewItem()
            }

            R.id.btn_cancel_adding -> {
                menuImageBinding.inputItemName.apply {
                    this.setText("")
                    this.requestFocus()
                }
            }
            R.id.btn_save_data -> {
                // function to convert array to string
                val result = arrayPopupToString()
                when {
                    result.isNotEmpty() -> {
                        historySaved = true
                        Intent(this, DetailActivity::class.java).apply {
                            putExtra(DetailActivity.QUERY, result)
                            putExtra(DetailActivity.ARRAYLIST, arrayMenuList)
                            putExtra(DetailActivity.WITH_IMAGE, true)
                            putExtra(DetailActivity.IMAGE_PATH, imagePath)
                            putExtra(DetailActivity.IMAGE_ID, imageID)
                            addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
                            startActivity(this)
                            finish()
                        }
                    }
                    else -> this.toast(getString(R.string.havent_eat))
                }
                imageMenuDialog.dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                20 -> {
                    val uriSelectedImage = data?.data
                    Glide.with(this)
                        .load(uriSelectedImage)
                        .into(imageBinding.imageView)

                    // move image to cache directory
                    val parcelFileDescriptor =
                        contentResolver.openFileDescriptor(uriSelectedImage!!, "r", null) ?: return

                    val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                    val bm2 = BitmapFactory.decodeStream(inputStream)
                    val fileName = contentResolver.getFileName(uriSelectedImage)
                    val file = File(cacheDir, fileName)
                    val outputStream = FileOutputStream(file)
                    bm2.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()

                    uploadImage(file)
                }
                21 -> {
                    val takenImage = data?.extras?.get("data") as Bitmap
                    imageBinding.imageView.setImageBitmap(takenImage)

                    val dir =
                        File(Environment.getExternalStorageDirectory().absolutePath + "/Nutrifit/Pictures/")
                    dir.mkdirs()


                    val outFile = File(dir, "image_${System.currentTimeMillis()}.jpg")
                    val outputStream: FileOutputStream?
                    try {
                        outputStream = FileOutputStream(outFile)
                        takenImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()

                        uploadImage(outFile)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            Intent().apply {
                this.putExtra("isSuccess", false)
                setResult(RESULT_OK, this)
            }
            // clear the array list menu
            finish()
        }
    }


    private fun addNewItem(): Boolean {
        val text = menuImageBinding.inputItemName.text
        var check = false
        for (matchName in menuData) {
            if (matchName.name == text.toString()) {
                check = true
                this@ImageDetection.toast(getString(R.string.food_already_list, text))
                break
            }
        }
        for (matchName in manualMenuList) {
            if (matchName.name == text.toString()) {
                check = true
                this@ImageDetection.toast(getString(R.string.fodd_already_add, text))
                break
            }
        }
        if (check) {
            return false
        } else {
            manualMenuList.add(MenuListEntity(text.toString(), 1))
            popupAdapter.apply {
                setData(manualMenuList)
                notifyDataSetChanged()
            }

            menuImageBinding.inputItemName.apply {
                this.setText("")
                this.requestFocus()
            }
        }
        //
        if (manualMenuList.size >= limitMenuItem) {
            menuImageBinding.inputMenuSection.apply {
                this.visibility = View.INVISIBLE
                this.animate().alpha(0f).duration = 200
            }
            this.toast(getString(R.string.max_menu_reached))
        }
        return true
    }

    private var handle: Boolean = false
    private fun setDisable(): Boolean {
        // set default
        menuImageBinding.btnAddNewItem.apply {
            this.setColorFilter(Color.GRAY)
            this.isEnabled = false
        }

        menuImageBinding.inputItemName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (s.length >= 2) {
                    menuImageBinding.btnAddNewItem.apply {
                        this.post {
                            this.clearColorFilter()
                            this.isEnabled = true
                        }
                    }
                    menuImageBinding.inputItemName.apply {
                        this.post {
                            this.backgroundTintList =
                                ColorStateList.valueOf(Color.rgb(76, 239, 155))
                        }
                    }
                    handle = true
                } else {
                    menuImageBinding.btnAddNewItem.apply {
                        this.post {
                            this.setColorFilter(Color.GRAY)
                            this.isEnabled = false
                        }
                    }
                    menuImageBinding.inputItemName.apply {
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

    private fun arrayPopupToString(): String {
        var text = ""
        for (data in menuData) {
            if (data.isChecked) {
                text += "${data.value} serving of ${data.name} "
                arrayMenuList.add(MenuListEntity(data.name, data.value))
            }
        }
        for (data in manualMenuList) {
            text += "${data.value} serving of ${data.name} "
            arrayMenuList.add(MenuListEntity(data.name, data.value))
        }
        return text
    }

    override fun onBackPressed() {
        this.areYouSure(getString(R.string.close_session)).apply {
            setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok)) { _, _ ->
                Intent().apply {
                    if (historySaved) {
                        this.putExtra("isSuccess", true)
                    } else {
                        this.putExtra("isSuccess", false)
                    }
                    setResult(RESULT_OK, this)
                }
                finish()
            }
            show()
        }
    }
}
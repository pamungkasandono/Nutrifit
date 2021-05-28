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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.data.ResponseImageML
import com.udimuhaits.nutrifit.databinding.ActivityImageDetectionBinding
import com.udimuhaits.nutrifit.databinding.DialogMenuImageBinding
import com.udimuhaits.nutrifit.network.NutrifitApiConfig
import com.udimuhaits.nutrifit.ui.detail.DetailActivity
import com.udimuhaits.nutrifit.ui.home.HomeActivity
import com.udimuhaits.nutrifit.ui.home.dialogmenu.DialogManualAdapter
import com.udimuhaits.nutrifit.ui.home.dialogmenu.ListManualEntity
import com.udimuhaits.nutrifit.ui.imagedetection.dialogmenu.ImageListAdapter
import com.udimuhaits.nutrifit.ui.imagedetection.dialogmenu.ImageMenuListData
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
    private var imageMenuListData = ArrayList<ImageMenuListData>()
    private var arrayData = ArrayList<ListManualEntity>()
    private lateinit var menuImageBinding: DialogMenuImageBinding
    private val imageListAdapter = ImageListAdapter()
    private val popupAdapter = DialogManualAdapter()
    private lateinit var imageMenuDialog: androidx.appcompat.app.AlertDialog
    private val limitMenuItem = 15
    private lateinit var imageMenuData: ArrayList<ImageMenuListData>
    private lateinit var imagePath: String
    private val arrTempName = arrayListOf<String>()
    private val alreadyData = arrayListOf<String>()
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageBinding = ActivityImageDetectionBinding.inflate(layoutInflater)
        setContentView(imageBinding.root)

        forcePortrait(this)

        alertDialog = AlertDialog.Builder(this).create()

//        imageMenuListData.add(ImageMenuListData("Tofu", 1, true))
//        imageMenuListData.add(ImageMenuListData("Sate", 1, true))
//        imageMenuListData.add(ImageMenuListData("Krabby Patty", 1, true))

        val whatIChoose = intent.extras?.getInt("youChoose")
        when (whatIChoose) {
            HomeActivity.PICK_IMAGE -> {
                this.toast("pick")
                imageBinding.retakeButton.text = "Change Image"
                imageBinding.title.text = "Chosen Image"
                isTakePicture = false
                chooseImage()
            }
            HomeActivity.TAKE_PICTURE -> {
                this.toast("take")
                imageBinding.retakeButton.text = "Retake Picture"
                imageBinding.title.text = "Taken Picture"
                isTakePicture = true
                takePicture()
            }
        }
        this.toast(whatIChoose.toString())

        imageBinding.retakeButton.setOnClickListener {
            if (isTakePicture) {
                this.areYouSure("Retake the picture?").apply {
                    setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
                        imageMenuListData.clear()
                        arrayData.clear()
                        arrTempName.clear()
                        alreadyData.clear()
                        imageListAdapter.notifyDataSetChanged()
                        takePicture()
                    }
                    show()
                }
            } else {
                this.areYouSure("To change the image?").apply {
                    setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
                        imageMenuListData.clear()
                        arrayData.clear()
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
            this.areYouSure("Close this session will delete your list").apply {
                setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
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
            this.toast("add list")
            imageMenuDialog(imageMenuListData)
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
                    val fileName = contentResolver.getFileName(uriSelectedImage!!)
                    val file = File(cacheDir, fileName)
                    Log.d("asdasd fileName", fileName)
                    Log.d("asdasd inputStream", inputStream.toString())
                    val outputStream = FileOutputStream(file)
                    bm2.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

                    // cek size
//                    val fileSize = File(uriSelectedImage)
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()

                    uploadImage(file)
                }
                21 -> {
                    val takenImage = data?.extras?.get("data") as Bitmap
                    imageBinding.imageView.setImageBitmap(takenImage)

                    // bikin folder
                    val dir =
                        File(Environment.getExternalStorageDirectory().absolutePath + "/Nutrifit/Pictures/")
                    dir.mkdirs()

                    Log.d("asdasd dir", dir.toString())

                    val outFile = File(dir, "image_${System.currentTimeMillis()}.jpg")
                    Log.d("asdasd outFile", outFile.toString())
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

    // ML IMAGE PROCESSING
    private fun uploadImage(file: File) {
        imageBinding.progressBar.progress = 0
        layerVisibility(true)
        val body = UploadRequestBody(file, "image", this)

        val token = this.userPreference().getString("token", "")

        if (token == "") {
            this.toast("Token kosong")
        }

//        val token =
//            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNjg0MjIyNzQxLCJqdGkiOiJlOGY3YWI5ODRlZTM0NDM2OWE0NmM0MjM2OTBhNTVkYyIsInVzZXJfaWQiOjQ2fQ.WiK55mRRLHUTkrlP4QZ4O_Z0mSGmgD3Uh0FSuyOIlfw"
        NutrifitApiConfig.getNutrifitApiService(token).uploadImage(
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
                            var getValue = imageMenuListData[i].value
                            getValue += 1
                            imageMenuListData[i] = ImageMenuListData(asd, getValue, true)
                        } else {
                            asd = filterData
                            responsePrediction += "$comma $asd"
                            comma = ", "
                            arrTempName.add(asd)
                            imageMenuListData.add(ImageMenuListData(asd, 1, true))
                        }
                    }
                    imageListAdapter.notifyDataSetChanged()
                } else {
                    responsePrediction = "No food detected"
                }
                imageBinding.result.text =
                    resources.getString(R.string.str_result_s, responsePrediction)

                imagePath = response.body()?.imageProperty?.imageUrl.toString()
            }

            override fun onFailure(call: Call<ResponseImageML>, t: Throwable) {
                this@ImageDetection.toast(t.message.toString())
            }
        })
    }

    fun layerVisibility(visible: Boolean) {
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
//        uploadBinding.response.apply {
//            post {
//                this.text = percentage.toString()
//            }
//        }
    }

    private fun imageMenuDialog(imageMenuDataList: ArrayList<ImageMenuListData>) {
        imageMenuData = imageMenuDataList
        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
        menuImageBinding = DialogMenuImageBinding.inflate(LayoutInflater.from(this))

        menuImageBinding.recyclerView3.layoutManager = LinearLayoutManager(this)
        menuImageBinding.recyclerView3.adapter = imageListAdapter

        dialogBuilder.setView(menuImageBinding.root)
        imageMenuDialog = dialogBuilder.create()
        imageMenuDialog.apply {
            setCanceledOnTouchOutside(false)
            setTitle("Semua sudah terlist?")
            show()
        }

        menuImageBinding.recyclerView2.layoutManager = LinearLayoutManager(this)
        menuImageBinding.recyclerView2.adapter = popupAdapter

        // handling kalau arraynya kosong
        imageListAdapter.setData(imageMenuData)
        imageListAdapter.notifyDataSetChanged()

        // untuk add item baru
        popupAdapter.setData(arrayData)
        popupAdapter.notifyDataSetChanged()

        if (imageMenuData.size < 1) {
            this.toast("No data on image.")
        }

        // set disable state
        setDisable()

        // checkbox select all
        menuImageBinding.checkBox.apply {
            setOnClickListener {
                val newData = ArrayList<ImageMenuListData>()
                for (data in imageMenuData) {
                    newData.add(ImageMenuListData(data.name, data.value, this.isChecked))
                }

                Log.d("asdasd old", imageMenuData.toString())
                Log.d("asdasd new", newData.toString())
                imageListAdapter.setData(newData)
                // assign new data to local
                imageMenuData = newData
                // assign new data to global
                this@ImageDetection.imageMenuListData = newData
                Log.d("asdasd local", imageMenuData.toString())
                Log.d("asdasd global", this@ImageDetection.imageMenuListData.toString())
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
                imageMenuData[position] = ImageMenuListData(name, newValue, isChecked)
                imageListAdapter.setData(imageMenuData)
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
                if (arrayData.size >= limitMenuItem) {
                    this@ImageDetection.toast("now max")
                    menuImageBinding.inputMenuSection.apply {
                        this.visibility = View.VISIBLE
                        this.animate().alpha(1f).duration = 200
                    }
                }
                arrayData.removeAt(position)
                popupAdapter.setData(arrayData)
                popupAdapter.notifyDataSetChanged()
            }
        })

        // listener to increase the value and update the data list
        popupAdapter.setOnDataChangeListener(object : DialogManualAdapter.InterfaceListener {
            override fun onValueChange(position: Int, name: String, newValue: Int) {
                arrayData[position] = ListManualEntity(name, newValue)
                popupAdapter.setData(arrayData)
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
//                mODBinding.textViewResult.text = when {
//                    result.isNotEmpty() -> result
//                    else -> "You haven't eat anything yet?"
//                }
                when {
                    result.isNotEmpty() -> {
                        Intent(this, DetailActivity::class.java).apply {
                            putExtra(DetailActivity.QUERY, result)
                            putExtra(DetailActivity.WITH_IMAGE, true)
                            putExtra(DetailActivity.IMAGE_PATH, imagePath)
                            startActivityForResult(this, HomeActivity.FROM_DETAIL)
                            finish()
                        }
                    }
                    else -> this.toast("You haven't eat anything yet?")
                }
                imageMenuDialog.dismiss()
            }
        }
    }

    private fun addNewItem(): Boolean {
        val text = menuImageBinding.inputItemName.text
        var check = false
        for (matchName in imageMenuData) {
            if (matchName.name == text.toString()) {
                check = true
                this@ImageDetection.toast("Di list, $text sudah ada.")
                break
            }
        }
        for (matchName in arrayData) {
            if (matchName.name == text.toString()) {
                check = true
                this@ImageDetection.toast("Anda sudah menambahkan $text.")
                break
            }
        }
        if (check) {
            return false
        } else {
            arrayData.add(ListManualEntity(text.toString(), 1))
            popupAdapter.apply {
                setData(arrayData)
                notifyDataSetChanged()
            }

            menuImageBinding.inputItemName.apply {
                this.setText("")
                this.requestFocus()
            }
        }
        //
        if (arrayData.size >= limitMenuItem) {
            menuImageBinding.inputMenuSection.apply {
                this.visibility = View.INVISIBLE
                this.animate().alpha(0f).duration = 200
            }
            this.toast("maximum menu reached")
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
        for (data in imageMenuData) {
            if (data.isChecked) {
                text += "${data.value} serving of ${data.name} "
            }
        }
        for (data in arrayData) {
            text += "${data.value} serving of ${data.name} "
        }
        return text
    }

    override fun onBackPressed() {
        this.areYouSure("Close this session will delete your list").apply {
            setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
                Intent().apply {
                    this.putExtra("isSuccess", true)
                    setResult(RESULT_OK, this)
                }
                finish()
            }
            show()
        }
    }
}
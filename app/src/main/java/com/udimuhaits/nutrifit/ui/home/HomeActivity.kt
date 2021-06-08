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
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.udimuhaits.nutrifit.R
import com.udimuhaits.nutrifit.data.MenuListEntity
import com.udimuhaits.nutrifit.databinding.ActivityHomeBinding
import com.udimuhaits.nutrifit.databinding.DialogChooseImageBinding
import com.udimuhaits.nutrifit.databinding.DialogMenuManualBinding
import com.udimuhaits.nutrifit.ui.detail.DetailActivity
import com.udimuhaits.nutrifit.ui.home.dialogmenu.DialogManualAdapter
import com.udimuhaits.nutrifit.ui.home.history.HistoryAdapter
import com.udimuhaits.nutrifit.ui.home.history.HistoryViewModel
import com.udimuhaits.nutrifit.ui.imagedetection.ImageDetection
import com.udimuhaits.nutrifit.ui.login.LoginViewModel
import com.udimuhaits.nutrifit.ui.settings.SettingsActivity
import com.udimuhaits.nutrifit.utils.*

@SuppressLint("SetTextI18n")
class HomeActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val FROM_DETAIL = 100
        const val FROM_IMAGE_DETECTION = 200
        const val PICK_IMAGE = 201
        const val TAKE_PICTURE = 202
    }

    private lateinit var binding: ActivityHomeBinding
    private var isBackPressed = false
    private val arrayMenuList = ArrayList<MenuListEntity>()
    private val limitTotalMenu = 15
    private lateinit var menuManualBinding: DialogMenuManualBinding
    private lateinit var fAuth: FirebaseAuth
    private val dialogManualAdapter = DialogManualAdapter()
    private lateinit var dialogImageOption: AlertDialog
    private lateinit var dialogAddManual: AlertDialog
    private var setDisabledState: Boolean = false
    private var clicked = false
    private val historyAdapter = HistoryAdapter()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_button_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_button_anim
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth = FirebaseAuth.getInstance()

        val account = fAuth.currentUser
        val aUsername = account?.displayName
        val aEmail = account?.email
        val aProfilePic = account?.photoUrl

        getImageFromLogin(aUsername, aEmail, aProfilePic.toString())

        forcePortrait(this)

        getHistory(aUsername, aEmail, aProfilePic.toString())

        if (this.writeIsGranted()) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 10
            )
        }

        binding.imgProfile.setOnClickListener {
            onAddButtonClick()
        }

        binding.btnSetting.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.btnSearch.setOnClickListener {
            manualDialog(arrayMenuList)
        }

        binding.searchBox.setOnClickListener {
            manualDialog(arrayMenuList)
        }

        binding.selectImage.setOnClickListener {
            selectImage()
        }

        val extraFromDetail = intent.extras

        if (extraFromDetail?.getString("fab_code") == "image") {
            intent.removeExtra("fab_code")
            selectImage()
        } else if (extraFromDetail?.getString("fab_code") == "manual") {
            intent.removeExtra("fab_code")
            manualDialog(arrayMenuList)
        }

    }

    private fun getHistory(aUsername: String?, aEmail: String?, aProfilePic: String?) {
        loginViewModel.postUser(aUsername, aEmail, aProfilePic).observe(this, { users ->
            historyViewModel.getHistory(users.userId, users.accessToken).observe(this) {
                if (it.isEmpty()) {
                    binding.textView7.visibility = View.VISIBLE
                    binding.recyclerViewHistory.visibility = View.GONE
                } else {
                    binding.textView7.visibility = View.GONE
                    binding.recyclerViewHistory.visibility = View.VISIBLE
                }

                historyAdapter.setData(it)
                historyAdapter.notifyDataSetChanged()
            }
        })

        with(binding.recyclerViewHistory) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = historyAdapter
        }

        binding.refreshHistory.setOnRefreshListener {
            arrayMenuList.clear()
            dialogManualAdapter.setData(arrayMenuList)
            dialogManualAdapter.notifyDataSetChanged()
            finish()
            startActivity(intent)
        }
    }

    private fun getImageFromLogin(aUsername: String?, aEmail: String?, aProfilePic: String?) {
        loginViewModel.postUser(aUsername, aEmail, aProfilePic).observe(this, { users ->
            Glide
                .with(this)
                .load(users.profilePic)
                .into(binding.imgProfile)
        })
    }

    private fun onAddButtonClick() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.btnSetting.visibility = View.VISIBLE
        } else {
            binding.btnSetting.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.btnSetting.startAnimation(fromBottom)
        } else {
            binding.btnSetting.startAnimation(toBottom)
        }
    }

    private fun setClickable(clicked: Boolean) {
        binding.btnSetting.isClickable = !clicked
    }

    private fun selectImage() {
        dialogImageOption = AlertDialog.Builder(this).create()
        dialogImageOption.setTitle(getString(R.string.choose_picture))
        val dialogImageOptionsBinding =
            DialogChooseImageBinding.inflate(LayoutInflater.from(this))
        dialogImageOption.setView(dialogImageOptionsBinding.root)

        dialogImageOptionsBinding.selectGallery.setOnClickListener {
            if (this.writeIsGranted()) {
                this.toastLong(getString(R.string.permission))
            } else {
                Intent(this, ImageDetection::class.java).apply {
                    this.putExtra("youChoose", PICK_IMAGE)
                    startActivityForResult(this, FROM_IMAGE_DETECTION)
                }
            }
            dialogImageOption.dismiss()
        }

        dialogImageOptionsBinding.selectCamera.setOnClickListener {
            if (this.writeIsGranted()) {
                this.toastLong(getString(R.string.permission))
            } else {
                Intent(this, ImageDetection::class.java).apply {
                    this.putExtra("youChoose", TAKE_PICTURE)
                    startActivityForResult(this, FROM_IMAGE_DETECTION)
                }
            }
            dialogImageOption.dismiss()
        }

        dialogImageOption.show()
    }

    private fun manualDialog(arrayManualList: ArrayList<MenuListEntity>) {
        dialogAddManual = AlertDialog.Builder(this).create()
        menuManualBinding = DialogMenuManualBinding.inflate(LayoutInflater.from(this))

        menuManualBinding.recyclerView2.layoutManager = LinearLayoutManager(this)
        menuManualBinding.recyclerView2.adapter = dialogManualAdapter

        dialogAddManual.setView(menuManualBinding.root)
        dialogAddManual.setCanceledOnTouchOutside(false)
        dialogAddManual.setTitle(resources.getString(R.string.what_do_you_want_to_eat_today))
        dialogAddManual.show()

        if (arrayManualList.size >= 1) {
            dialogManualAdapter.setData(arrayManualList)
            dialogManualAdapter.notifyDataSetChanged()
        }

        if (arrayMenuList.size >= limitTotalMenu) {
            menuManualBinding.inputMenuSection.apply {
                this.visibility = View.INVISIBLE
                this.animate().alpha(0f).duration = 200
            }
        }

        setDisable()

        menuManualBinding.btnAddNewItem.setOnClickListener(this)

        menuManualBinding.btnCancelAdding.setOnClickListener(this)

        menuManualBinding.inputItemName.setOnEditorActionListener { _, _, _ ->
            if (setDisable()) {
                addNewItem()
            }
            true
        }

        menuManualBinding.btnSaveData.setOnClickListener(this)

        dialogManualAdapter.setOnDeleteListener(object : DialogManualAdapter.InterfaceListener {
            override fun onDeleteClick(position: Int) {
                if (arrayManualList.size >= limitTotalMenu) {
                    this@HomeActivity.toast(getString(R.string.max_reached))
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

        dialogManualAdapter.setOnDataChangeListener(object : DialogManualAdapter.InterfaceListener {
            override fun onValueChange(position: Int, name: String, newValue: Int) {
                arrayManualList[position] = MenuListEntity(name, newValue)
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
                val result = arrayManualToString()
                dialogAddManual.dismiss()
                when {
                    result.isNotEmpty() -> {
                        Intent(this, DetailActivity::class.java).apply {
                            putExtra(DetailActivity.QUERY, result)
                            putExtra(DetailActivity.ARRAYLIST, arrayMenuList)
                            putExtra(DetailActivity.WITH_IMAGE, false)
                            startActivityForResult(this, FROM_DETAIL)
                        }
                    }
                    else -> this.toast(getString(R.string.havent_eat))
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FROM_DETAIL -> {
                    if (data?.getBooleanExtra("isSuccess", false) == true) {
                        arrayMenuList.clear()
                        dialogManualAdapter.setData(arrayMenuList)
                        dialogManualAdapter.notifyDataSetChanged()
                        finish()
                        startActivity(intent)
                    }
                    arrayMenuList.clear()
                    dialogManualAdapter.setData(arrayMenuList)
                    dialogManualAdapter.notifyDataSetChanged()
                }
                FROM_IMAGE_DETECTION -> {
                    if (data?.getBooleanExtra("isSuccess", false) == true) {
                        arrayMenuList.clear()
                        dialogManualAdapter.setData(arrayMenuList)
                        dialogManualAdapter.notifyDataSetChanged()
                        finish()
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun setDisable(): Boolean {
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
        for (matchName in arrayMenuList) {
            if (matchName.name == text.toString()) {
                check = true
                break
            }
        }
        if (check) {
            this.toast(getString(R.string.menu_already, text))
            return false
        } else {
            arrayMenuList.add(MenuListEntity(text.toString(), 1))
            dialogManualAdapter.setData(arrayMenuList)
            dialogManualAdapter.notifyDataSetChanged()
            menuManualBinding.inputItemName.apply {
                this.setText("")
                this.requestFocus()
            }
        }
        if (arrayMenuList.size >= limitTotalMenu) {
            menuManualBinding.inputMenuSection.apply {
                this.visibility = View.INVISIBLE
                this.animate().alpha(0f).duration = 200
            }
            this.toast(getString(R.string.max_reached))
        }
        return true
    }

    private fun arrayManualToString(): String {
        var text = ""
        for (data in arrayMenuList) {
            text += "${data.value} serving of ${data.name} "
        }
        return text
    }

    override fun onBackPressed() {
        if (isBackPressed) {
            super.onBackPressed()
        }

        isBackPressed = true
        Toast.makeText(this, getString(R.string.back), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ isBackPressed = false }, 2000)
    }
}



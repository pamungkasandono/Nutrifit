package com.udimuhaits.nutrifit.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udimuhaits.nutrifit.R
import java.text.SimpleDateFormat
import java.util.*

fun Context.userPreference(): SharedPreferences {
    return this.getSharedPreferences("UsersPreference", AppCompatActivity.MODE_PRIVATE)
}

@SuppressLint("SourceLockedOrientationActivity")
fun forcePortrait(activity: Activity) {
    val screenLayoutSize =
        activity.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
    if (screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_SMALL || screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}

fun Context.writeIsGranted(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) != PackageManager.PERMISSION_GRANTED
}

fun Context.toastLong(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun ContentResolver.getFileName(uri: Uri): String {
    var name = ""
    val cursor = query(uri, null, null, null, null)
    cursor?.use {
        it.moveToFirst()
        name = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
    }
    return name
}

fun Context.areYouSure(s: String): AlertDialog {
    val alertDialog = AlertDialog.Builder(this).create()
    alertDialog.apply {
        setTitle(getString(R.string.are_you_sure))
        setMessage(s)
        setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
    }
    return alertDialog
}

// get current year、month and day

@SuppressLint("SimpleDateFormat")
fun getDate(opt: Int = 10): String {
    val sdf = when (opt) {
        1 -> SimpleDateFormat("yyyy")
        0 -> SimpleDateFormat("MM")
        else -> SimpleDateFormat("yyyy-MM-dd")
    }
    sdf.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    return sdf.format(Date())
}

@SuppressLint("SimpleDateFormat")
fun String.stringToDate(): String {
    //Instantiating the SimpleDateFormat class
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    //Parsing the given String to Date object
    val date = formatter.parse(this)

    val suffixes = arrayOf(
        "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
        "th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
        "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
        "th", "st"
    )

    val newDate = SimpleDateFormat("dd-MMMM-yyyy", Locale.US).format(date)
    val date1 = newDate.split("-")

    return "${date1[0].toInt()}${suffixes[date1[0].toInt()]} ${date1[1]} ${date1[2]}"
}

fun getAgeByBirthDate(birthDate: String): String {
    val arrayBirthDate = birthDate.split("-")

    val dob = Calendar.getInstance()
    val today = Calendar.getInstance()
    dob[arrayBirthDate[0].toInt(), arrayBirthDate[1].toInt()] = arrayBirthDate[2].toInt()
    var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
    if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
        age--
    }
    val ageInt = age
    return ageInt.toString()
}












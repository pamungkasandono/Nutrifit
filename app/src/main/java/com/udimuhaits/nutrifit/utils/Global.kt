package com.udimuhaits.nutrifit.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.ContextCompat


var SET_NUTRIFIT_ACCESS_TOKEN = ""

val GET_NUTRIFIT_ACCESS_TOKEN = SET_NUTRIFIT_ACCESS_TOKEN

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
        setTitle("Are you sure")
        setMessage(s)
        setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
            dialog.dismiss()
        }
    }
    return alertDialog
}
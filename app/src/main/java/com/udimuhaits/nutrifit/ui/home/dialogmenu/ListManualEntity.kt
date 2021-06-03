package com.udimuhaits.nutrifit.ui.home.dialogmenu

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ListManualEntity(
    var name: String,
    var value: Int
) : Parcelable

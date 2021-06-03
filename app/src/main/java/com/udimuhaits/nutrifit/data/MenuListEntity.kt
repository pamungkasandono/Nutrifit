package com.udimuhaits.nutrifit.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MenuListEntity(
    var name: String,
    var value: Int,
    var isChecked: Boolean = true
) : Parcelable

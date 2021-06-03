package com.udimuhaits.nutrifit.data

import com.google.gson.annotations.SerializedName


data class HistoryResponse(
    @SerializedName("sortbydate")
    private val result: Map<String, List<DataRow>>
)

data class DataRow(

    @field:SerializedName("food_name")
    val foodName: String,

    @field:SerializedName("total_fat")
    val totalFat: String,

    @field:SerializedName("fiber")
    val fiber: String,

    @field:SerializedName("quantity")
    val quantity: Int,

    @field:SerializedName("calories")
    val calories: String,

    @field:SerializedName("saturated_fat")
    val saturatedFat: String,

    @field:SerializedName("sodium")
    val sodium: String,

    @field:SerializedName("time_food_consumed")
    val timeFoodConsumed: String,

    @field:SerializedName("user_id")
    val userId: List<String>,

    @field:SerializedName("serving_size")
    val servingSize: String,

    @field:SerializedName("protein")
    val protein: String,

    @field:SerializedName("cholesterol")
    val cholesterol: String,

    @field:SerializedName("date_time_consumed")
    val dateTimeConsumed: String,

    @field:SerializedName("carbonhydrates")
    val carbonhydrates: String,

    @field:SerializedName("sugar")
    val sugar: String,

    @field:SerializedName("CapturedFood_id")
    val capturedFoodId: String? = null
)


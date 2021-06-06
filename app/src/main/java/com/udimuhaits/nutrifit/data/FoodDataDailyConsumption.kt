package com.udimuhaits.nutrifit.data

import com.google.gson.annotations.SerializedName

data class FoodDataDailyConsumptionItem(

    @field:SerializedName("food_name")
    val foodName: String,

    @field:SerializedName("total_fat")
    val totalFat: Float,

    @field:SerializedName("fiber")
    val fiber: Float,

    @field:SerializedName("quantity")
    val quantity: Int,

    @field:SerializedName("calories")
    val calories: Float,

    @field:SerializedName("saturated_fat")
    val saturatedFat: Float,

    @field:SerializedName("sodium")
    val sodium: Float,

    @field:SerializedName("time_food_consumed")
    val timeFoodConsumed: String,

    @field:SerializedName("user_id")
    val userId: Int,

    @field:SerializedName("serving_size")
    val servingSize: Float,

    @field:SerializedName("protein")
    val protein: Float,

    @field:SerializedName("cholesterol")
    val cholesterol: Float,

    @field:SerializedName("date_time_consumed")
    val dateTimeConsumed: String,

    @field:SerializedName("carbonhydrates")
    val carbonhydrates: Float,

    @field:SerializedName("sugar")
    val sugar: Float,

    @field:SerializedName("CapturedFood_id")
    val capturedFoodId: String? = null
)

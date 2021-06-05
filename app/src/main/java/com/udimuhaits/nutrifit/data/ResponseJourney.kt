package com.udimuhaits.nutrifit.data

import com.google.gson.annotations.SerializedName

data class ResponseJourney(
    @field:SerializedName("ResponseJourney")
    val responseJourney: List<ResponseJourneyItem>
)

data class ResponseJourneyItem(

    @field:SerializedName("food_name")
    val foodName: String,

    @field:SerializedName("total_fat")
    val totalFat: Double,

    @field:SerializedName("fiber")
    val fiber: Double,

    @field:SerializedName("quantity")
    val quantity: Int,

    @field:SerializedName("calories")
    val calories: Double,

    @field:SerializedName("saturated_fat")
    val saturatedFat: Double,

    @field:SerializedName("sodium")
    val sodium: Double,

    @field:SerializedName("time_food_consumed")
    val timeFoodConsumed: String,

    @field:SerializedName("serving_size")
    val servingSize: Double,

    @field:SerializedName("protein")
    val protein: Double,

    @field:SerializedName("cholesterol")
    val cholesterol: Double,

    @field:SerializedName("date_time_consumed")
    val dateTimeConsumed: String,

    @field:SerializedName("carbonhydrates")
    val carbonhydrates: Double,

    @field:SerializedName("sugar")
    val sugar: Double,

    @field:SerializedName("CapturedFood_id")
    val capturedFoodId: Int
)

package com.udimuhaits.nutrifit.data

import com.google.gson.annotations.SerializedName

data class FoodDataDailyConsumption(
	@field:SerializedName("FoodDataDailyConsumption")
	val foodDataDailyConsumption: List<FoodDataDailyConsumptionItem>
)

data class FoodDataDailyConsumptionItem(

	@field:SerializedName("food_name")
	val foodName: String,

	@field:SerializedName("total_fat")
	val totalFat: Int,

	@field:SerializedName("fiber")
	val fiber: Int,

	@field:SerializedName("quantity")
	val quantity: Int,

	@field:SerializedName("calories")
	val calories: Int,

	@field:SerializedName("saturated_fat")
	val saturatedFat: Int,

	@field:SerializedName("sodium")
	val sodium: Int,

	@field:SerializedName("time_food_consumed")
	val timeFoodConsumed: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("serving_size")
	val servingSize: Int,

	@field:SerializedName("protein")
	val protein: Int,

	@field:SerializedName("cholesterol")
	val cholesterol: Int,

	@field:SerializedName("date_time_consumed")
	val dateTimeConsumed: String,

	@field:SerializedName("carbonhydrates")
	val carbonhydrates: Int,

	@field:SerializedName("sugar")
	val sugar: Int,

	@field:SerializedName("CapturedFood_id")
	val capturedFoodId: Int
)

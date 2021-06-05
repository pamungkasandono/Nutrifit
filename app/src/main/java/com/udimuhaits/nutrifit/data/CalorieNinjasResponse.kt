package com.udimuhaits.nutrifit.data

import com.google.gson.annotations.SerializedName

data class CalorieNinjasResponse(
    @field:SerializedName("items")
    val items: List<ItemsItem>
)

data class ItemsItem(

    @field:SerializedName("sodium_mg")
    val sodiumMg: String,

    @field:SerializedName("sugar_g")
    val sugarG: String,

    @field:SerializedName("fat_total_g")
    val fatTotalG: String,

    @field:SerializedName("cholesterol_mg")
    val cholesterolMg: String,

    @field:SerializedName("protein_g")
    val proteinG: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("fiber_g")
    val fiberG: String,

    @field:SerializedName("serving_size_g")
    val servingSizeG: String,

    @field:SerializedName("calories")
    val calories: String,

    @field:SerializedName("fat_saturated_g")
    val fatSaturatedG: String,

    @field:SerializedName("carbohydrates_total_g")
    val carbohydratesTotalG: String,

    @field:SerializedName("potassium_mg")
    val potassiumMg: String
)

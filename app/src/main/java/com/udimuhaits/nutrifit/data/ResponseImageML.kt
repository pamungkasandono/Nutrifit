package com.udimuhaits.nutrifit.data

import com.google.gson.annotations.SerializedName

data class ResponseImageML(

    @field:SerializedName("image_property")
    val imageProperty: ImageProperty,

    @field:SerializedName("prediction")
    val prediction: List<PredictionItem>
)

data class ImageProperty(

    @field:SerializedName("image_url")
    val imageUrl: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("upload_date")
    val uploadDate: String
)

data class PredictionItem(

    @field:SerializedName("ymin")
    val ymin: Double,

    @field:SerializedName("xmin")
    val xmin: Double,

    @field:SerializedName("ymax")
    val ymax: Double,

    @field:SerializedName("xmax")
    val xmax: Double,

    @field:SerializedName("confidence")
    val confidence: Double,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("class")
    val jsonMemberClass: Int
)

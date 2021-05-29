package com.udimuhaits.nutrifit.data

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @field:SerializedName("user_id")
    val userId: Int?,

    @field:SerializedName("username")
    val username: String?,

    @field:SerializedName("email")
    val email: String?,

    @field:SerializedName("profile_pic")
    val profilePic: String?,

    @field:SerializedName("access_token")
    val accessToken: String?,

    @field:SerializedName("refresh_token")
    val refreshToken: String?
)

data class UserBody(
    val detail: String?,
    val status: String?
)

data class UserProfile(
    @field:SerializedName("id")
    val id: Int?,

    @field:SerializedName("username")
    val username: String?,

    @field:SerializedName("email")
    val email: String?,

    @field:SerializedName("profile_pic")
    val profilePic: String?,


    @field:SerializedName("birth_date")
    val birthDate: String?,

    @field:SerializedName("height")
    val height: Int?,

    @field:SerializedName("weight")
    val weight: Double?,
)

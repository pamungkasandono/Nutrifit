package com.udimuhaits.nutrifit.network

import com.udimuhaits.nutrifit.data.UserBody
import com.udimuhaits.nutrifit.data.UserProfile
import com.udimuhaits.nutrifit.data.UserResponse
import retrofit2.Call
import retrofit2.http.*

interface UserApiServices {
    @GET("google/")
    fun getLogin(
        @Query("token") token: String?
    ): Call<UserBody>

    @GET("api/profile/{id}/")
    fun getUser(
        @Path("id") id: Int?,
    ): Call<UserProfile>

    @FormUrlEncoded
    @POST("google/login/")
    fun postLogin(
        @Field("username") username: String?,
        @Field("email") email: String?,
        @Field("profile_pic") profilePic: String?
    ): Call<UserResponse>

    @FormUrlEncoded
    @PUT("api/profile/{id}/")
    fun putUser(
        @Path("id") id: Int?,
        @Field("birth_date") birthDate: String?,
        @Field("height") height: Int?,
        @Field("weight") weight: Double?,
    ): Call<UserProfile>
}
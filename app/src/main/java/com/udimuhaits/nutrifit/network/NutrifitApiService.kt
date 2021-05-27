package com.udimuhaits.nutrifit.network

import com.udimuhaits.nutrifit.data.ResponseImageML
import com.udimuhaits.nutrifit.data.UserBody
import com.udimuhaits.nutrifit.data.UserProfile
import com.udimuhaits.nutrifit.data.UserResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface NutrifitApiService {
    @GET("google/")
    fun getLogin(
        @Query("token") token: String?
    ): Call<UserBody>

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
        @Field("weight") weight: Int?,
    ): Call<UserProfile>

    @Multipart
    @POST("api/uploadimage/prediction/yolov5s6")
    fun uploadImage(
        @Part image_url: MultipartBody.Part
    ): Call<ResponseImageML>
}
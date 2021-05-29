package com.udimuhaits.nutrifit.network

import com.udimuhaits.nutrifit.BuildConfig
import com.udimuhaits.nutrifit.data.ResponseImageML
import com.udimuhaits.nutrifit.data.UserBody
import com.udimuhaits.nutrifit.data.UserProfile
import com.udimuhaits.nutrifit.data.UserResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface NutrifitApiService {
    @GET(BuildConfig.GET_LOGIN)
    fun getLogin(
        @Query("token") token: String?
    ): Call<UserBody>

    @GET(BuildConfig.GET_USER)
    fun getUser(
        @Path("id") id: Int?,
    ): Call<UserProfile>

    @FormUrlEncoded
    @POST(BuildConfig.POST_LOGIN)
    fun postLogin(
        @Field("username") username: String?,
        @Field("email") email: String?,
        @Field("profile_pic") profilePic: String?
    ): Call<UserResponse>

    @FormUrlEncoded
    @PUT(BuildConfig.PUT_USER)
    fun putUser(
        @Path("id") id: Int?,
        @Field("birth_date") birthDate: String?,
        @Field("height") height: Int?,
        @Field("weight") weight: Double?,
    ): Call<UserProfile>

    @Multipart
    @POST(BuildConfig.POST_IMAGE)
    fun uploadImage(
        @Part image_url: MultipartBody.Part
    ): Call<ResponseImageML>
}
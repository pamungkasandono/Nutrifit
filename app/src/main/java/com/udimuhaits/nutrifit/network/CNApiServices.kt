package com.udimuhaits.nutrifit.network

import com.udimuhaits.nutrifit.data.CalorieNinjasResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CNApiServices {

    @Headers("X-Api-Key: YOUR_API")
    @GET("nutrition/")
    fun getSearchResult(
        @Query("query") query: String
    ): Call<CalorieNinjasResponse> // ini harus menggunakan data response
}
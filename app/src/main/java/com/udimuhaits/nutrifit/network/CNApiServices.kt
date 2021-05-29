package com.udimuhaits.nutrifit.network

import com.udimuhaits.nutrifit.BuildConfig
import com.udimuhaits.nutrifit.data.CalorieNinjasResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CNApiServices {

    @Headers("X-Api-Key: ${BuildConfig.API_KEY_CN}")
    @GET(BuildConfig.SEARCH_OBJECT)
    fun getSearchResult(
        @Query("query") query: String
    ): Call<CalorieNinjasResponse> // ini harus menggunakan data response
}
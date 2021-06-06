package com.udimuhaits.nutrifit.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udimuhaits.nutrifit.data.CNEntity
import com.udimuhaits.nutrifit.data.CalorieNinjasResponse
import com.udimuhaits.nutrifit.network.NutrifitApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {

    private val _modelResponseCN = MutableLiveData<List<CNEntity>>()

    fun getListFood(query: String): LiveData<List<CNEntity>> {
        val clint = NutrifitApiConfig.getCNApiService().getSearchResult(query)
        clint.enqueue(object : Callback<CalorieNinjasResponse> {
            override fun onResponse(
                call: Call<CalorieNinjasResponse>,
                response: Response<CalorieNinjasResponse>
            ) {
                if (response.isSuccessful) {
                    val calNinList = ArrayList<CNEntity>()
                    for (data in response.body()?.items!!) {
                        with(data) {
                            val food = CNEntity(
                                name,
                                sodiumMg,
                                sugarG,
                                fatTotalG,
                                cholesterolMg,
                                proteinG,
                                fiberG,
                                servingSizeG,
                                calories,
                                fatSaturatedG,
                                carbohydratesTotalG,
                                potassiumMg
                            )
                            calNinList.addAll(listOf(food))
                        }
                    }
                    _modelResponseCN.postValue(calNinList)
                }
            }

            override fun onFailure(call: Call<CalorieNinjasResponse>, t: Throwable) {
                Log.d("  Error T", t.message.toString())
                Log.d("  Error call", call.toString())
            }
        })
        return _modelResponseCN
    }
}
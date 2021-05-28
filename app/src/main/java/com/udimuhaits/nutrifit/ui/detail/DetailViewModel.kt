package com.udimuhaits.nutrifit.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udimuhaits.nutrifit.data.CNEntity
import com.udimuhaits.nutrifit.data.CalorieNinjasResponse
import com.udimuhaits.nutrifit.network.CNApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {

    // Dari viewModel harusnya cuma mengisi data dari CalorieNinjaResponse ke CNEntity
    // Dan Hasil di terima oleh activity lalu di teruskan ke Adapter

    private val _modelResponseCN = MutableLiveData<List<CNEntity>>()
//    val modelResponseCN: LiveData<List<CNEntity>> = _modelResponseCN

    var setQueries = ""

//    private val _isLoading = MutableLiveData<Boolean>()
//    val isNotLoading: LiveData<Boolean> = _isLoading

//    init {
//        listFood()
//    }

    fun getListFood(query: String): LiveData<List<CNEntity>> {
        Log.d("asdasd query", query)
//        val query = setQueries // wajib di kasih koma untuk menghindari kesalahan
//        _isLoading.value = false

        val clint = CNApiConfig.getApiService().getSearchResult(query)
//        val calorieNinjasResult = MutableLiveData<List<CNEntity>>()

        clint.enqueue(object : Callback<CalorieNinjasResponse> {
            override fun onResponse(
                call: Call<CalorieNinjasResponse>,
                response: Response<CalorieNinjasResponse>
            ) {
//                _isLoading.value = true
                if (response.isSuccessful) {
                    Log.d("asdasd ok", response.body()?.items.toString())
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
                    Log.d("asdasd last", calNinList.toString())
                }
            }

            override fun onFailure(call: Call<CalorieNinjasResponse>, t: Throwable) {
                Log.d("asdasd Error T", t.message.toString())
                Log.d("asdasd Error call", call.toString())
            }
        })
        return _modelResponseCN
    }
}
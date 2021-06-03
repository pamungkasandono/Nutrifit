package com.udimuhaits.nutrifit.ui.home.history

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udimuhaits.nutrifit.data.FoodlistItem
import com.udimuhaits.nutrifit.data.HistoryResponse
import com.udimuhaits.nutrifit.data.ItemHistoryEntity
import com.udimuhaits.nutrifit.data.ResponseItem
import com.udimuhaits.nutrifit.network.NutrifitApiConfig
import com.udimuhaits.nutrifit.utils.getDate
import com.udimuhaits.nutrifit.utils.toast
import com.udimuhaits.nutrifit.utils.userPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryViewModel : ViewModel() {

    // Dari viewModel harusnya cuma mengisi data dari CalorieNinjaResponse ke CNEntity
    // Dan Hasil di terima oleh activity lalu di teruskan ke Adapter

    private val _historyResponse = MutableLiveData<List<HistoryResponse>>()

    private val _historyItem = MutableLiveData<List<ItemHistoryEntity>>()

//    val getHistoryItem: LiveData<List<ItemHistoryEntity>> = _historyItem

//    val getHistory: LiveData<List<HistoryResponse>> = _setHistory

//    private var _context: Context

//    fun setter(context: Context) {
//        _context = context
//    }

//    init {
//        history()
//    }


    fun getHistory(context: Context): LiveData<List<ItemHistoryEntity>> {

        val token = context.userPreference().getString("token", "")

        if (token == "") {
            context.toast("Token kosong")
        }

        val userID = context.userPreference().getInt("user_id", 0)

        NutrifitApiConfig.getNutrifitApiService(token)
            .getHistory(userID, getDate(1), getDate(0))
            .enqueue(object : Callback<List<ResponseItem>> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<List<ResponseItem>>,
                    response: Response<List<ResponseItem>>
                ) {
                    if (response.isSuccessful) {
                        val historyResponse = ArrayList<ItemHistoryEntity>()
                        var testArray = ArrayList<FoodlistItem>()

                        for (i in response.body()!!) {
                            Log.i("asdasd i to string", i.toString())

                            testArray.addAll(i.foodlist)
                        }
                        Log.i("asdasd testArray", testArray.toString())
                        var tempDataTesArr = ""
                        var strFoodName = ""
                        var arryTemp = ArrayList<ItemHistoryEntity>()
                        var arryTempIdx = 0
                        var imgPathTemp: String? = null
                        var imgPathTempNew: String? = null
                        for (dataTestArr in testArray) {
                            if (dataTestArr.dateTimeConsumed == tempDataTesArr) {
                                strFoodName += ", ${dataTestArr.foodName}"
                                if (imgPathTemp != null) {
                                    imgPathTempNew = dataTestArr.capturedFoodId
                                }
                                arryTemp[arryTempIdx] =
                                    ItemHistoryEntity(tempDataTesArr, strFoodName, imgPathTempNew)
                            } else {
                                tempDataTesArr = dataTestArr.dateTimeConsumed
                                strFoodName = dataTestArr.foodName
                                imgPathTemp = dataTestArr.capturedFoodId
                                arryTemp.add(
                                    ItemHistoryEntity(
                                        tempDataTesArr, strFoodName, imgPathTemp
                                    )
                                )
                                arryTempIdx = arryTemp.size - 1
                            }

                            Log.i("asdasd dataTestArr", dataTestArr.toString())
                        }
                        Log.i("asdasd strFoodName", strFoodName)
                        Log.i("asdasd arryTemp", arryTemp.toString())
                        _historyItem.postValue(arryTemp)
                        Log.i("asdasd historyResponse", historyResponse.toString())
                    }
//                    for (i in response.body()?.indices!!) {
//                        val res1 = response.body()!![i]
//                        Log.i("asdasd res1", res1.toString())
//                        Log.i("asdasd res2", res1.foodlist[i].toString())
//                    }
                }

                override fun onFailure(call: Call<List<ResponseItem>>, t: Throwable) {
                    Log.i("asdasd error", t.message.toString())
                }
            })
        return _historyItem
    }
}
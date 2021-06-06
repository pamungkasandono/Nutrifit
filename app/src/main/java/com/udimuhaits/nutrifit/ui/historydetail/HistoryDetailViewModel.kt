package com.udimuhaits.nutrifit.ui.historydetail

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udimuhaits.nutrifit.data.ResponseJourneyItem
import com.udimuhaits.nutrifit.network.NutrifitApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryDetailViewModel : ViewModel() {

    private val _historyItem = MutableLiveData<List<ResponseJourneyItem>>()

    fun getHistoryDetail(
        userId: Int?,
        token: String?,
        intentData: String?
    ): LiveData<List<ResponseJourneyItem>> {
        if (intentData != null) {
            if (userId != null) {
                NutrifitApiConfig.getNutrifitApiService(token)
                    .getHistoryDetail(userId, intentData)
                    .enqueue(object : Callback<List<ResponseJourneyItem>> {
                        @SuppressLint("SetTextI18n")
                        override fun onResponse(
                            call: Call<List<ResponseJourneyItem>>,
                            response: Response<List<ResponseJourneyItem>>
                        ) {
                            if (response.isSuccessful) {
                                _historyItem.postValue(response.body())
                            }
                        }

                        override fun onFailure(
                            call: Call<List<ResponseJourneyItem>>,
                            t: Throwable
                        ) {
                            Log.i("error", t.message.toString())
                        }
                    })
            }
        }
        return _historyItem
    }
}
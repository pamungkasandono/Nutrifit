package com.udimuhaits.nutrifit.ui.historydetail

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udimuhaits.nutrifit.data.HistoryResponse
import com.udimuhaits.nutrifit.data.ResponseJourneyItem
import com.udimuhaits.nutrifit.network.NutrifitApiConfig
import com.udimuhaits.nutrifit.utils.toast
import com.udimuhaits.nutrifit.utils.toastLong
import com.udimuhaits.nutrifit.utils.userPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryDetailViewModel : ViewModel() {

    private val _historyResponse = MutableLiveData<List<HistoryResponse>>()

    private val _historyItem = MutableLiveData<List<ResponseJourneyItem>>()

    fun getHistoryDetail(
        context: Context,
        intentData: String?
    ): LiveData<List<ResponseJourneyItem>> {

        val token = context.userPreference().getString("token", "")

        if (token == "") {
            context.toast("Token kosong")
        }

        val userID = context.userPreference().getInt("user_id", 0)

        context.toastLong("getDate() $intentData")
        if (intentData != null) {
            NutrifitApiConfig.getNutrifitApiService(token)
                .getHistoryDetail(userID, intentData)
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

                    override fun onFailure(call: Call<List<ResponseJourneyItem>>, t: Throwable) {
                        Log.i("asdasd error", t.message.toString())
                    }
                })
        }
        return _historyItem
    }
}
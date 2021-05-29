package com.udimuhaits.nutrifit.ui.form

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udimuhaits.nutrifit.data.UserProfile
import com.udimuhaits.nutrifit.network.NutrifitApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FormViewModel : ViewModel() {
    companion object {
        const val TAG = "FormViewModel"
    }

    private val _userProfile = MutableLiveData<UserProfile>()

    private val _updateUser = MutableLiveData<UserProfile>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUser(token: String?, id: Int?): LiveData<UserProfile> {
        _isLoading.value = true
        NutrifitApiConfig.getNutrifitApiService(token).getUser(id)
            .enqueue(object : Callback<UserProfile> {
                override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _userProfile.value = response.body()
                        Log.d("tesViewModel", response.body().toString())
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
        return _userProfile
    }

    fun putUser(
        id: Int?,
        token: String?,
        birthDate: String?,
        height: Int?,
        weight: Double?
    ): LiveData<UserProfile> {
        _isLoading.value = true
        NutrifitApiConfig.getNutrifitApiService(token).putUser(id, birthDate, height, weight)
            .enqueue(object : Callback<UserProfile> {
                override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _updateUser.value = response.body()
                        Log.d("tesViewModel", response.body().toString())
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }

            })
        return _updateUser
    }
}
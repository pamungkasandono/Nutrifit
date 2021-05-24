package com.udimuhaits.nutrifit.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udimuhaits.nutrifit.data.UserBody
import com.udimuhaits.nutrifit.data.UserProfile
import com.udimuhaits.nutrifit.data.UserResponse
import com.udimuhaits.nutrifit.network.UserApiConfig
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import java.util.*

class LoginViewModel : ViewModel() {

    companion object {
        const val TAG = "LoginViewModel"
    }

    private val _sendUser = MutableLiveData<UserResponse>()

    private val _userBody = MutableLiveData<UserBody>()

    private val _updateUser = MutableLiveData<UserProfile>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun postUser(username: String?, email: String?, profilePic: String?): LiveData<UserResponse> {
        _isLoading.value = true
        UserApiConfig.postUserApiService().postLogin(username, email, profilePic).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _sendUser.value = response.body()
                    Log.d("tesViewModel", response.body().toString())
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }

        })
        return _sendUser
    }

    fun getUser(token: String?): LiveData<UserBody> {
        _isLoading.value = true
        UserApiConfig.getUserApiService(token).getLogin(token).enqueue(object : Callback<UserBody> {
            override fun onResponse(call: Call<UserBody>, response: Response<UserBody>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _userBody.value = response.body()
                    Log.d("tesViewModel", response.body().toString())
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserBody>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
        return _userBody
    }

    fun putUser(id: Int?, token: String?, birthDate: String?, height: Int?, weight: Int?): LiveData<UserProfile> {
        _isLoading.value = true
        UserApiConfig.getUserApiService(token).putUser(id, birthDate, height, weight).enqueue(object :Callback<UserProfile> {
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
                Log.e("logViewModel", "onFailure: ${t.message.toString()}")
            }

        })

        return _updateUser
    }

}
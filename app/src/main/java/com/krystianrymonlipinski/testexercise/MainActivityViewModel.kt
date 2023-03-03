package com.krystianrymonlipinski.testexercise

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krystianrymonlipinski.testexercise.retrofit.HttpService
import com.krystianrymonlipinski.testexercise.retrofit.model.NumberObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class MainActivityViewModel : ViewModel() {

    private lateinit var httpService: HttpService

    private val _numbersData: MutableLiveData<List<NumberData>> = MutableLiveData(listOf())
    val numbersData: LiveData<List<NumberData>> = _numbersData
    private val _isLoadingSuccessful: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoadingSuccessful: LiveData<Boolean> = _isLoadingSuccessful

    init {
        setupHttpClient()
    }

    private fun setupHttpClient() {
        Retrofit.Builder().apply {
            baseUrl(INFO_BASE_URL)
            addConverterFactory(GsonConverterFactory.create())
        }.build().also {
            httpService = it.create(HttpService::class.java)
        }
    }

    fun loadAllNumbersInfo() {
        httpService.getAllNumbersInfo().enqueue(object : Callback<List<NumberObject>> {
            override fun onResponse(call: Call<List<NumberObject>>?, response: Response<List<NumberObject>>?) {
                response?.let { res ->
                    _numbersData.value = res.body().map {
                        NumberData(it.name, null)
                    }
                    _isLoadingSuccessful.postValue(true)

                    res.body().onEachIndexed { index, numberObject ->
                        val updatedUrl = numberObject.imageUrl.replace("http", "https", ignoreCase = false)
                        loadImage(index, updatedUrl)
                    }

                } ?: _isLoadingSuccessful.postValue(false)
            }

            override fun onFailure(call: Call<List<NumberObject>>?, t: Throwable?) {
                _isLoadingSuccessful.postValue(false)
            }

        })
    }

    fun loadNumberInfo(number: Int) {
        httpService.getNumberInfo(number.toString()).enqueue(object : Callback<NumberObject> {
            override fun onResponse(call: Call<NumberObject>?, response: Response<NumberObject>?) {
                response?.let {
                    val responseData = it.body()
                    Timber.d("HERE: $responseData")
                } ?: Timber.d("HERE: No data returned")
            }

            override fun onFailure(call: Call<NumberObject>?, t: Throwable?) {
                Timber.d("HERE: failure; ${t?.toString()}")
            }
        })
    }

    private fun loadImage(index: Int, url: String) : Bitmap? {
        httpService.getImage(url).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.let {
                    viewModelScope.launch(Dispatchers.IO) {
                        val bitmap = BitmapFactory.decodeStream(it.body().byteStream())
                        _numbersData.postValue(_numbersData.value?.apply {
                            get(index).image = bitmap
                        })
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

            }
        })
        return null
    }

    companion object {
        private const val INFO_BASE_URL = " https://dev.tapptic.com/test/"

    }
}
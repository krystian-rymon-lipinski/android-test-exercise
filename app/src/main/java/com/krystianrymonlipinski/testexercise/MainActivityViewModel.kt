package com.krystianrymonlipinski.testexercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.krystianrymonlipinski.testexercise.retrofit.HttpService
import com.krystianrymonlipinski.testexercise.retrofit.model.NumberData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class MainActivityViewModel : ViewModel() {

    private var numbersData = listOf<NumberData>()
    private lateinit var httpService: HttpService

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

    fun getNumbersInfo() = numbersData

    fun loadAllNumbersInfo() {
        httpService.getAllNumbersInfo().enqueue(object : Callback<List<NumberData>> {
            override fun onResponse(call: Call<List<NumberData>>?, response: Response<List<NumberData>>?) {
                response?.let {
                    numbersData = it.body().onEach {
                        it.imageUrl.replace("http", "https", ignoreCase = false)
                    }
                    _isLoadingSuccessful.postValue(true)
                } ?: _isLoadingSuccessful.postValue(false)
            }

            override fun onFailure(call: Call<List<NumberData>>?, t: Throwable?) {
                _isLoadingSuccessful.postValue(false)
            }

        })
    }

    fun loadNumberInfo(number: Int) {
        httpService.getNumberInfo(number.toString()).enqueue(object : Callback<NumberData> {
            override fun onResponse(call: Call<NumberData>?, response: Response<NumberData>?) {
                response?.let {
                    val responseData = it.body()
                    Timber.d("HERE: $responseData")
                } ?: Timber.d("HERE: No data returned")
            }

            override fun onFailure(call: Call<NumberData>?, t: Throwable?) {
                Timber.d("HERE: failure; ${t?.toString()}")
            }
        })
    }

    companion object {
        private const val INFO_BASE_URL = " https://dev.tapptic.com/test/"

    }
}
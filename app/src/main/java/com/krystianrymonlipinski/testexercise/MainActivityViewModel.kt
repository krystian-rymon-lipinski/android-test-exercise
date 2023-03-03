package com.krystianrymonlipinski.testexercise

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
    private val _selectedNumber: MutableLiveData<String> = MutableLiveData()
    val selectedNumber: LiveData<String> = _selectedNumber
    private val _displayedImage: MutableLiveData<NumberData> = MutableLiveData()
    val displayedImage: LiveData<NumberData> = _displayedImage
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

    fun setSelectedNumber(index: Int) {
        _selectedNumber.value = _numbersData.value?.get(index)?.name
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
                        loadImage(numberObject.imageUrl, index)
                    }

                } ?: _isLoadingSuccessful.postValue(false)
            }

            override fun onFailure(call: Call<List<NumberObject>>?, t: Throwable?) {
                _isLoadingSuccessful.postValue(false)
            }

        })
    }

    fun loadNumberInfo(numberName: String) {
        httpService.getNumberInfo(numberName).enqueue(object : Callback<NumberObject> {
            override fun onResponse(call: Call<NumberObject>?, response: Response<NumberObject>?) {
                response?.let {
                    loadImage(it.body().imageUrl)
                } ?: Timber.d("HERE: No data returned")
            }

            override fun onFailure(call: Call<NumberObject>?, t: Throwable?) {
                Timber.d("HERE: failure; ${t?.toString()}")
            }
        })
    }

    private fun loadImage(url: String, index: Int? = null) {
        val secureUrl = url.replace("http", "https", ignoreCase = false)

        httpService.getImage(secureUrl).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.let {
                    viewModelScope.launch(Dispatchers.IO) {
                        val bitmap = BitmapFactory.decodeStream(it.body().byteStream())

                        index?.let { index ->
                            _numbersData.postValue(_numbersData.value?.apply {
                                get(index).image = bitmap
                            })
                        } ?: _displayedImage.postValue(NumberData(
                            selectedNumber.value!!,
                            bitmap
                        ))
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                //TODO: show a default image indicating it didn't work
            }
        })
    }

    companion object {
        private const val INFO_BASE_URL = " https://dev.tapptic.com/test/"

    }
}
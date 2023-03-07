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
    private val _dataRetrievalState: MutableLiveData<DataRetrievalState> = MutableLiveData(DataRetrievalState.LOADING)
    val dataRetrievalState: LiveData<DataRetrievalState> = _dataRetrievalState
    private val _selectedNumber: MutableLiveData<SelectedCard?> = MutableLiveData()
    val selectedNumber: LiveData<SelectedCard?> = _selectedNumber

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

    fun handleOnNumberSelected(index: Int) {
        _numbersData.value?.get(index)?.name?.let {
            _selectedNumber.postValue(SelectedCard(index, it, null))
            loadNumberInfo(it)
        }
    }

    fun setDataRetrievalState(state: DataRetrievalState) {
        _dataRetrievalState.value = state
    }

    fun clearSelectedNumber() {
        _selectedNumber.value = null
    }

    fun isNumberSelected() = _selectedNumber.value != null
    fun isDataLoaded() = _dataRetrievalState.value != DataRetrievalState.LOADING
    fun getAllNumbersInfo() = _numbersData.value ?: emptyList()
    fun getCurrentlySelectedNumber() = _selectedNumber.value?.index

    fun loadAllNumbersInfo() {
        httpService.getAllNumbersInfo().enqueue(object : Callback<List<NumberObject>> {
            override fun onResponse(call: Call<List<NumberObject>>?, response: Response<List<NumberObject>>?) {
                response?.let { res ->
                    _numbersData.value = res.body().map {
                        NumberData(it.name, null)
                    }
                    _dataRetrievalState.postValue(DataRetrievalState.SUCCESS)

                    res.body().onEachIndexed { index, numberObject ->
                        loadImage(numberObject.imageUrl, index)
                    }

                } ?: _dataRetrievalState.postValue(DataRetrievalState.FAILURE)
            }

            override fun onFailure(call: Call<List<NumberObject>>?, t: Throwable?) {
                _dataRetrievalState.postValue(DataRetrievalState.FAILURE)
            }

        })
    }

    private fun loadNumberInfo(numberName: String) {
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
                        } ?: _selectedNumber.postValue(_selectedNumber.value?.copy(image = bitmap))
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                //TODO: show a default image indicating it didn't work
            }
        })
    }

    data class SelectedCard(
        val index: Int,
        val numberName: String,
        val image: Bitmap?
    )

    enum class DataRetrievalState {
        LOADING,
        FAILURE,
        SUCCESS
    }

    companion object {
        private const val INFO_BASE_URL = " https://dev.tapptic.com/test/"
    }
}
package com.krystianrymonlipinski.testexercise.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krystianrymonlipinski.testexercise.models.NumberData
import com.krystianrymonlipinski.testexercise.retrofit.HttpService
import com.krystianrymonlipinski.testexercise.retrofit.model.NumberObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val httpService: HttpService
) : ViewModel() {


    private val _numbersData: MutableLiveData<List<NumberData>> = MutableLiveData(listOf())
    val numbersData: LiveData<List<NumberData>> = _numbersData
    private val _dataRetrievalState: MutableLiveData<DataRetrievalState> = MutableLiveData(
        DataRetrievalState.LOADING
    )
    val dataRetrievalState: LiveData<DataRetrievalState> = _dataRetrievalState
    private val _selectedNumber: MutableLiveData<SelectedCard?> = MutableLiveData()
    val selectedNumber: LiveData<SelectedCard?> = _selectedNumber

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

    fun loadAllNumbersInfo() { withCoroutine {
        httpService.getAllNumbersInfo().enqueue(object : Callback<List<NumberObject>> {
            override fun onResponse(
                call: Call<List<NumberObject>>?,
                response: Response<List<NumberObject>>?) { withCoroutine {

                response?.let { res ->
                    _numbersData.postValue(res.body().map {
                        NumberData(it.name, null)
                    })
                    _dataRetrievalState.postValue(DataRetrievalState.SUCCESS)

                    res.body().onEachIndexed { index, numberObject ->
                        loadImage(numberObject.imageUrl, index)
                    }

                } ?: _dataRetrievalState.postValue(DataRetrievalState.FAILURE)
            }

            }
            override fun onFailure(call: Call<List<NumberObject>>?, t: Throwable?) {
                _dataRetrievalState.postValue(DataRetrievalState.FAILURE)
            }
        })

    } }

    private fun loadNumberInfo(numberName: String) { withCoroutine {
        httpService.getNumberInfo(numberName).enqueue(object : Callback<NumberObject> {
            override fun onResponse(
                call: Call<NumberObject>?,
                response: Response<NumberObject>?) { withCoroutine {

                response?.let {
                    loadImage(it.body().imageUrl)
                } ?: Timber.d("No data returned")
            } }
            override fun onFailure(call: Call<NumberObject>?, t: Throwable?) {
                Timber.d("Failure: ${t?.toString()}")
            }
        })
    } }

    private fun loadImage(url: String, index: Int? = null) { withCoroutine {
        val secureUrl = url.replace("http", "https", ignoreCase = false)

        httpService.getImage(secureUrl).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>?,
                response: Response<ResponseBody>?) { withCoroutine {

                response?.let {
                    val bitmap = BitmapFactory.decodeStream(it.body().byteStream())

                    index?.let { index ->
                        _numbersData.postValue(_numbersData.value?.apply {
                            get(index).image = bitmap
                        })
                    } ?: _selectedNumber.postValue(_selectedNumber.value?.copy(image = bitmap))
                }
            } }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                Timber.d("Failure: ${t?.toString()}")
            }
        })
    } }

    private fun withCoroutine(action: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            action.invoke()
        }
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


}
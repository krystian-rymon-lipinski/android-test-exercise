package com.krystianrymonlipinski.testexercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.krystianrymonlipinski.testexercise.retrofit.HttpService
import com.krystianrymonlipinski.testexercise.retrofit.model.NumberData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var httpService: HttpService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupHttpClient()
        lifecycleScope.launch(Dispatchers.IO) {
            loadAllNumbersInfo()
            loadNumberInfo(number = 1)
        }
    }

    private fun setupHttpClient() {
        Retrofit.Builder().apply {
            baseUrl(BASE_URL)
            addConverterFactory(GsonConverterFactory.create())
        }.build()
         .also {
            httpService = it.create(HttpService::class.java)
        }
    }

    private fun loadAllNumbersInfo() {
        httpService.getAllNumbersInfo().enqueue(object : Callback<List<NumberData>> {
            override fun onResponse(call: Call<List<NumberData>>?, response: Response<List<NumberData>>?) {
                response?.let {
                    val responseData = it.body()
                    Timber.d("HERE: $responseData")
                } ?: Timber.d("HERE: No data returned")
            }

            override fun onFailure(call: Call<List<NumberData>>?, t: Throwable?) {
                Timber.d("HERE: failure; ${t?.toString()}")
            }

        })
    }

    private fun loadNumberInfo(number: Int) {
        httpService.getNumberInfo(number.toString()).enqueue(object : Callback<NumberData>{
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
        private const val BASE_URL = " https://dev.tapptic.com/test/"
    }
}
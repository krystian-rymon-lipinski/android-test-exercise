package com.krystianrymonlipinski.testexercise.retrofit

import com.krystianrymonlipinski.testexercise.retrofit.model.NumberData
import retrofit2.Call
import retrofit2.http.GET

interface HttpService {

    @GET("json.php")
    fun getNumbersInfo() : Call<List<NumberData>>
}
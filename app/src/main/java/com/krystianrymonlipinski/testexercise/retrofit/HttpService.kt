package com.krystianrymonlipinski.testexercise.retrofit

import com.krystianrymonlipinski.testexercise.retrofit.model.NumberData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HttpService {

    @GET("json.php")
    fun getAllNumbersInfo() : Call<List<NumberData>>

    @GET("json.php")
    fun getNumberInfo(@Query("name") numberName: String) : Call<NumberData>
}
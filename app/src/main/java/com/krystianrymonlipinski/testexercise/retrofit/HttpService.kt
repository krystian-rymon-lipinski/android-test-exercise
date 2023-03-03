package com.krystianrymonlipinski.testexercise.retrofit

import com.krystianrymonlipinski.testexercise.retrofit.model.NumberObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface HttpService {

    @GET("json.php")
    fun getAllNumbersInfo() : Call<List<NumberObject>>

    @GET("json.php")
    fun getNumberInfo(@Query("name") numberName: String) : Call<NumberObject>

    @GET
    fun getImage(@Url url: String) : Call<ResponseBody>
}
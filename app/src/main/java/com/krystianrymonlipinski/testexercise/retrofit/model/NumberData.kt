package com.krystianrymonlipinski.testexercise.retrofit.model

import com.google.gson.annotations.SerializedName

data class NumberData(
      @SerializedName("name") val name: String,
      @SerializedName("image") val imageUrl: String
)
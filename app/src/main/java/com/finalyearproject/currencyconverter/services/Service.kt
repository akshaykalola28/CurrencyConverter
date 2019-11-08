package com.finalyearproject.currencyconverter.services

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Service {

    @GET("latest")
    fun getExchangeRate(@Query("symbols") symbols: String): Call<JsonObject>
}
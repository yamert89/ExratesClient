package ru.exrates.mobile.logic.rest

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange

interface RestService {

    @POST("rest/exchange")
    fun getExchanges(@Body payload: String) : Call<Map<String, Exchange>>

    @GET("rest/pair")
    fun getPair(): Call<Map<String, CurrencyPair>>
}
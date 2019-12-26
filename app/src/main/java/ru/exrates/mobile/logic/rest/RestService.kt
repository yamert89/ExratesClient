package ru.exrates.mobile.logic.rest

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload

interface RestService {
    @GET("rest/lists")
    fun lists(): Call<Map<String, List<String>>>

    @POST("rest/exchange")
    fun getExchanges(@Body payload: String) : Call<Map<String, Exchange>>

    @POST("rest/exchange")
    fun getExchange(@Body payload: ExchangePayload) : Call<Exchange> //todo получить быстрый ответ. если пары нет, запрос очень долгий

    @POST("rest/exchange")
    fun getExchange(@Body payload: String) : Call<Exchange>

    @GET("rest/pair")
    fun getPair(@Query("pname") pName: String): Call<Map<String, CurrencyPair>>
}
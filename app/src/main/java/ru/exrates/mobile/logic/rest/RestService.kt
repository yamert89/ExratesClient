package ru.exrates.mobile.logic.rest

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.CursPeriod
import ru.exrates.mobile.logic.entities.json.ExchangeNamesObject
import ru.exrates.mobile.logic.entities.json.ExchangePayload

interface RestService {

    @GET("ping")
    fun ping(): Call<String>

    @GET("rest/lists")
    fun lists(): Call<List<ExchangeNamesObject>>

    @POST("rest/exchange")
    fun getExchanges(@Body payload: String) : Call<Map<String, Exchange>>

    @POST("rest/exchange")
    fun getExchange(@Body payload: ExchangePayload) : Call<Exchange>

    @POST("rest/exchange")
    fun getExchange(@Body payload: String) : Call<Exchange>

    @GET("rest/pair")
    fun getPair(@Query("c1") c1: String, @Query("c2") c2: String, @Query("historyinterval") historyInterval: String, @Query("limit") limit: Int): Call<MutableList<CurrencyPair>>

    @GET("rest/pair")
    fun getPair(@Query("c1") c1: String, @Query("c2") c2: String, @Query("limit") limit: Int): Call<MutableList<CurrencyPair>>

    @GET("rest/pair/single")
    fun addOnePair(@Query("c1") c1: String, @Query("c2") c2: String, @Query("exId") exId: Int): Call<CurrencyPair>

    @GET("rest/pair/history")
    fun getPriceHistory(@Query("c1") c1: String, @Query("c2") c2: String, @Query("exId") exId: Int,
                        @Query("historyinterval") historyInterval: String, @Query("limit") limit: Int): Call<List<Double>>

    @POST("rest/dynamics")
    fun getPriceChangeBySingleInterval(@Body curs: ExchangePayload) : Call<CursPeriod>
}
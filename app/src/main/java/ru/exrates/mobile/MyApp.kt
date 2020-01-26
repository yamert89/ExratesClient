package ru.exrates.mobile

import android.app.Application
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.rest.RestService

class MyApp(): Application(){
    var currentExchange: Exchange? = null
    var currentPairInfo: MutableList<CurrencyPair>? = null
    var currentExchangeName: String = "binanceExchange"
    var currentPairName: String = "ETCBTC"
    //var currencyNameslist: List<String>? = null
    var exchangeNamesList: Map<String, List<String>>? = null
    var currentInterval: String = "1h"
    var restService: RestService
    val ip = "192.168.0.102"
    // val ip = "192.168.43.114"
    //val ip = "192.168.1.72"
    init {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging).build()
        val om = ObjectMapper()
        om.registerKotlinModule()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ip:8080/")
            .addConverterFactory(JacksonConverterFactory.create(om))
            .build()
        restService = retrofit.create(RestService::class.java)
        
    }


}
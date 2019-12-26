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
    var currentPairInfo: Map<String, CurrencyPair>? = null
    var restService: RestService
    //val ip = "192.168.0.100"
    // val ip = "192.168.43.114"
    val ip = "192.168.1.722"
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
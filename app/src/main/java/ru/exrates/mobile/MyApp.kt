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
import ru.exrates.mobile.logic.entities.json.ExchangeNamesObject
import ru.exrates.mobile.logic.rest.RestService
import ru.exrates.mobile.presenters.BasePresenter
import java.time.Duration

class MyApp(): Application(){
    var currentExchange: Exchange? = null
    var currentPairInfo: MutableList<CurrencyPair>? = null
    //var currentExchangeId = 1
    var currentCur1: String = "ETC"
    var currentCur2: String = "BTC"
    //var currencyNameslist: List<String>? = null
    var exchangeNamesList: List<ExchangeNamesObject>? = null
    var currentInterval: String = ""
    lateinit var restService: RestService
    val om = ObjectMapper()
    val ip = "192.168.0.101"
    // val ip = "192.168.43.114"
    //val ip = "192.168.1.72"


    override fun onCreate() {
        super.onCreate()
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .callTimeout(Duration.ofMinutes(3))
            /*.addInterceptor(logging)*/.build()
        om.registerKotlinModule()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ip:8080/")
            .addConverterFactory(JacksonConverterFactory.create(om))
            .client(client)
            .build()
        restService = retrofit.create(RestService::class.java)
    }


}
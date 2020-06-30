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

class MyApp : Application(){
    private val exRates = ExRates()
    var currentExchange: Exchange?
        get() = exRates.currentExchange
        set(value){exRates.currentExchange = value }

    var currentPairInfo: MutableList<CurrencyPair>?
        get() = exRates.currentPairInfo
        set(value) {exRates.currentPairInfo = value}

    var currentCur1: String
        get() = exRates.currentCur1
        set(value) {exRates.currentCur1 = value}

    var currentCur2: String
        get() = exRates.currentCur2
        set(value){exRates.currentCur2 = value}

    var exchangeNamesList: Map<Int, ExchangeNamesObject>?
        get() = exRates.exchangeNamesList
        set(value) {exRates.exchangeNamesList = value}

    var currentInterval: String
        get() = exRates.currentInterval
        set(value) {exRates.currentInterval = value}

    var restService: RestService = exRates.restService
        private set

    val om = exRates.om


    /*override fun onCreate() {
        super.onCreate()
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .callTimeout(Duration.ofMinutes(1))
            .connectTimeout(Duration.ofSeconds(10))
            *//*.addInterceptor(logging)*//*.build()
        om.registerKotlinModule()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ip:8080/")
            .addConverterFactory(JacksonConverterFactory.create(om))
            .client(client)
            .build()
        restService = retrofit.create(RestService::class.java)
    }*/
}

class ExRates{
    var currentExchange: Exchange? = null
    var currentPairInfo: MutableList<CurrencyPair>? = null
    var currentCur1: String = "ETC"
    var currentCur2: String = "BTC"
    var exchangeNamesList: Map<Int, ExchangeNamesObject>? = null
    var currentInterval: String = ""
    lateinit var restService: RestService
    val om = ObjectMapper()
    //val ip = "192.168.0.103"
    // val ip = "192.168.43.114"
    val ip = "192.168.1.72"
    init {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .callTimeout(Duration.ofMinutes(1))
            .connectTimeout(Duration.ofSeconds(10))
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
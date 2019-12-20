package ru.exrates.mobile

import android.app.Application
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import ru.exrates.mobile.logic.DataProvider
import ru.exrates.mobile.logic.rest.RestService

class MyApp(): Application(){
    val dataProvider: DataProvider = DataProvider()
    var restService: RestService
    init {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging).build()
        val om = ObjectMapper()
        om.registerKotlinModule()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:8080/")
            .addConverterFactory(JacksonConverterFactory.create(om))
            .build()
        restService = retrofit.create(RestService::class.java)
        
    }


}
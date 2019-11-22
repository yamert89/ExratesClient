package ru.exrates.mobile

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import ru.exrates.mobile.logic.DataProvider
import ru.exrates.mobile.logic.rest.RestService

class MyApp(): Application(){
    val dataProvider: DataProvider = DataProvider()
    var restService: RestService
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://enchat.ru/")
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
        restService = retrofit.create(RestService::class.java)
        
    }


}
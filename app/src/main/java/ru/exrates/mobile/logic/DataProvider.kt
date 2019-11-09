package ru.exrates.mobile.logic

import android.content.Context
import android.content.SharedPreferences
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.JSON
import kotlinx.serialization.json.Json


import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.rest.RestService

class DataProvider(val exchanges: Map<String, Exchange> = HashMap(), val restService: RestService = RestService()){

    @UnstableDefault
    fun getSavedExchange(context: Context): Exchange {
        val prefs = context.getSharedPreferences("exchangeStorage", Context.MODE_PRIVATE)
        val ex: String = prefs.getString("savedExchange", "binanceExchange")!!
        return Json.parse(Exchange.serializer(), ex)
    }

    @UnstableDefault
    fun storeExchange(exchange: Exchange, context: Context) {
        val prefs = context.getSharedPreferences("exchangeStorage", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("savedExchange", Json.stringify(Exchange.serializer(), exchange))
        editor.apply()
    }





}
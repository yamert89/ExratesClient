package ru.exrates.mobile.logic

import android.content.Context
import android.content.SharedPreferences
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.JSON
import kotlinx.serialization.json.Json
import ru.exrates.mobile.DEFAULT_EXCHANGE
import ru.exrates.mobile.EXCH_STORAGE
import ru.exrates.mobile.SAVED_EXCHANGE
import ru.exrates.mobile.logic.entities.CurrencyPair


import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.rest.RestService
import java.util.concurrent.ArrayBlockingQueue

class DataProvider(val exchanges: Map<String, Exchange> = HashMap(), val restService: RestService = RestService()){

    @UnstableDefault
    fun getSavedExchange(context: Context): Exchange {
        val ex = Storage(context).getStoreExchangeStringValue(SAVED_EXCHANGE, DEFAULT_EXCHANGE)!!
        return if (ex == DEFAULT_EXCHANGE) Exchange("", mutableListOf(CurrencyPair("def", 0.0, mapOf("1d" to 0.0), ArrayBlockingQueue(1))), listOf("4.8"))
        else Json.parse(Exchange.serializer(), ex)
    }

    @UnstableDefault
    fun storeExchange(exchange: Exchange, context: Context) {
        Storage(context).saveStoreExchangeStringValue(SAVED_EXCHANGE, Json.stringify(Exchange.serializer(), exchange))
    }





}
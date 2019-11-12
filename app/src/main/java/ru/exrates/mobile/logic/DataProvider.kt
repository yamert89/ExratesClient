package ru.exrates.mobile.logic

import android.content.Context
import android.content.SharedPreferences
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.JSON
import kotlinx.serialization.json.Json
import ru.exrates.mobile.*
import ru.exrates.mobile.logic.entities.CurrencyPair


import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.rest.RestService
import java.lang.Exception
import java.lang.RuntimeException
import java.util.concurrent.ArrayBlockingQueue

class DataProvider(@ContextualSerialization val exchanges: Map<String, Exchange> = HashMap(),
                   val restService: RestService = RestService()){

    @UnstableDefault
    fun getSavedExchange(context: Context): Exchange {
        val ex = Storage(context).getStoreExchangeStringValue(SAVED_EXCHANGE, DEFAULT)
        return if (ex == DEFAULT) Exchange("", mutableListOf(CurrencyPair("def", 0.0, mapOf("1d" to 0.0), ArrayBlockingQueue(1))), listOf("4.8"))
        else Json.parse(Exchange.serializer(), ex)
    }

    @UnstableDefault
    fun storeExchange(exchange: Exchange, context: Context) {
        Storage(context).saveStoreExchangeStringValue(SAVED_EXCHANGE, Json.stringify(Exchange.serializer(), exchange))
    }

    fun getSavedExchanges(context: Context): Map<String, Exchange>?{
        val map = Storage(context).getStoreExchangeStringValue(STORAGE_EXCHANGES, DEFAULT)

        return null//if(map == DEFAULT) throw RuntimeException("saved exchanges not found")
    }





}
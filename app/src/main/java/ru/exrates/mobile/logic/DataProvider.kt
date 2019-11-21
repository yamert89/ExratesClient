package ru.exrates.mobile.logic


import android.content.Context
import ru.exrates.mobile.*
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.rest.RestService
import java.util.concurrent.ArrayBlockingQueue

class DataProvider(val exchanges: Map<String, Exchange> = HashMap(),
                   val restService: RestService = RestService()){

    fun getSavedExchange(context: Context): Exchange = Storage(context).loadObject(SAVED_EXCHANGE) ?:
        Exchange("", mutableListOf(CurrencyPair("def", 0.0, mapOf("1d" to 0.0), ArrayBlockingQueue(1))), listOf("4.8"))

    fun storeExchange(exchange: Exchange, context: Context) = Storage(context).saveObject(exchange, SAVED_EXCHANGE)

    fun getSavedExchanges(context: Context): Map<String, Exchange>? = Storage(context).loadObject<Map<String, Exchange>>(STORAGE_EXCHANGES)

    fun storeExchanges(exchanges: Map<String, Exchange>, context: Context) = Storage(context).saveObject(exchanges, SAVED_EXCHANGES)

    fun getMainSavedCurrencyNameList(context: Context): List<String> = Storage(context).loadObject<List<String>>(
        SAVED_CURRENCY_NAME_LIST) ?: listOf(context.resources.getString(R.string.tv_loading))

    fun getMainSavedExchangesNameList(context: Context) = Storage(context).loadObject<List<String>>(
        SAVED_EXCHANGE_NAME_LIST) ?: listOf(context.resources.getString(R.string.tv_loading))






}
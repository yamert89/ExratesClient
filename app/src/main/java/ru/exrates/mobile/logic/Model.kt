package ru.exrates.mobile.logic

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.exrates.mobile.ExratesActivity
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.log_d
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.logic.rest.*

class Model(private val app: MyApp, private val activity: ExratesActivity) {

    fun getActualExchange(payload: ExchangePayload, callback: ExCallback<Exchange> = OneExchangeCallback(activity)){
        log_d("REQUEST: actual exchange")
        app.restService.getExchange(payload).enqueue(callback)
    }

    fun getActualPair(c1: String, c2: String, historyinterval: String, limit: Int){
        log_d("REQUEST: actual pair")
        app.restService.getPair(c1, c2, historyinterval, limit).enqueue(PairCallback(activity))
    }

    fun getActualPair(c1: String, c2: String, limit: Int){
        log_d("REQUEST: actual pair")
        app.restService.getPair(c1, c2, truncateLimit(limit)).enqueue(PairCallback(activity))
    }



    fun getLists(){
        log_d("REQUEST: lists")
        app.restService.lists().enqueue(ListsCallback(activity))
    }

    fun ping(){
        app.restService.ping().enqueue(object : Callback<String>{
            override fun onFailure(call: Call<String>, t: Throwable) {

            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.code() != 200) activity.toast("Не удалось подключиться к серверу")
            }

        })
    }

    fun getPriceHistory(pname: String, exchId: Int, historyinterval: String, limit: Int){
        log_d("REQUEST: price history")
        app.restService.getPriceHistory(pname, exchId, historyinterval, truncateLimit(limit)).enqueue(HistoryCallback(activity))
    }

    private fun truncateLimit(limit: Int): Int{
        if (app.currentInterval.isEmpty()) return limit
        return when(app.currentInterval.last()){
            'w' -> 15
            'M' -> 12
            else -> limit
        }
    }


}
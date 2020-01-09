package ru.exrates.mobile.logic

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.exrates.mobile.ExratesActivity
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.logic.rest.*

class Model(private val app: MyApp, private val activity: ExratesActivity) {

    fun getActualExchange(payload: ExchangePayload, callback: ExCallback<Exchange> = OneExchangeCallback(activity)){
        app.restService.getExchange(payload).enqueue(callback)
    }

    fun getActualPair(pname: String, historyinterval: String){
        app.restService.getPair(pname, historyinterval).enqueue(PairCallback(activity))
    }

    fun getActualPair(pname: String){
        app.restService.getPair(pname).enqueue(PairCallback(activity))
    }



    fun getLists(){
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

    fun getPriceHistory(pname: String, exchname: String, historyinterval: String){
        app.restService.getPriceHistory(pname, exchname, historyinterval).enqueue(HistoryCallback(activity))
    }


}
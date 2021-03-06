package ru.exrates.mobile.logic.rest

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.exrates.mobile.view.ExratesActivity
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.logic.logD
import ru.exrates.mobile.presenters.Presenter

class ActivityRestModel(private val app: MyApp,
                        private val activity: ExratesActivity,
                        private val presenter: Presenter) : BaseActivityRestModel(app, activity, presenter){

    override fun getActualExchange(payload: ExchangePayload, callback: ExCallback<Exchange>){
        logD("REQUEST: actual exchange: $payload")
        app.restService.getExchange(payload).enqueue(callback)
    }

    override fun getPriceChange(exchange: Exchange){
        val pairs = exchange.pairs.map { it.symbol }.toTypedArray()
        exchange.changePeriods.forEach {
            logD("REQUEST: price change: exId=${exchange.exId}, $it, ${pairs.joinToString()}")
            app.restService.getPriceChangeBySingleInterval(ExchangePayload(exchange.exId, it, pairs)).enqueue(CursPeriodCallback(activity, presenter))
        }
    }

    override fun getActualPair(c1: String, c2: String, historyinterval: String, limit: Int){
        logD("REQUEST: actual pair: $c1, $c2, interval=$historyinterval, limit=$limit")
        app.restService.getPair(c1, c2, historyinterval, limit).enqueue(PairCallback(activity, presenter))
    }

    override fun getActualPair(c1: String, c2: String, limit: Int){
        logD("REQUEST: actual pair: $c1, $c2, limit=$limit")
        app.restService.getPair(c1, c2, truncateLimit(limit)).enqueue(PairCallback(activity, presenter))
    }

    override fun addOnePair(c1: String, c2: String, exId: Int, currentInterval: String){
        logD("REQUEST: add one pair: $c1, $c2, ex $exId")
        app.restService.addOnePair(c1, c2, exId, currentInterval).enqueue(OnePairCallback(activity, presenter))
    }

    override fun getLists(){
        logD("REQUEST: lists")
        app.restService.lists().enqueue(ListsCallback(activity, presenter))
    }

    override fun ping(){
        app.restService.ping().enqueue(object : Callback<String>{
            override fun onFailure(call: Call<String>, t: Throwable) {

            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.code() != 200) activity.toast("Не удалось подключиться к серверу")
            }

        })
    }

    override fun getPriceHistory(c1: String, c2: String, exchId: Int, historyinterval: String, limit: Int){
        logD("REQUEST: price history: $c1, $c2, exId: $exchId, historyinterval: $historyinterval, limit: $limit")
        app.restService.getPriceHistory(c1, c2, exchId, historyinterval, truncateLimit(limit)).enqueue(HistoryCallback(activity, presenter))
    }

    override fun checkMessages(versionToken: String) {
        app.restService.check(versionToken).enqueue(CheckCallback(activity, presenter))
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
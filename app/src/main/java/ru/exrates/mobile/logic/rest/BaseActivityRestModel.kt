package ru.exrates.mobile.logic.rest

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.logic.logD
import ru.exrates.mobile.presenters.Presenter
import ru.exrates.mobile.view.ExratesActivity

abstract class BaseActivityRestModel(private val app: MyApp,
                                     private val activity: ExratesActivity,
                                     private val presenter: Presenter
): RestModel {
    abstract fun getActualExchange(payload: ExchangePayload, callback: ExCallback<Exchange> = OneExchangeCallback(activity, presenter))

    abstract fun getPriceChange(exchange: Exchange)

    abstract fun getActualPair(c1: String, c2: String, historyinterval: String, limit: Int)

    abstract fun getActualPair(c1: String, c2: String, limit: Int)

    abstract fun addOnePair(c1: String, c2: String, exId: Int, currentInterval: String)

    abstract fun getLists()

    abstract fun ping()

    abstract fun getPriceHistory(c1: String, c2: String, exchId: Int, historyinterval: String, limit: Int)

    abstract fun checkMessages(versionToken: String)

}
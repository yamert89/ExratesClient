package ru.exrates.mobile.logic

import ru.exrates.mobile.ExratesActivity
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.logic.rest.ExCallback
import ru.exrates.mobile.logic.rest.OneExchangeCallback
import ru.exrates.mobile.logic.rest.PairCallback

class Model(private val app: MyApp, private val activity: ExratesActivity) {

    fun getActualExchange(payload: ExchangePayload, callback: ExCallback<Exchange> = OneExchangeCallback(activity)){
        app.restService.getExchange(payload).enqueue(callback)
    }

    fun getActualPair(pName: String){
        app.restService.getPair(pName).enqueue(PairCallback(activity))
    }


}
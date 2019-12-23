package ru.exrates.mobile.logic.rest

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.exrates.mobile.ExratesActivity
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange


abstract class ExCallback<T>(protected val activity: ExratesActivity): Callback<T> {
    override fun onFailure(call: Call<T>, t: Throwable) {
        t.printStackTrace()
        //Log.e("EXRATES", t.message)
    }

    fun <T> mainFunc(ob: T?, func: (ob: T) -> Unit){
        func(ob ?: throw IllegalStateException("Response is null"))
    }
}

class ExchangesCallback(activity: ExratesActivity):ExCallback<Map<String, Exchange>>(activity) {
    override fun onResponse(call: Call<Map<String, Exchange>>, response: Response<Map<String, Exchange>>) {
        //mainActivity.updateExchangeData(response.body() ?: throw IllegalStateException("Response is null"))
    }

}

class OneExchangeCallback(activity: ExratesActivity) : ExCallback<Exchange>(activity){
    override fun onResponse(call: Call<Exchange>, response: Response<Exchange>) {
        mainFunc(response.body(), activity::updateExchangeData)
    }

}

class PairCallback(activity: ExratesActivity) : ExCallback<Map<String, CurrencyPair>>(activity){
    override fun onResponse(
        call: Call<Map<String, CurrencyPair>>,
        response: Response<Map<String, CurrencyPair>>
    ) {
        mainFunc(response.body(), activity::updatePairData)
    }


}
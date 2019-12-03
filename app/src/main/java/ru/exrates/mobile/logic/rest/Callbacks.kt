package ru.exrates.mobile.logic.rest

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.exrates.mobile.MainActivity
import ru.exrates.mobile.logic.entities.Exchange


abstract class ExCallback<T>(protected val mainActivity: MainActivity): Callback<T> {
    override fun onFailure(call: Call<T>, t: Throwable) {
        t.printStackTrace()
        //Log.e("EXRATES", t.message)
    }

    fun <T> mainFunc(ob: T?, func: (ob: T) -> Unit){
        func(ob ?: throw IllegalStateException("Response is null"))
    }
}

class ExchangesCallback(mainActivity: MainActivity):ExCallback<Map<String, Exchange>>(mainActivity) {
    override fun onResponse(call: Call<Map<String, Exchange>>, response: Response<Map<String, Exchange>>) {
        //mainActivity.updateExchangeData(response.body() ?: throw IllegalStateException("Response is null"))
    }

}

class OneExchangeCallback(mainActivity: MainActivity) : ExCallback<Exchange>(mainActivity){
    override fun onResponse(call: Call<Exchange>, response: Response<Exchange>) {
        mainFunc(response.body(), mainActivity::updateExchangeData)
    }

}
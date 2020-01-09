package ru.exrates.mobile.logic.rest

import android.view.View
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.exrates.mobile.CurrencyActivity
import ru.exrates.mobile.ExratesActivity
import ru.exrates.mobile.MainActivity
import ru.exrates.mobile.log_e
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import java.net.SocketTimeoutException


abstract class ExCallback<T>(protected val activity: ExratesActivity): Callback<T> {
    override fun onFailure(call: Call<T>, t: Throwable) {
        log_e("failed response")
        if (t is SocketTimeoutException) activity.toast("Не удалось подключиться к серверу. Превышено время ожидания ответа")
        else {
            log_e(t.message ?: "")
            activity.toast("Ошибка подключения ${t.message}")
        }
        activity.progressLayout.visibility = View.INVISIBLE

        //Log.e("EXRATES", t.message)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.body() == null) throw IllegalStateException("Response is null : $response ${response.message()} \n ${response.errorBody().toString()}")
    }

    fun <T> mainFunc(ob: T?, func: (ob: T) -> Unit){
        func(ob ?: throw IllegalStateException("Response is null"))
        activity.progressLayout.visibility = View.INVISIBLE
    }
}

class ExchangesCallback(activity: ExratesActivity):ExCallback<Map<String, Exchange>>(activity) {
    override fun onResponse(call: Call<Map<String, Exchange>>, response: Response<Map<String, Exchange>>) {
        //mainActivity.updateExchangeData(response.body() ?: throw IllegalStateException("Response is null"))
    }

}

class OneExchangeCallback(activity: ExratesActivity) : ExCallback<Exchange>(activity){
    override fun onResponse(call: Call<Exchange>, response: Response<Exchange>) {
        super.onResponse(call, response)
        mainFunc(response.body(), activity::updateExchangeData)
    }

}

class PairCallback(activity: ExratesActivity) : ExCallback<MutableList<CurrencyPair>>(activity){
    override fun onResponse(
        call: Call<MutableList<CurrencyPair>>,
        response: Response<MutableList<CurrencyPair>>
    ) {
        super.onResponse(call, response)
        mainFunc(response.body(), activity::updatePairData)
    }
}

class ListsCallback(activity: ExratesActivity) : ExCallback<Map<String, List<String>>>(activity) {
    override fun onResponse(
        call: Call<Map<String, List<String>>>,
        response: Response<Map<String, List<String>>>
    ) {
        super.onResponse(call, response)
        activity as MainActivity
        mainFunc(response.body(), activity::initData)
    }
}

class HistoryCallback(activity: ExratesActivity) : ExCallback<List<Double>>(activity){
    override fun onResponse(call: Call<List<Double>>, response: Response<List<Double>>) {
        super.onResponse(call, response)
        activity as CurrencyActivity
        mainFunc(response.body(), activity::updateGraph)
    }

}

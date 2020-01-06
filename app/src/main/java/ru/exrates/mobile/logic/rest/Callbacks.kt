package ru.exrates.mobile.logic.rest

import android.view.View
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        else activity.toast("Ошибка подключения ${t.message}")
        activity.progressLayout.visibility = View.INVISIBLE

        //Log.e("EXRATES", t.message)
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
        mainFunc(response.body(), activity::updateExchangeData)
    }

}

class PairCallback(activity: ExratesActivity) : ExCallback<MutableList<CurrencyPair>>(activity){
    override fun onResponse(
        call: Call<MutableList<CurrencyPair>>,
        response: Response<MutableList<CurrencyPair>>
    ) {
        mainFunc(response.body(), activity::updatePairData)
    }
}

class ListsCallback(activity: ExratesActivity) : ExCallback<Map<String, List<String>>>(activity) {
    override fun onResponse(
        call: Call<Map<String, List<String>>>,
        response: Response<Map<String, List<String>>>
    ) {
        mainFunc(response.body(), (activity as MainActivity)::initData)
    }

}

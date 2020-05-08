package ru.exrates.mobile.logic.rest

import android.view.View
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangeNamesObject
import ru.exrates.mobile.logic.logE
import ru.exrates.mobile.presenters.CurrencyPresenter
import ru.exrates.mobile.presenters.MainPresenter
import ru.exrates.mobile.presenters.Presenter
import ru.exrates.mobile.view.CurrencyActivity
import ru.exrates.mobile.view.ExratesActivity
import ru.exrates.mobile.view.MainActivity
import java.net.SocketTimeoutException


abstract class ExCallback<T>(private val activity: ExratesActivity, val presenter: Presenter): Callback<T> {
    override fun onFailure(call: Call<T>, t: Throwable) {
        logE("failed response")
        if (t is SocketTimeoutException) activity.toast("Не удалось подключиться к серверу. Превышено время ожидания ответа")
        else {
            logE(t.message ?: "")
            activity.toast("Ошибка подключения ${t.message}")
        }
        activity.progressLayout.visibility = View.INVISIBLE

        //Log.e("EXRATES", t.message)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
       /* when(response.code()){
            404 -> log_e("Page not found")
            else -> log_trace("resp success")
        }*/
        if (response.body() == null) throw IllegalStateException("Response is null : $response ${response.message()} \n ${response.errorBody().toString()}")
    }

    fun <T> mainFunc(ob: T?, func: (ob: T) -> Unit){
        func(ob ?: throw IllegalStateException("Response is null"))
        activity.progressLayout.visibility = View.INVISIBLE
    }
}

/*class ExchangesCallback(activity: ExratesActivity):ExCallback<Map<String, Exchange>>(activity, presenter = ) {
    override fun onResponse(call: Call<Map<String, Exchange>>, response: Response<Map<String, Exchange>>) {
        //mainActivity.updateExchangeData(response.body() ?: throw IllegalStateException("Response is null"))
    }

}*/

class OneExchangeCallback(activity: ExratesActivity, presenter: Presenter) : ExCallback<Exchange>(activity, presenter){
    override fun onResponse(call: Call<Exchange>, response: Response<Exchange>) {
        super.onResponse(call, response)
        mainFunc(response.body(), presenter::updateExchangeData)
    }

}

class PairCallback(activity: ExratesActivity, presenter: Presenter) : ExCallback<MutableList<CurrencyPair>>(activity, presenter){
    override fun onResponse(
        call: Call<MutableList<CurrencyPair>>,
        response: Response<MutableList<CurrencyPair>>
    ) {
        super.onResponse(call, response)
        mainFunc(response.body(), presenter::updatePairData)
    }
}

class ListsCallback(activity: ExratesActivity, presenter: Presenter) : ExCallback<List<ExchangeNamesObject>>(activity, presenter) {
    override fun onResponse(
        call: Call<List<ExchangeNamesObject>>,
        response: Response<List<ExchangeNamesObject>>
    ) {
        super.onResponse(call, response)
        //activity as MainActivity
        presenter as MainPresenter
        mainFunc(response.body(), presenter::initData)
    }
}

class HistoryCallback(activity: ExratesActivity, presenter: Presenter) : ExCallback<List<Double>>(activity, presenter){
    override fun onResponse(call: Call<List<Double>>, response: Response<List<Double>>) {
        super.onResponse(call, response)
        //activity as CurrencyActivity
        presenter as CurrencyPresenter
        if (response.code() == 404) mainFunc(listOf(), presenter::updatePairData)
        mainFunc(response.body(), presenter::updatePairData)
    }

}

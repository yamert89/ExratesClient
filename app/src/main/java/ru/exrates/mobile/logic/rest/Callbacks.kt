package ru.exrates.mobile.logic.rest

import android.view.View
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.CursPeriod
import ru.exrates.mobile.logic.entities.json.ExchangeNamesObject
import ru.exrates.mobile.logic.logE
import ru.exrates.mobile.presenters.*
import ru.exrates.mobile.view.CurrencyActivity
import ru.exrates.mobile.view.ExratesActivity
import ru.exrates.mobile.view.MainActivity
import ru.exrates.mobile.view.dialogs.ConnectionFailed
import java.net.SocketTimeoutException


abstract class ExCallback<T>(private val activity: ExratesActivity, val presenter: Presenter): Callback<T> {
    override fun onFailure(call: Call<T>, t: Throwable) {
        logE("failed basic response")
        if (t is SocketTimeoutException) {
            ConnectionFailed(presenter as BasePresenter).show(activity.supportFragmentManager, "connFailed")
        }
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

class OnePairCallback(activity: ExratesActivity, presenter: Presenter) : ExCallback<CurrencyPair>(activity, presenter){
    override fun onResponse(call: Call<CurrencyPair>, response: Response<CurrencyPair>) {
        super.onResponse(call, response)
        presenter as ExchangePresenter
        mainFunc(response.body(), presenter::addPair)
    }

}

class ListsCallback(activity: ExratesActivity, presenter: Presenter) : ExCallback<MutableMap<Int, ExchangeNamesObject>>(activity, presenter) {
    private var alreadyFailed = false
    override fun onResponse(
        call: Call<MutableMap<Int, ExchangeNamesObject>>,
        response: Response<MutableMap<Int, ExchangeNamesObject>>
    ) {
        super.onResponse(call, response)
        //activity as MainActivity
        presenter as MainPresenter
        mainFunc(response.body(), presenter::initData)
    }

    override fun onFailure(call: Call<MutableMap<Int, ExchangeNamesObject>>, t: Throwable) {
        if (!alreadyFailed) {
            //presenter.resume()
            alreadyFailed = true
        } else super.onFailure(call, t)
    }
}

class HistoryCallback(activity: ExratesActivity, presenter: Presenter) : ExCallback<List<Double>>(activity, presenter){
    override fun onResponse(call: Call<List<Double>>, response: Response<List<Double>>) {
        super.onResponse(call, response)
        //activity as CurrencyActivity
        presenter as CurrencyPresenter
        if (response.code() == 404) mainFunc(listOf(), presenter::updateHistory)
        mainFunc(response.body(), presenter::updateHistory)
    }

    override fun onFailure(call: Call<List<Double>>, t: Throwable) {
        logE("FAILED history request")
    }
}

class CursPeriodCallback(activity: ExratesActivity, presenter: Presenter) : ExCallback<CursPeriod>(activity, presenter){
    override fun onResponse(call: Call<CursPeriod>, response: Response<CursPeriod>) {
        super.onResponse(call, response)
        presenter as ExchangePresenter
        mainFunc(response.body(), presenter::updateChangePeriod)
    }

    override fun onFailure(call: Call<CursPeriod>, t: Throwable) {
        logE("FAILED curs period request")
    }
}
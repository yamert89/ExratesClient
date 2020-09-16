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
import ru.exrates.mobile.logic.logT
import ru.exrates.mobile.presenters.*
import ru.exrates.mobile.view.ExratesActivity
import ru.exrates.mobile.view.dialogs.ConnectionFailed
import java.net.SocketTimeoutException


abstract class ExCallback<T>(protected val activity: ExratesActivity, val presenter: Presenter): Callback<T> {
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
        logT("Response of ${call.request().url}\n${response.body()}")
    }

    fun <T> mainFunc(ob: T, func: (ob: T) -> Unit){
        func(ob)
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
        val ex = response.body()!!
        when(ex.status){
            ClientCodes.SUCCESS -> mainFunc(ex, presenter::updateExchangeData)
            ClientCodes.EXCHANGE_NOT_ACCESSIBLE -> {
                activity.toast("Server of ${ex.name} is not responding")
                presenter.handleError {
                    it.inactiveExchanges.add(ex.name)
                }
            }
            else -> logE("unknown resp status ${ex.status}")
        }
    }

}

class PairCallback(activity: ExratesActivity, presenter: Presenter) : ExCallback<MutableList<CurrencyPair>>(activity, presenter){
    override fun onResponse(
        call: Call<MutableList<CurrencyPair>>,
        response: Response<MutableList<CurrencyPair>>
    ) {
        super.onResponse(call, response)
        mainFunc(response.body()!!, presenter::updatePairData)
    }
}

class OnePairCallback(activity: ExratesActivity, presenter: Presenter) : ExCallback<CurrencyPair>(activity, presenter){
    override fun onResponse(call: Call<CurrencyPair>, response: Response<CurrencyPair>) {
        super.onResponse(call, response)
        presenter as ExchangePresenter
        val pair = response.body()!!
        when(pair.status){
            ClientCodes.SUCCESS -> mainFunc(pair, presenter::addPair)
            ClientCodes.EXCHANGE_NOT_ACCESSIBLE -> activity.toast("Server of ${pair.exchangeName} is not responding")
            else -> logE("unknown resp status ${pair.status}")
        }

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
        mainFunc(response.body()!!, presenter::initData)
    }

    override fun onFailure(call: Call<MutableMap<Int, ExchangeNamesObject>>, t: Throwable) {
        if (!alreadyFailed) {
            //presenter.resume() //todo
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
        if (response.body()?.size == 2 && response.body()!![0] == ClientCodes.EXCHANGE_NOT_ACCESSIBLE.toDouble()) activity.toast("Server of ${activity.app.exchangeNamesList[response.body()!![1].toInt()]} is not responding")
        else mainFunc(response.body()!!, presenter::updateHistory)
    }

    override fun onFailure(call: Call<List<Double>>, t: Throwable) {
        logE("FAILED history request")
    }
}

class CursPeriodCallback(activity: ExratesActivity, presenter: Presenter) : ExCallback<CursPeriod>(activity, presenter){
    override fun onResponse(call: Call<CursPeriod>, response: Response<CursPeriod>) {
        super.onResponse(call, response)
        presenter as ExchangePresenter
        if (response.body()!!.status != ClientCodes.SUCCESS) logE("unsuccessful code in cursPeriod response: ${response.body()}")
        else mainFunc(response.body()!!, presenter::updateChangePeriod)
    }

    override fun onFailure(call: Call<CursPeriod>, t: Throwable) {
        logE("FAILED curs period request")
    }
}
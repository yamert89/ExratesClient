package ru.exrates.mobile.presenters

import ru.exrates.mobile.MyApp
import ru.exrates.mobile.data.Storage
import ru.exrates.mobile.logic.CURRENT_CUR_1
import ru.exrates.mobile.logic.CURRENT_CUR_2
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.logD
import ru.exrates.mobile.logic.logTrace
import ru.exrates.mobile.logic.rest.RestModel
import ru.exrates.mobile.view.ExratesActivity
import java.util.*

open class BasePresenter(val app: MyApp) : Presenter{

    var activity: ExratesActivity? = null
    val storage = Storage(app.baseContext, app.om)
    var timer: Timer = Timer()
    lateinit var restModel: RestModel

    override fun task(){
        logTrace("Task started")
    }

    override fun updateExchangeData(exchange: Exchange) {
        logD("Exchange data updated...")
    }

    override fun updatePairData(list: MutableList<CurrencyPair>) {
        logD("Pair data updated...")
    }

    fun currentNameListsIsNull(): Boolean = app.exchangeNamesList == null

    fun currentDataIsNull(): Boolean = app.currentExchange == null || app.currentPairInfo == null

    fun parseSymbol(symbol: String): Pair<String, String>{
        val arr = symbol.split("/")
        return arr[0] to arr[1]
    }

    override fun saveState(){
        logD("saving state....")
        if(currentDataIsNull()) return
        save(
            /*CURRENT_EXCHANGE to app.currentExchange!!.exId,*/
            CURRENT_CUR_1 to app.currentPairInfo!![0].baseCurrency,
            CURRENT_CUR_2 to app.currentPairInfo!![0].quoteCurrency
        )
    }

    fun save(vararg args : Pair<String, Any>){
        args.forEach { storage.storeValue(it.first, it.second) }
        logD("savestate: ${args.joinToString()} objects saved")
    }

    override fun start() {

    }

    override fun stop(){
        saveState()
        timer.cancel()
    }

    override fun pause() {
        stop()
    }

    override fun resume(){
        timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                task()
            }
        }, 75000L, 40000L) //todo period
    }

    override fun destroy() {
        stop()
    }

    override fun attachView(view: ExratesActivity, presenter: Presenter?) {
        activity = view
        restModel = RestModel(app, view, presenter ?: this)
    }

    override fun detachView() {
        activity = null
    }





}
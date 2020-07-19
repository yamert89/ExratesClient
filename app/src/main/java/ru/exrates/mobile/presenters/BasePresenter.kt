package ru.exrates.mobile.presenters

import ru.exrates.mobile.MyApp
import ru.exrates.mobile.data.Storage
import ru.exrates.mobile.logic.*
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.rest.RestModel
import ru.exrates.mobile.view.ExratesActivity
import ru.exrates.mobile.view.viewAdapters.PairsAdapter
import java.util.*

abstract class BasePresenter(val app: MyApp) : Presenter{

    var activity: ExratesActivity? = null
    val storage = Storage(app.baseContext, app.om)
    var timer: Timer = Timer()
    lateinit var restModel: RestModel
    protected lateinit var pairsAdapter: PairsAdapter

    fun pairsAdapterIsInitialized() = this::pairsAdapter.isInitialized

    override fun task(){
        logT("Task started")
    }

    override fun updateExchangeData(exchange: Exchange) {
        app.currentExchange = exchange
        logD("incoming pairs: ${exchange.pairs.joinToString { it.symbol }}")
        pairsAdapter.dataPairs.clear()
        pairsAdapter.dataPairs.addAll(exchange.pairs)
        pairsAdapter.notifyDataSetChanged()
    }

    override fun updatePairData(list: MutableList<CurrencyPair>) {
        logD("Pair data updated...")
    }

    fun currentNameListsIsNull(): Boolean = app.exchangeNamesList == null

    fun currentDataIsNull(): Boolean = app.currentExchange == null || app.currentPairInfo == null

    protected fun savePairsAdapter(){
        val adapterName = when(app.currentExchange?.exId){
            1 -> SAVED_CURRENCIES_ADAPTER_BINANCE
            else -> SAVED_CURRENCIES_ADAPTER_P2PB2B
        }
        save(
            SAVED_CURRENCIES_ADAPTER to adapterName,
            adapterName to pairsAdapter)
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
        stopTimer()
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
        }, 75000L, 80000L) //todo period
    }

    override fun destroy() {
        stop()
    }

    override fun attachView(view: ExratesActivity) {
        activity = view
        restModel = RestModel(app, view, this)
    }

    override fun detachView() {
        activity = null
    }

    fun stopTimer() = timer.cancel()





}
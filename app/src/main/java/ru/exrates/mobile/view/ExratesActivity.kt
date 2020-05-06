package ru.exrates.mobile.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.data.Model
import ru.exrates.mobile.data.Storage
import ru.exrates.mobile.logic.*
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import java.util.*

abstract class ExratesActivity : AppCompatActivity() {
    protected lateinit var app: MyApp
    protected lateinit var storage: Storage
    protected lateinit var timer: Timer
    protected lateinit var model: Model
    lateinit var progressLayout: ConstraintLayout

    open fun updateExchangeData(exchange: Exchange) {
        logD("Exchange data updated...")
    }

    open fun updatePairData(list: MutableList<CurrencyPair>) {
        logD("Pair data updated...")
    }

    open fun startProgress() {
        progressLayout.visibility = View.VISIBLE
    }

    open fun stopProgress(){
        progressLayout.visibility = View.INVISIBLE
    }

    fun currentNameListsIsNull(): Boolean = app.exchangeNamesList == null

    fun currentDataIsNull(): Boolean = app.currentExchange == null || app.currentPairInfo == null

    open fun saveState(){
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

    fun toast(message: String) = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        timer = Timer()
        app = this.application as MyApp
        storage = Storage(applicationContext, app.om)
        logD("Basic exrates activity created")
    }

    override fun onResume() {
        super.onResume()
        timer = Timer()
        timer.schedule(object :  TimerTask(){
            override fun run() {
                task()
            }
        }, 75000L, 40000L) //todo period
    }

    protected abstract fun task()

    override fun onPause() {
        super.onPause()
        logD("root onpause")
        saveState()
        timer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveState()
        timer.cancel()
        logD("root ondestroy")

    }

    override fun onStop() {
        super.onStop()
        logD("root ondestop")
        saveState()
        timer.cancel()
    }



}
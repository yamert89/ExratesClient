package ru.exrates.mobile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import ru.exrates.mobile.logic.Model
import ru.exrates.mobile.logic.Storage
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
        log_d("Exchange data updated...")
    }

    open fun updatePairData(list: MutableList<CurrencyPair>) {
        log_d("Pair data updated...")
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
        log_d("saving state....")
        if(currentDataIsNull()) return
        save(
            CURRENT_EXCHANGE to app.currentExchange!!.exId,
            CURRENT_PAIR to app.currentPairInfo!![0].symbol
        )
    }

    fun save(vararg args : Pair<String, Any>){
        args.forEach { storage.storeValue(it.first, it.second) }
        log_d("savestate: ${args.size} objects saved")
    }

    fun toast(message: String) = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        timer = Timer()
        storage = Storage(applicationContext, app.om)
        app = this.application as MyApp
        log_d("Basic exrates activity created")
    }

    override fun onResume() {
        super.onResume()
        timer = Timer()
        timer.schedule(object :  TimerTask(){
            override fun run() {
                task()
            }
        }, 15000L, 180000L) //todo period
    }

    protected abstract fun task()

    override fun onPause() {
        super.onPause()
        log_d("root onpause")
        saveState()
        timer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveState()
        timer.cancel()
        log_d("root ondestroy")

    }

    override fun onStop() {
        super.onStop()
        log_d("root ondestop")
        saveState()
        timer.cancel()
    }



}
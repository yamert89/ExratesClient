package ru.exrates.mobile

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.internal.MapEntry
import ru.exrates.mobile.logic.Model
import ru.exrates.mobile.logic.Storage
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import java.util.*

abstract class ExratesActivity : AppCompatActivity(){
    protected lateinit var app: MyApp
    protected lateinit var storage: Storage
    protected lateinit var timer: Timer
    protected lateinit var model: Model

    open fun updateExchangeData(exchange: Exchange){log_d("Exchange data updated...")}

    open fun updatePairData(list: List<CurrencyPair>){log_d("Pair data updated...")}

    open suspend fun firstLoadActivity(): Boolean{return true}

    fun currentDataIsNull(): Boolean = app.currentExchange == null || app.currentPairInfo == null

    private fun saveState(){
        log_d("saving state....")
        if(currentDataIsNull()) return
        save(
            MapEntry(CURRENT_EXCHANGE, app.currentExchange!!),
            MapEntry(CURRENT_PAIR_INFO, app.currentPairInfo!!),
            MapEntry(CURRENT_PAIR, app.currentPairInfo!!.iterator().next())
        )

    }

    fun save(vararg args : MapEntry<String, Any>){
        args.forEach { storage.storeValue(it.key, it.value) }
        log_d("savestate: ${args.size} objects saved")
    }

    fun toast(message: String) = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timer = Timer()
        storage = Storage(applicationContext)
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
        saveState()
        timer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveState()
        timer.cancel()

    }

    override fun onStop() {
        super.onStop()
        saveState()
        timer.cancel()
    }





}
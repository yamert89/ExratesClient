package ru.exrates.mobile

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.internal.MapEntry
import ru.exrates.mobile.logic.Storage
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import java.util.*

abstract class ExratesActivity : AppCompatActivity(){
    protected lateinit var app: MyApp
    protected val storage: Storage = Storage(applicationContext)
    protected var timer = Timer()

    abstract fun updateExchangeData(exchange: Exchange)

    abstract fun updatePairData(map: Map<String, CurrencyPair>)

    open suspend fun firstLoadActivity(){}

    fun currentDataIsNull(): Boolean = app.currentExchange == null || app.currentPairInfo == null

    private fun saveState(){
        if(currentDataIsNull()) return
        save(
            MapEntry(CURRENT_EXCHANGE, app.currentExchange!!),
            MapEntry(CURRENT_PAIR_INFO, app.currentPairInfo!!),
            MapEntry(CURRENT_PAIR, app.currentPairInfo!!.iterator().next().value)
        )
        timer.cancel()
    }

    fun save(vararg args : MapEntry<String, Any>){
        args.forEach { storage.storeValue(it.key, it.value) }
        log_d("savestate: ${args.size} objects saved")
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        app = this.application as MyApp
    }

    override fun onPause() {
        super.onPause()
        saveState()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveState()

    }

    override fun onStop() {
        super.onStop()
        saveState()
    }





}
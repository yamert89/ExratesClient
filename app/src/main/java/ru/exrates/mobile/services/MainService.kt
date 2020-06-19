package ru.exrates.mobile.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import ru.exrates.mobile.ExRates
import ru.exrates.mobile.logic.EXTRA_CURRENCY_NAME_1
import ru.exrates.mobile.logic.EXTRA_CURRENCY_NAME_2
import ru.exrates.mobile.logic.EXTRA_PERIOD
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.rest.ServiceModel
import java.util.*

class MainService: Service() {
    private val exRates = ExRates()
    private val serviceModel = ServiceModel(exRates.restService, this)
    val timer = Timer()
    private lateinit var c1: String
    private lateinit var c2: String

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) throw IllegalArgumentException("intent is null")
        c1 = intent.getStringExtra(EXTRA_CURRENCY_NAME_1)
        c2 = intent.getStringExtra(EXTRA_CURRENCY_NAME_2)
        timer.schedule(object : TimerTask(){
            override fun run() {
                task()
            }
        }, 0L, intent.getLongExtra(EXTRA_PERIOD, 180000L))
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun task(){
        serviceModel.getPair(c1, c2, 25) //todo limit ?
    }

    fun updatePair(pairs: MutableList<CurrencyPair>){

    }


}
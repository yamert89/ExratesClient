package ru.exrates.mobile.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import ru.exrates.mobile.ExRates
import ru.exrates.mobile.R
import ru.exrates.mobile.logic.*
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.rest.ServiceModel
import java.util.*
import kotlin.NullPointerException
import kotlin.math.min

class MainService: Service() {
    private val exRates = ExRates()
    private val serviceModel = ServiceModel(exRates.restService, this)
    val timer = Timer()
    private lateinit var c1: String
    private lateinit var c2: String
    private var maxLimit: Double = 0.0
    private var minLimit: Double = 0.0
    private var exId = 0


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) throw IllegalArgumentException("intent is null")
        c1 = intent.getStringExtra(EXTRA_CURRENCY_NAME_1) ?: throw NullPointerException("string extra cur1 not found")
        c2 = intent.getStringExtra(EXTRA_CURRENCY_NAME_2) ?: throw NullPointerException("string extra cur2 not found")
        maxLimit = intent.getDoubleExtra(EXTRA_MAX_LIMIT, 0.0)
        minLimit = intent.getDoubleExtra(EXTRA_MIN_LIMIT, 0.0)
        exId = intent.getIntExtra(EXTRA_EXCHANGE_ID, 1)
        timer.schedule(object : TimerTask(){
            override fun run() {
                task()
            }
        }, 20000L, intent.getLongExtra(EXTRA_PERIOD, 180000L))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "Exrates"
            val descriptionText = "Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(NOTIFICATION_CHANNEL, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
        return START_REDELIVER_INTENT
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
        logD("service task running")
        serviceModel.getPair(c1, c2, 1)
    }

    fun updatePair(pairs: MutableList<CurrencyPair>){
        logD("service callback running")
        val price = pairs.find { it.exId == exId }?.price ?: throw NullPointerException("Service update pair: ex not found")
        if (price > minLimit && price < maxLimit) return
        val notif = Notification.Builder(applicationContext, NOTIFICATION_CHANNEL).setSmallIcon(R.drawable.abt)
            .setContentTitle("Title").setContentText(pairs[0].price.toString()).build()
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(1, notif)
        Toast.makeText(applicationContext, pairs[0].price.toString(), Toast.LENGTH_LONG).show()
    }


}
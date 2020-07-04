package ru.exrates.mobile.services

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import ru.exrates.mobile.ExRates
import ru.exrates.mobile.R
import ru.exrates.mobile.logic.*
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.rest.ServiceModel
import ru.exrates.mobile.view.prefs.ServiceCallbackReceiver
import java.util.*

class MainService: Service(), ServiceCallbackReceiver {
    private val exRates = ExRates()
    private val serviceModel = ServiceModel(exRates.restService, this)
    val timer = Timer()
    private lateinit var c1: String
    private lateinit var c2: String
    private var maxLimit: Float = 0f
    private var minLimit: Float = 0f
    private var exId = 0


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) throw IllegalArgumentException("intent is null")
        c1 = intent.getStringExtra(EXTRA_CURRENCY_NAME_1) ?: throw NullPointerException("string extra cur1 not found")
        c2 = intent.getStringExtra(EXTRA_CURRENCY_NAME_2) ?: throw NullPointerException("string extra cur2 not found")
        maxLimit = intent.getFloatExtra(EXTRA_MAX_LIMIT, 0f)
        minLimit = intent.getFloatExtra(EXTRA_MIN_LIMIT, 0f)
        exId = intent.getIntExtra(EXTRA_EXCHANGE_ID, 1)
        timer.schedule(object : TimerTask(){
            override fun run() {
                task()
            }
        }, 7000L, intent.getLongExtra(EXTRA_PERIOD, 15000L))
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
            logTrace("Notification channel created")
        }
        startForeground(1, Notification.Builder(applicationContext, NOTIFICATION_CHANNEL).setSmallIcon(R.drawable.abt)
            .setContentTitle("Title").setContentText("text").build())
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()

    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    private fun task(){
        logD("service task running")
        serviceModel.getPair(c1, c2, 1)
    }

    fun updatePair(pairs: MutableList<CurrencyPair>){
        logD("service callback running")
        /*val price = pairs.find { it.exId == exId }?.price ?: throw NullPointerException("Service update pair: ex not found")
        if (price > minLimit && price < maxLimit) return*/
        val requestId = System.currentTimeMillis().toInt()
        val flags1 = Intent.FLAG_ACTIVITY_NEW_TASK
        val intent = Intent(applicationContext, StopReceiver::class.java).apply {
            flags = flags1
        }
        val stopIntent = PendingIntent.getBroadcast(applicationContext, requestId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notif = Notification.Builder(applicationContext, NOTIFICATION_CHANNEL).setSmallIcon(R.drawable.abt)
            .setContentTitle("Title").setContentText(pairs[0].price.toString()).setContentIntent(stopIntent).build()
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(1, notif)
        Toast.makeText(applicationContext, pairs[0].price.toString(), Toast.LENGTH_LONG).show()
    }

    /*inner class StopReceiver() : BroadcastReceiver() {
        override fun peekService(myContext: Context?, service: Intent?): IBinder {
            logE("main service peek")
            return super.peekService(myContext, service)

        }

        override fun onReceive(context: Context?, intent: Intent?) {
            stopSelf()
            //stopForeground(false)
            logE("main service stopped")
        }


    }*/
}

class StopReceiver() : BroadcastReceiver() {
    override fun peekService(myContext: Context?, service: Intent?): IBinder {
        logE("main service peek")
        return super.peekService(myContext, service)

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context!!.stopService(Intent(context.applicationContext, MainService::class.java))

        //stopForeground(false)
        logE("main service stopped")
    }


}
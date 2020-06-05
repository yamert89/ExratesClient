package ru.exrates.mobile.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MainService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
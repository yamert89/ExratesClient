package ru.exrates.mobile.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.*

abstract class ExratesActivity : AppCompatActivity() {
    lateinit var app: MyApp
    //private lateinit var presenter: Presenter
    lateinit var progressLayout: ConstraintLayout

    open fun startProgress() {
        progressLayout.visibility = View.VISIBLE
    }

    open fun stopProgress(){
        progressLayout.visibility = View.INVISIBLE
    }

    fun toast(message: String) = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = this.application as MyApp

        logD("Basic exrates activity created")
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        logD("root onpause")

    }

    override fun onDestroy() {
        super.onDestroy()

        logD("root ondestroy")
    }



}
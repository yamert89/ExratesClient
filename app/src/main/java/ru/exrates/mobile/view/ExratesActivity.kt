package ru.exrates.mobile.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.GlobalScope
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.rest.RestModel
import ru.exrates.mobile.data.Storage
import ru.exrates.mobile.logic.*
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.presenters.BasePresenter
import ru.exrates.mobile.presenters.MainPresenter
import ru.exrates.mobile.presenters.Presenter
import java.util.*

abstract class ExratesActivity : AppCompatActivity() {
    protected lateinit var app: MyApp
    private lateinit var presenter: Presenter
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
        presenter.resume()
    }

    override fun onPause() {
        super.onPause()
        logD("root onpause")
        presenter.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
        logD("root ondestroy")
    }



}
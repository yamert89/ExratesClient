package ru.exrates.mobile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.anychart.chart.common.dataentry.ValueDataEntry
import ru.exrates.mobile.logic.Model
import ru.exrates.mobile.logic.Storage
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

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

    fun currentNameListsIsNull(): Boolean = app.exchangeNamesList == null

    fun currentDataIsNull(): Boolean = app.currentExchange == null || app.currentPairInfo == null

    open fun saveState(){
        log_d("saving state....")
        if(currentDataIsNull()) return
        save(
            CURRENT_EXCHANGE to app.currentExchange!!.name,
            CURRENT_PAIR to app.currentPairInfo!![0].symbol
        )
    }

    fun save(vararg args : Pair<String, Any>){
        args.forEach { storage.storeValue(it.first, it.second) }
        log_d("savestate: ${args.size} objects saved")
    }

    fun toast(message: String) = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()

    fun createChartValueDataList(priceHistory: List<Double>): ValueDataList{
        log_d("current interval = ${app.currentInterval}")
        var dateInterval = Duration.ZERO
        var pattern = "HH:mm"
        var xLabel = "hours"
        when(app.currentInterval.last()){
            'm' -> dateInterval = Duration.ofMinutes(1)
            'h' -> {
                dateInterval = Duration.ofHours(1)
            }
            'd' -> {
                dateInterval = Duration.ofDays(1)
                pattern = "dd"
                xLabel = "days"
            }
            'w' -> {
                dateInterval = Duration.ofDays(7)
                pattern = "dd"
                xLabel = "days"
            }
            'M' -> {
                dateInterval = Duration.ofDays(30)
                pattern = "MMMM"
                xLabel = "months"
            }
        }

        var now = ZonedDateTime.now(ZoneId.systemDefault())
        val dataList = ArrayList<ValueDataEntry>()
        for (element in priceHistory){
            now = now.minus(dateInterval)
            dataList.add(0, ValueDataEntry(now.format(DateTimeFormatter.ofPattern(pattern))  , element))
        }
        return ValueDataList(xLabel, dataList)

    }

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

    data class ValueDataList(val xLabel: String, val dataList: ArrayList<ValueDataEntry>)





}
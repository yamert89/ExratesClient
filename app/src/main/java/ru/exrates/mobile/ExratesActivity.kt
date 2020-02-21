package ru.exrates.mobile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import lecho.lib.hellocharts.model.PointValue
import ru.exrates.mobile.logic.Model
import ru.exrates.mobile.logic.Storage
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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
        //var forLabel = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault())

        var start = ZonedDateTime.now(ZoneId.systemDefault())
        val dataList = ArrayList<PointValue>()
        val labelList = ArrayList<String>()
        val labelValueList = ArrayList<Float>()
        for (element in priceHistory){
            now = now.minus(dateInterval)
            //dataList.add(0, PointValue(now.toEpochSecond().toFloat(), element.toFloat()))
            val y = now.toEpochSecond().toFloat()
            dataList.add(0, PointValue(y, element.toFloat()))
            //labelList.add(now.format(DateTimeFormatter.ofPattern(pattern)))
        }

        val numberOfDateIntervals = 30 //todo
        var count = 6 //6 - empiric needs 5min
        var interval = 30 / 6  //= 5

        val duration = Duration.between(now, start)
        val seconds = duration.seconds
        when {
            seconds < 3600 -> {
                interval = 10
                start = ZonedDateTime.of(start.year, start.monthValue, start.dayOfMonth, start.hour + 1, 0, 0, 0, ZoneId.systemDefault())
            }
            seconds < 86400 -> {
                interval = 1
                start = ZonedDateTime.of(start.year, start.monthValue, start.dayOfMonth, 0, 0, 0, 0, ZoneId.systemDefault())
            }
            else -> {
                interval = 1
            }
        }

        while(start > now){
            start -= Duration.of(interval.toLong(), ChronoUnit.MINUTES)
            labelList.add(start.format(DateTimeFormatter.ofPattern(pattern)))
            labelValueList.add(start.toEpochSecond().toFloat())
        }
        log_d("labels size: ${labelList.size}  l. values size: ${labelValueList.size}")
        log_d("labels: ${labelList.joinToString()}")
        log_d("label values: ${labelValueList.joinToString()}")

        return ValueDataList(dataList, labelList, labelValueList, xLabel)

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

    data class ValueDataList(
        val values: List<PointValue>,
        val xLabels: List<String>,
        val xAxisLabelValues: List<Float>,
        val xAxisLabel: String,
        val yLabels: List<String> = emptyList())





}
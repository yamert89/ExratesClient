package ru.exrates.mobile.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import lecho.lib.hellocharts.view.LineChartView
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.R
import ru.exrates.mobile.logic.rest.RestModel
import ru.exrates.mobile.logic.*
import ru.exrates.mobile.view.graph.GraphFactory
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.SelectedExchange
import ru.exrates.mobile.logic.structures.IntervalComparator
import ru.exrates.mobile.view.viewAdapters.ExchangesAdapter
import java.util.*

class CurrencyActivity : ExratesActivity() {
    private lateinit var currencyName: TextView
    private lateinit var currencyInterval: Button
    private lateinit var currencyIntervalValue: TextView
    private lateinit var anyChartView: LineChartView
    //private lateinit var currencyExchange: TextView
    private lateinit var currencyExchanges: RecyclerView
    private lateinit var exchangesAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var historyPeriodSpinner: Spinner
    private lateinit var root: ConstraintLayout
    private lateinit var curIco : ImageView
    private var intervals: TreeSet<String> = TreeSet(IntervalComparator())
    private var currentInterval = ""
    private var currentGraphInterval = ""
    private var currentGraphIntervalIdx = 0
    private var selectedExchange = SelectedExchange(1)

        //private var activeExchangeName = "binanceExchange"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*try{
            setContentView(R.layout.currency)
            app = this.application as MyApp
            //currencyExchange = findViewById(R.id.cur_exchange)
            currencyName = findViewById(R.id.cur_name)
            currencyInterval = findViewById(R.id.cur_interval)
            currencyIntervalValue = findViewById(R.id.cur_intervalValue)
            progressLayout = findViewById(R.id.progressLayout)
            historyPeriodSpinner = findViewById(R.id.cur_history_period)
            anyChartView = findViewById(R.id.anyChartView_cur)
            curIco = findViewById(R.id.cur_ico)
            root = findViewById(R.id.currency)
            //storage = Storage(applicationContext)


            restModel = RestModel(app, this)

            val currName1: String = intent.getStringExtra(EXTRA_CURRENCY_NAME_1)!!
            val currName2: String = intent.getStringExtra(EXTRA_CURRENCY_NAME_2)!!
            //app.currentExchangeId = intent.getIntExtra(EXTRA_EXCHANGE_ID, 1)
            selectedExchange.id = intent.getIntExtra(EXTRA_EXCHANGE_ID, 1) //todo replace with strorage saved_exid
            currentGraphInterval = storage.getValue(
                CURRENT_GRAPH_INTERVAL,
                app.currentPairInfo!!.find { selectedExchange.id == it.exId }?.historyPeriods?.get(0) ?: "1h"
            )

            restModel.getActualPair(currName1, currName2, currentGraphInterval,
                CURRENCY_HISTORIES_CUR_NUMBER
            )

            //updateIntervals()

            logD(intervals.joinToString())

            if(currentNameListsIsNull()){
                currentInterval = storage.getValue(CURRENT_INTERVAL, intervals.first())
                logD("Loaded saved pair data from storage")
            }
            if (currentNameListsIsNull()) throw NullPointerException("current data is null")

            val curIcoId = intent.getIntExtra(EXTRA_CUR_ICO, 0)
            curIco.setImageDrawable(ResourcesCompat.getDrawable(app.resources, curIcoId, null))

            historyPeriodSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)


            //historyPeriodSpinner.setSelection(currentGraphIntervalIdx)
            historyPeriodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?){}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val interval = parent?.getItemAtPosition(position) as String
                    currentGraphInterval = interval
                    currentGraphIntervalIdx = position
                    restModel.getPriceHistory(currName1, currName2, selectedExchange.id, interval,
                        CURRENCY_HISTORIES_CUR_NUMBER
                    )
                    val key = "$CURRENT_GRAPH_INTERVAL_IDX${selectedExchange.id}${app.currentPairInfo!![0].symbol}"
                    storage.storeValue(key, position )
                    logD("Graph interval saved with key $key and value $position")

                }
            }

            currencyName.text = "$currName1 / $currName2"

            exchangesAdapter =
                ExchangesAdapter(
                    app.currentPairInfo ?: mutableListOf(),
                    restModel,
                    app,
                    app.currentPairInfo?.get(0)!!.historyPeriods?.get(0)!!,
                    selectedExchange
                )
            viewManager = LinearLayoutManager(this)

            currencyExchanges = findViewById<RecyclerView>(R.id.cur_exchanges).apply{
                adapter = exchangesAdapter
                layoutManager = viewManager

            }

            //currencyIntervalValue.text = intervals.first()

            currencyInterval.setOnClickListener {
                val interval = if(currentDataIsNull()) app.currentPairInfo?.get(0)!!.historyPeriods?.get(0)!! else
                    intervals.higher(currentInterval)
                        ?: app.currentPairInfo!![0].priceChange.firstKey()
                currencyIntervalValue.text = interval
                currentInterval = interval
                val adapter = currencyExchanges.adapter as ExchangesAdapter
                adapter.interval = currentInterval
                adapter.notifyDataSetChanged()

            }
            selectedExchange.listener = {
                val historyAdapter = historyPeriodSpinner.adapter as ArrayAdapter<String>
                historyAdapter.clear()
                historyAdapter.addAll(
                    app.currentPairInfo?.find { it.exId == selectedExchange.id }?.historyPeriods!!
                )
                historyAdapter.notifyDataSetChanged()
                val key = "$CURRENT_GRAPH_INTERVAL_IDX${selectedExchange.id}${app.currentPairInfo!![0].symbol}"
                currentGraphIntervalIdx = storage.getValue(key, 0)
                historyPeriodSpinner.setSelection(currentGraphIntervalIdx)
            }

        }catch (e: Exception){
            Log.d(null, "Current activity start failed", e)
        }*/

    }


    private fun updateIntervals(){
        /*app.currentPairInfo!!.forEach { //todo mb null?
            intervals.addAll(it.historyPeriods!!.subtract(intervals))
        }
        val interval = intervals.first()
        currentInterval = interval
        currencyIntervalValue.text = interval
        val pairSymbol = app.currentPairInfo!![0].symbol
        val key = "$CURRENT_GRAPH_INTERVAL_IDX${selectedExchange.id}$pairSymbol"
        currentGraphIntervalIdx = storage.getValue(key, 0) //todo sync with currentGraphInterval
        currentGraphInterval = storage.getValue(CURRENT_GRAPH_INTERVAL, "1h")
        logD("Graph interval loaded with key $key and value $currentGraphIntervalIdx")*/

    }

    /*override fun updatePairData(list: MutableList<CurrencyPair>) {
        super.updatePairData(list)
        app.currentPairInfo = list
        updateIntervals()
        val adapter = currencyExchanges.adapter as ExchangesAdapter
        adapter.interval = currencyIntervalValue.text.toString()
        adapter.pairsByExchanges.clear()
        adapter.pairsByExchanges.addAll(list)
        adapter.notifyDataSetChanged()
        val pair = list.find { it.exId == selectedExchange.id }
        updateGraph(pair?.priceHistory ?: throw NullPointerException("pair not found in exId: ${selectedExchange.id}, and pairData: ${list.joinToString()}"))
        if (historyPeriodSpinner.adapter.isEmpty) {
            val historyAdapter = historyPeriodSpinner.adapter as ArrayAdapter<String>
            historyAdapter.clear()
            historyAdapter.addAll(
                list[0].historyPeriods!! //fixme
            )//todo not null?
            historyAdapter.notifyDataSetChanged()
            historyPeriodSpinner.setSelection(currentGraphIntervalIdx)
        }

        val cur = list.find { it.exId == selectedExchange.id }!!

        if (cur.priceHistory.isEmpty()) {
            root.removeView(anyChartView)
            val notice = TextView(app.baseContext).apply {
                text = "Data not available"
            }
            root.addView(notice, 2)
        } else {
            GraphFactory(
                anyChartView,
                currentGraphInterval
            ).createBigGraph(cur.priceHistory)
            // anyChartView.setChart(GraphFactory(anyChartView).getBigGraph(dataList))
            //set.data(dataList as List<ValueDataEntry>)
            logD("updating graph from pairData with ${cur.priceHistory.joinToString()}")
        }



    }

    fun updateGraph(list: List<Double>){
        //val data = mutableListOf<ValueDataEntry>()
        if (list.isEmpty()) {
            root.removeView(anyChartView)
            val notice = TextView(app.baseContext).apply {
                text = "Data not available"
            }
            root.addView(notice, 2)
        } else {
            GraphFactory(
                anyChartView,
                currentGraphInterval
            ).createBigGraph(list)


            //list.forEach { data.add(ValueDataEntry("1", it)) }
            logD("updating graph with ${list.joinToString()}")

            //set.data(dataList as List<ValueDataEntry>)
            //anyChartView.setChart(GraphFactory(anyChartView).getBigGraph(dataList))
            *//*val historyAdapter = historyPeriodSpinner.adapter as ArrayAdapter<String>
            historyAdapter.addAll(list.mapTo(mutableListOf<String>(), {it.toString()}))
            historyAdapter.notifyDataSetChanged()*//*
        }

    }

    override fun task() {
        logD("task cur activ started with cur1: ${app.currentCur1}, cur2: ${app.currentCur2}, curGraphInterval: $currentGraphInterval")
        restModel.getActualPair(app.currentCur1, app.currentCur2, currentGraphInterval,
            CURRENCY_HISTORIES_CUR_NUMBER
        )
    }

    override fun saveState() {
        super.saveState()
        storage.storeValue(CURRENT_GRAPH_INTERVAL, currentGraphInterval)
        storage.storeValue(CURRENT_GRAPH_INTERVAL_IDX, currentGraphIntervalIdx)

    }*/






}
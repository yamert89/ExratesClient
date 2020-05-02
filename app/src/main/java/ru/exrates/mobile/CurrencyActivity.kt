package ru.exrates.mobile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import lecho.lib.hellocharts.view.LineChartView
import ru.exrates.mobile.graph.GraphFactory
import ru.exrates.mobile.logic.Model
import ru.exrates.mobile.logic.Storage
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.structures.IntervalComparator
import ru.exrates.mobile.viewadapters.ExchangesAdapter
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
    private var intervals: MutableSet<String> = TreeSet(IntervalComparator())
    private var currentInterval = "" //TODO app.curInt
    private var currentGraphInterval = "" //todo?
    private var currentGraphIntervalIdx = 0
        //private var activeExchangeName = "binanceExchange"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try{
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
            currentGraphInterval = storage.getValue(CURRENT_GRAPH_INTERVAL, app.currentExchange!!.historyPeriods.first())
            currentGraphIntervalIdx = storage.getValue(CURRENT_GRAPH_INTERVAL_IDX, 0)

            model = Model(app, this)

            updateIntervals()

            log_d(intervals.joinToString())

            if(currentNameListsIsNull()){
                currentInterval = storage.getValue(CURRENT_INTERVAL, intervals.first())
                log_d("Loaded saved pair data from storage")
            }
            if (currentNameListsIsNull()) throw NullPointerException("current data is null")


            val currName1: String = intent.getStringExtra(EXTRA_CURRENCY_NAME_1)!!
            val currName2: String = intent.getStringExtra(EXTRA_CURRENCY_NAME_2)!!
            //app.currentExchangeId = intent.getIntExtra(EXTRA_EXCHANGE_ID, 1)
            val curIcoId = intent.getIntExtra(EXTRA_CUR_ICO, 0)
            curIco.setImageDrawable(ResourcesCompat.getDrawable(app.resources, curIcoId, null))


            model.getActualPair(currName1, currName2, currentGraphInterval, CURRENCY_HISTORIES_CUR_NUMBER)

            historyPeriodSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)


            historyPeriodSpinner.setSelection(currentGraphIntervalIdx)
            historyPeriodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?){}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val interval = parent?.getItemAtPosition(position) as String
                    currentGraphInterval = interval
                    currentGraphIntervalIdx = position
                    model.getPriceHistory(currName1, currName2, app.currentExchange!!.exId, interval, CURRENCY_HISTORIES_CUR_NUMBER)

                }
            }

            currencyName.text = "$currName1 / $currName2"

            exchangesAdapter = ExchangesAdapter(app.currentPairInfo ?: mutableListOf(), model, currentInterval)
            viewManager = LinearLayoutManager(this)

            currencyExchanges = findViewById<RecyclerView>(R.id.cur_exchanges).apply{
                adapter = exchangesAdapter
                layoutManager = viewManager

            }

            currencyIntervalValue.text = intervals.first()

            currencyInterval.setOnClickListener {
                val interval = if(currentDataIsNull()) app.currentExchange!!.historyPeriods[0] else
                    app.currentPairInfo!!.find { it.exId == app.currentExchange!!.exId }!!
                        .priceChange.higherKey(currentInterval)
                        ?: app.currentPairInfo!![0].priceChange.firstKey()
                currencyIntervalValue.text = interval
                currentInterval = interval
                val adapter = currencyExchanges.adapter as ExchangesAdapter
                adapter.interval = currentInterval
                adapter.notifyDataSetChanged()

            }






        }catch (e: Exception){
            Log.d(null, "Current activity start failed", e)
        }

    }

    private fun updateIntervals(){
        app.currentPairInfo!!.forEach {
            intervals.addAll(it.historyPeriods!!.subtract(intervals))
        }
        val interval = intervals.first()
        currentInterval = interval
        currencyIntervalValue.text = interval

    }

    override fun updatePairData(list: MutableList<CurrencyPair>) {
        super.updatePairData(list)
        app.currentPairInfo = list
        updateIntervals()
        val adapter = currencyExchanges.adapter as ExchangesAdapter
        adapter.interval = currencyIntervalValue.text.toString()
        adapter.pairsByExchanges.clear()
        adapter.pairsByExchanges.addAll(list)
        adapter.notifyDataSetChanged()
        val pair = list.find { it.exId == app.currentExchange?.exId }
        updateGraph(pair?.priceHistory ?: throw NullPointerException("pair not found in exId: ${app.currentExchange?.exId}, and pairData: ${list.joinToString()}"))
        if (historyPeriodSpinner.adapter.isEmpty) {
            val historyAdapter = historyPeriodSpinner.adapter as ArrayAdapter<String>
            historyAdapter.clear()
            historyAdapter.addAll(
                app.currentExchange?.historyPeriods ?: list[0].historyPeriods!!
            )//todo not null?
            historyAdapter.notifyDataSetChanged()
            historyPeriodSpinner.setSelection(currentGraphIntervalIdx)
        }

        val cur = list.find { it.exId == app.currentExchange?.exId }!!

        if (cur.priceHistory.isEmpty()) {
            root.removeView(anyChartView)
            val notice = TextView(app.baseContext).apply {
                text = "Data not available"
            }
            root.addView(notice, 2)
        } else {
            GraphFactory(anyChartView, currentGraphInterval).createBigGraph(cur.priceHistory)
            // anyChartView.setChart(GraphFactory(anyChartView).getBigGraph(dataList))
            //set.data(dataList as List<ValueDataEntry>)
            log_d("updating graph from pairData with ${cur.priceHistory.joinToString()}")
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
            GraphFactory(anyChartView, currentGraphInterval).createBigGraph(list)


            //list.forEach { data.add(ValueDataEntry("1", it)) }
            log_d("updating graph with ${list.joinToString()}")

            //set.data(dataList as List<ValueDataEntry>)
            //anyChartView.setChart(GraphFactory(anyChartView).getBigGraph(dataList))
            /*val historyAdapter = historyPeriodSpinner.adapter as ArrayAdapter<String>
            historyAdapter.addAll(list.mapTo(mutableListOf<String>(), {it.toString()}))
            historyAdapter.notifyDataSetChanged()*/
        }

    }

    override fun task() {
        log_d("task cur activ started with cur1: ${app.currentCur1}, cur2: ${app.currentCur2}, curGraphInterval: $currentGraphInterval")
        model.getActualPair(app.currentCur1, app.currentCur2, currentGraphInterval, CURRENCY_HISTORIES_CUR_NUMBER)
    }

    override fun saveState() {
        super.saveState()
        storage.storeValue(CURRENT_GRAPH_INTERVAL, currentGraphInterval)
        storage.storeValue(CURRENT_GRAPH_INTERVAL_IDX, currentGraphIntervalIdx)

    }






}
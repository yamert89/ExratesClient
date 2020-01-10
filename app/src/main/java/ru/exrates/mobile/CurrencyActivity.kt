package ru.exrates.mobile

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.ValueDataEntry
import ru.exrates.mobile.graph.GraphFactory
import ru.exrates.mobile.logic.Model
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.viewadapters.ExchangesAdapter

class CurrencyActivity : ExratesActivity() {
    private lateinit var currencyName: TextView
    private lateinit var currencyInterval: Button
    private lateinit var currencyIntervalValue: TextView
    private lateinit var anyChartView: AnyChartView
    private var currentInterval = "1h"
    private lateinit var currencyExchange: TextView
    private lateinit var currencyExchanges: RecyclerView
    private lateinit var exchangesAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var historyPeriodSpinner: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try{
            setContentView(R.layout.currency)
            app = this.application as MyApp
            currencyExchange = findViewById(R.id.cur_exchange)
            currencyName = findViewById(R.id.cur_name)
            currencyInterval = findViewById(R.id.cur_interval)
            currencyIntervalValue = findViewById(R.id.cur_intervalValue)
            progressLayout = findViewById(R.id.progressLayout)
            historyPeriodSpinner = findViewById(R.id.cur_history_period)
            anyChartView = findViewById(R.id.anyChartView_cur)

            model = Model(app, this)

            if(currentNameListsIsNull()){
                currentInterval = storage.getValue(CURRENT_INTERVAL, "1h")
                log_d("Loaded saved pair data from storage")
            }
            if (currentNameListsIsNull()) throw NullPointerException("current data is null")


            val currName: String = intent.getStringExtra(EXTRA_CURRENCY_NAME)!!
            val defExchName = intent.getStringExtra(EXTRA_EXCHANGE_NAME)!!

            model.getActualPair(currName)

            historyPeriodSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)

            historyPeriodSpinner.setSelection(0)
            historyPeriodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?){}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val interval = parent?.getItemAtPosition(position) ?: throw NullPointerException("item on position $position not found")
                    model.getPriceHistory(currName, defExchName, interval as String)

                }
            }

            currencyName.text = currName

            exchangesAdapter = ExchangesAdapter(app.currentPairInfo ?: mutableListOf(), currName, currentInterval)
            viewManager = LinearLayoutManager(this)

            currencyExchanges = findViewById<RecyclerView>(R.id.cur_exchanges).apply{
                adapter = exchangesAdapter
                layoutManager = viewManager

            }

            currencyInterval.setOnClickListener {
                currencyIntervalValue.text = if(currentDataIsNull()) "1h" else
                    app.currentPairInfo!![0].priceChange.higherKey(currencyIntervalValue.text.toString()) ?: app.currentPairInfo!![0].priceChange.firstKey()
                val adapter = currencyExchanges.adapter as ExchangesAdapter
                adapter.interval = currencyIntervalValue.text.toString()
                adapter.notifyDataSetChanged()

            }

        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    override fun updatePairData(list: MutableList<CurrencyPair>) {
        super.updatePairData(list)
        app.currentPairInfo = list
        val adapter = currencyExchanges.adapter as ExchangesAdapter
        adapter.interval = currencyIntervalValue.text.toString()
        adapter.pairsByExchanges.clear()
        adapter.pairsByExchanges.addAll(list)
        adapter.notifyDataSetChanged()
        val pair = list.find { it.exchangeName == (app.currentExchangeName } //todo
        updateGraph(pair?.priceHistory ?: throw NullPointerException("pair not found in updatePairData"))
        val historyAdapter = historyPeriodSpinner.adapter as ArrayAdapter<String>
        historyAdapter.clear()
        historyAdapter.addAll(app.currentExchange?.historyPeriods ?: list[0].historyPeriods!!)//todo not null?
        historyAdapter.notifyDataSetChanged()

    }

    fun updateGraph(list: List<Double>){
        val data = mutableListOf<ValueDataEntry>()
        list.forEach { data.add(ValueDataEntry("1", it)) }
        anyChartView = GraphFactory(anyChartView).getBigGraph(data)
        /*val historyAdapter = historyPeriodSpinner.adapter as ArrayAdapter<String>
        historyAdapter.addAll(list.mapTo(mutableListOf<String>(), {it.toString()}))
        historyAdapter.notifyDataSetChanged()*/
    }

    override fun task() {
        model.getActualPair(currencyName.text.toString())
    }




}
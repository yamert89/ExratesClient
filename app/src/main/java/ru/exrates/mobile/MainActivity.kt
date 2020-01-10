package ru.exrates.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import ru.exrates.mobile.graph.GraphFactory
import ru.exrates.mobile.logic.Model
import ru.exrates.mobile.logic.Storage
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.viewadapters.PairsAdapter

class MainActivity : ExratesActivity() {
//fixme Skipped 39 frames!  The application may be doing too much work on its main thread.
    private lateinit var currencyName: Spinner
    private lateinit var currencyPrice: TextView
    private lateinit var exchangeName: Spinner
    private lateinit var currenciesRecyclerView: RecyclerView
    private lateinit var anyChartView: AnyChartView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var pairsAdapter: PairsAdapter
    private lateinit var curAdapter: ArrayAdapter<String>
    private lateinit var exchAdapter: ArrayAdapter<String>
    private var curIdx = 0
    private var exIdx = 0
    private var cur: CurrencyPair? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            storage = Storage(applicationContext)

            model = Model(app, this)

            currencyName = findViewById(R.id.main_currency_spinner)
            currencyPrice = findViewById(R.id.main_cur_price)
            exchangeName = findViewById(R.id.main_exch_spinner)
            progressLayout = findViewById(R.id.progressLayout)
            anyChartView = findViewById(R.id.anyChartView)

            curAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
            exchAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)

            curAdapter.add("Currency")
            exchAdapter.add("Exchange")

            currencyName.adapter = curAdapter
            exchangeName.adapter = exchAdapter

            viewManager = LinearLayoutManager(this)

            pairsAdapter = PairsAdapter(mutableListOf())
            currenciesRecyclerView = findViewById<RecyclerView>(R.id.main_cur_list).apply {
                adapter = pairsAdapter
                layoutManager = viewManager
            }

            exchangeName.setSelection(0, true)

            exchangeName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    log_d("items was not be selected")
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val exchName = parent?.getItemAtPosition(position)
                    log_d("item selected pos: $position, name: $exchName")
                    startActivity(Intent(applicationContext, ExchangeActivity::class.java).apply{
                        putExtra(EXTRA_EXCHANGE_NAME, exchName.toString())
                    })
                }

            }

            currencyName.setSelection(0, true)

            currencyName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val curName = parent?.getItemAtPosition(position)
                    log_d("item selected pos: $position, name: $curName")
                    startActivity(Intent(applicationContext, CurrencyActivity::class.java).apply {
                        putExtra(EXTRA_CURRENCY_NAME, curName.toString())
                        putExtra(EXTRA_EXCHANGE_NAME, exchangeName.selectedItem as String)
                    })
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

            //val cartesian = AnyChart.line()
           anyChartView = GraphFactory(anyChartView).getSmallGraph(listOf(
               ValueDataEntry("12", 5.6),
               ValueDataEntry("13", 6.3),
               ValueDataEntry("14", 2.5),
               ValueDataEntry("15", 6.6)
           ))

            startProgress()

            log_d("Main activity created")

        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    override fun updateExchangeData(exchange: Exchange){
        super.updateExchangeData(exchange)
        app.currentExchange = exchange
        val adapter = currenciesRecyclerView.adapter as PairsAdapter
        adapter.dataPairs.clear()
        adapter.dataPairs.addAll(exchange.pairs)
        adapter.notifyDataSetChanged()
    }

    override fun updatePairData(list: MutableList<CurrencyPair>) {
        super.updatePairData(list)
        app.currentPairInfo = list
        var count = 0.0
        list.forEach { count += it.price }
        currencyPrice.text = (count / list.size).toNumeric()
    }

    override fun task() {
        log_d("task")
        if (currentDataIsNull()){
            log_d("current data  is null")
            return
        }
        model.getActualExchange(ExchangePayload(
            app.currentExchange!!.name,
            app.currentInterval,
            app.currentExchange!!.pairs.filter{it.visible}.map { it.symbol }.toTypedArray()
        ))
        model.getActualPair(app.currentPairInfo!![0].symbol)
    }

    private suspend fun firstLoadActivity(): Boolean{
        var res = false
        coroutineScope {
            try {
                log_d("before request")
                model.getLists()
            }catch (e: Exception){
                log_e("exception")
                return@coroutineScope
            }
            res = true

        }
        if (res) storage.storeValue(IS_FIRST_LOAD, false)
        return res
    }

    fun initData(lists: Map<String, List<String>>){
        log_d("init data")
        app.exchangeNamesList = lists

        GlobalScope.launch {
            save(SAVED_EXCHANGE_NAME_LIST to lists)
            log_d("list saved")

        }

        log_d("get exchange")
        val defaultExchName = lists.keys.iterator().next()
        model.getActualExchange(ExchangePayload(defaultExchName, app.currentInterval, emptyArray()))
        model.getActualPair(lists.getValue(defaultExchName)[0])
        updateExchangesList(lists.keys)
        updateCurrenciesList(lists.getValue(defaultExchName))

    }

    private fun updateExchangesList(exchangeNames: Set<String>?){
        if (exchangeNames == null) return
        log_d( "exchanges: $exchangeNames")
        with(exchAdapter){clear(); addAll(exchangeNames); notifyDataSetChanged()}
        exchangeName.setSelection(storage.getValue(SAVED_EX_IDX, 0))
    }

    private fun updateCurrenciesList(curNames: List<String>?){
        if (curNames == null) return
        log_d( "curNames : $curNames")
        with(curAdapter){clear(); addAll(curNames); notifyDataSetChanged()}
        currencyName.setSelection(storage.getValue(SAVED_CUR_IDX, 0))
    }

    override fun startProgress(){
        super.startProgress()
        log_d( "Snack started..")
        Snackbar.make(currenciesRecyclerView, "Первичная загрузка данных, подождите", Snackbar.LENGTH_LONG).show()
    }

    override fun saveState() {
        super.saveState()
        save(SAVED_EX_IDX to exchangeName.selectedItemPosition,
            SAVED_CUR_IDX to currencyName.selectedItemPosition)
    }

    override fun onResume() {
        super.onResume()
        try {
            app = this.application as MyApp
            var flag = true
            log_d("on resume")
            val listsReq = GlobalScope.launch(Dispatchers.IO) {
                log_d("start coroutine")
                if (storage.getValue(IS_FIRST_LOAD, true)) {
                    log_d("before first load")
                    flag = firstLoadActivity()
                    log_d("flaq is $flag")
                }

                else {
                    log_d("Saved lists loaded")
                    app.exchangeNamesList = storage.loadObject(SAVED_EXCHANGE_NAME_LIST)
                    curIdx = storage.getValue(SAVED_CUR_IDX, 0)
                    exIdx = storage.getValue(SAVED_EX_IDX, 0)
                    val pair = storage.getValue(CURRENT_PAIR, "BTCUSDT") //todo for all activities
                    val exchange = storage.getValue(CURRENT_EXCHANGE, "binanceExchange")
                    app.currentExchangeName = exchange
                    app.currentPairName = pair
                    model.getActualExchange(ExchangePayload(
                        exchange,
                        app.currentInterval,
                        arrayOf(pair))
                    )
                    model.getActualPair(pair)


                }

            }

            runBlocking { listsReq.join() }
            if (!flag) {
                toast("Не удалось подключиться к серверу")
                return
            }else model.ping()

            updateExchangesList(app.exchangeNamesList?.keys)
            updateCurrenciesList(app.exchangeNamesList?.get(app.currentExchangeName))

            /*currencyName.setSelection(
                curAdapter.getPosition(
                    cur?.symbol ?: DEFAULT_MAIN_CURRENCY_NAME
                )
            )*/
            //exchangeName.setSelection(exchAdapter.getPosition(exch?.name))
            currencyPrice.text = cur?.price?.toNumeric() ?: "0.0"




        }catch (e: Exception){e.printStackTrace()}
    }




}

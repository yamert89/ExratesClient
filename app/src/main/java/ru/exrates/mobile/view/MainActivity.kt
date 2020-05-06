package ru.exrates.mobile.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import lecho.lib.hellocharts.view.LineChartView
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.R
import ru.exrates.mobile.logic.rest.RestModel
import ru.exrates.mobile.logic.*
import ru.exrates.mobile.view.graph.GraphFactory
import ru.exrates.mobile.view.listeners.ExchangeSpinnerItemSelectedListener
import ru.exrates.mobile.view.listeners.SearchButtonClickListener
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangeNamesObject
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.view.viewAdapters.PairsAdapter
import java.io.FileNotFoundException
import java.io.InvalidClassException

class MainActivity : ExratesActivity() {
//fixme Skipped 39 frames!  The application may be doing too much work on its main thread.
    private lateinit var currencyName: Spinner
    private lateinit var currencyPrice: TextView
    private lateinit var exchangeName: Spinner
    private lateinit var goToCurBtn: ImageButton
    private lateinit var currenciesRecyclerView: RecyclerView
    private lateinit var anyChartView: LineChartView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var root: ConstraintLayout
    private lateinit var searchBtn: ImageView
    private lateinit var autoCompleteTextView: AutoCompleteTextView



    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        try {
            logTrace("main oncreate")
            setContentView(R.layout.activity_main)

            currencyName = findViewById(R.id.main_currency_spinner)
            currencyPrice = findViewById(R.id.main_cur_price)
            exchangeName = findViewById(R.id.main_exch_spinner)
            progressLayout = findViewById(R.id.progressLayout)
            anyChartView = findViewById(R.id.anyChartView)
            goToCurBtn = findViewById(R.id.go_to_currency)
            root = findViewById(R.id.root)
            searchBtn = findViewById(R.id.main_search_btn)
            autoCompleteTextView = findViewById(R.id.main_autoComplete)

            curAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
            exchAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)

            /*curAdapter.add("Currency")
            exchAdapter.add("Exchange")*/

            currencyName.adapter = curAdapter
            exchangeName.adapter = exchAdapter

            viewManager = LinearLayoutManager(this)

            val savedAdapter = storage.getValue(
                SAVED_CURRENCIES_ADAPTER,
                SAVED_CURRENCIES_ADAPTER_BINANCE
            )
            pairsAdapter = storage.getValue(savedAdapter,
                PairsAdapter(
                    mutableListOf(),
                    app = app
                )
            )
            pairsAdapter.app = app
            currenciesRecyclerView = findViewById<RecyclerView>(R.id.main_cur_list).apply {
                adapter = pairsAdapter
                layoutManager = viewManager
            }

            exchangeName.setSelection(0)

            currencyName.setSelection(0)

            currencyName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = startCurActivity(position)
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            goToCurBtn.setOnClickListener { startCurActivity() }

            searchBtn.setOnClickListener(
                SearchButtonClickListener(
                    autoCompleteTextView,
                    currencyName,
                    this
                )
            )

            autoCompleteTextView.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line ))

            exchangeName.onItemSelectedListener =
                ExchangeSpinnerItemSelectedListener(
                    this,
                    app
                )

            logD("Main activity created")

        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    private fun startCurActivity(position: Int = Int.MAX_VALUE){
        if (position != Int.MAX_VALUE) {
            if (curIdx == position) return
            curIdx = position
        }

        startActivity(Intent(applicationContext, CurrencyActivity::class.java).apply {
            val symbol = currencyName.getItemAtPosition(curIdx).toString()
            val curs = parseSymbol(symbol)
            app.currentCur1 = curs.first
            app.currentCur2 = curs.second

            putExtra(EXTRA_CURRENCY_NAME_1, curs.first )
            putExtra(EXTRA_CURRENCY_NAME_2, curs.second )
            // putExtra(EXTRA_EXCHANGE_NAME, exName)
            // putExtra(EXTRA_EXCHANGE_ID, app.exchangeNamesList!!.find { it.name == exName }!!.id)
            var id = app.baseContext.resources.getIdentifier(curs.first.toLowerCase(), "drawable", app.baseContext.packageName)
            if (id == 0) id = android.R.drawable.ic_menu_help
            putExtra(EXTRA_CUR_ICO, id)
            val defExId = if (app.exchangeNamesList!![0].pairs.contains(symbol)) 1 else {
                app.exchangeNamesList!!.find { it.pairs.contains(symbol) }!!.id
            }
            putExtra(EXTRA_EXCHANGE_ID, defExId)

            rebuildExSpinner(defExId)

            save(SAVED_EXID to defExId/*, SAVED_EX_IDX to pos*/)

        })
    }

    private fun rebuildExSpinner(exId : Int){
        logD("rebuildExSpinner")
        val adapter = (exchangeName.adapter as ArrayAdapter<String>)
        val pos = adapter.getPosition(app.exchangeNamesList?.find { it.id == exId }?.name)
        val ex = adapter.getItem(pos)
        adapter.remove(ex)
        adapter.insert(ex, 0)
        exchangeName.setSelection(0)
    }

    private fun parseSymbol(symbol: String): Pair<String, String>{
        val arr = symbol.split("/")
        return arr[0] to arr[1]
    }

    override fun updateExchangeData(exchange: Exchange){
        logD("updateExData")
        super.updateExchangeData(exchange)
        //if(app.currentExchange != null) exchange.pairs.addAll(app.currentExchange!!.pairs.intersect(exchange.pairs)) //todo
        app.currentExchange = exchange
        logD("incoming pairs: ${exchange.pairs.joinToString { it.symbol }}")
        val adapter = currenciesRecyclerView.adapter as PairsAdapter
        adapter.dataPairs.clear()
        adapter.dataPairs.addAll(exchange.pairs)
        adapter.notifyDataSetChanged()
    }

    override fun updatePairData(list: MutableList<CurrencyPair>) {
        logD("updatePairData")
        if (list.isEmpty()){
            logE("Incoming list of pairData is empty")
            return
        }
        logD(list.joinToString { "${it.symbol} | ${it.exchangeName}" })
        app.currentCur1 = list[0].baseCurrency
        app.currentCur2 = list[0].quoteCurrency
        super.updatePairData(list)
        app.currentPairInfo = list
        var count = 0.0
        list.forEach { count += it.price }
        currencyPrice.text = (count / list.size).toNumeric()
        val cur = list.find { it.exId == storage.getValue(SAVED_EXID, list[0].exId)}!! //todo right? app.currentExchange outdated. how to choose right pair?
        logTrace("current currency in graph: $cur")
        //val(xLabel, dataList) = createChartValueDataList(cur.priceHistory)
        logTrace("priceHistory:" + cur.priceHistory.joinToString())
        logTrace(
            "priceHistory truncated:" + cur.priceHistory.subList(
                cur.priceHistory.size - 10,
                cur.priceHistory.lastIndex + 1
            ).joinToString()
        )
        if (cur.priceHistory.isEmpty()) {
            root.removeView(anyChartView)
            logE("Graph removed")
            val notice = TextView(app.baseContext).apply {
                text = "Data not available"
            }
            root.addView(notice, 4)
        } else GraphFactory(anyChartView, "1h")
            .createSmallGraph(cur.priceHistory.subList(cur.priceHistory.size - 10, cur.priceHistory.lastIndex + 1))
    }

    override fun task() {
        logTrace("task")
        if (currentDataIsNull()){
            logTrace("current data  is null")
            return
        }
        logTrace("pairs: " + app.currentExchange!!.pairs.filter { it.visible }
            .map { it.symbol }.toTypedArray().joinToString())
        restModel.getActualExchange(ExchangePayload(
            app.currentExchange!!.exId,
            app.currentInterval,
            app.currentExchange!!.pairs.filter{it.visible}.map { it.baseCurrency + it.quoteCurrency }.toTypedArray().plus(arrayOf(app.currentCur1 + app.currentCur2))
        ))
        restModel.getActualPair(app.currentPairInfo!![0].baseCurrency , app.currentPairInfo!![0].quoteCurrency, "1h",
            CURRENCY_HISTORIES_MAIN_NUMBER
        )
    }

    private suspend fun firstLoadActivity(): Boolean{
        var res = false
        startProgress()
        coroutineScope {
            try {
                logTrace("before request")
                restModel.getLists()
            }catch (e: Exception){
                logE("exception")
                return@coroutineScope
            }
            res = true

        }
        if (res) storage.storeValue(IS_FIRST_LOAD, false)
        return res
    }

    fun initData(exchangeNamesList: List<ExchangeNamesObject>){
        logTrace("init data")
        try {
            app.exchangeNamesList = exchangeNamesList
            GlobalScope.launch {
                save(SAVED_EXCHANGE_NAME_LIST to exchangeNamesList)
                logTrace("list saved")

            }
            logTrace("get exchange")


            val allPairs = getListWithAllPairs(exchangeNamesList).sorted()
            val defExId = exchangeNamesList.find { it.pairs.contains(allPairs[0]) }!!.id
            restModel.getActualExchange(ExchangePayload(defExId, app.currentInterval, emptyArray()))
            val curs = parseSymbol(allPairs[0])
            restModel.getActualPair(
                curs.first, curs.second,
                CURRENCY_HISTORIES_MAIN_NUMBER
            )
            updateExchangesList(exchangeNamesList.map { it.name })

            updateCurrenciesList(allPairs)
            val adapter = autoCompleteTextView.adapter as ArrayAdapter<String>
            adapter.addAll(allPairs)
            rebuildExSpinner(defExId)

        }catch (e: Exception){
            logE("exception in init method")
            e.printStackTrace()
        }

    }




    override fun startProgress(){
        super.startProgress()
        logTrace("Snack started..")
        Snackbar.make(currenciesRecyclerView, "Первичная загрузка данных, подождите", Snackbar.LENGTH_LONG).show()
    }

    override fun saveState() {
        super.saveState()
        val adapterName = when(app.currentExchange?.exId){
            1 -> SAVED_CURRENCIES_ADAPTER_BINANCE
            else -> SAVED_CURRENCIES_ADAPTER_P2PB2B
        }
        save(
            SAVED_EX_IDX to exchangeName.selectedItemPosition,
            SAVED_CUR_IDX to currencyName.selectedItemPosition,
            SAVED_CURRENCIES_ADAPTER to adapterName,
            adapterName to currenciesRecyclerView.adapter!!,
            SAVED_CURRENCIES_NAMES to (app.currentExchange?.pairs?.map { it.symbol }?.toTypedArray() ?: arrayOf("ETCBTC"))/*, //todo hardcode
            SAVED_EXID to (app.currentExchange?.exId ?: 1)*/)
    }


    override fun onResume() {
        super.onResume()

    }




}

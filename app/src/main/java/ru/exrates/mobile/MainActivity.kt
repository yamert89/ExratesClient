package ru.exrates.mobile

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
import ru.exrates.mobile.graph.GraphFactory
import ru.exrates.mobile.logic.Model
import ru.exrates.mobile.logic.entities.BindedImageView
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangeNamesObject
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.viewadapters.PairsAdapter
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
    private lateinit var pairsAdapter: PairsAdapter
    private lateinit var curAdapter: ArrayAdapter<String>
    private lateinit var exchAdapter: ArrayAdapter<String>
    private lateinit var root: ConstraintLayout
    private var curIdx = 0
    private var exIdx = 0
    private var cur: CurrencyPair? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        try {
            log_trace("main oncreate")
            setContentView(R.layout.activity_main)
            //storage = Storage(applicationContext, app.om)

            model = Model(app, this)

            currencyName = findViewById(R.id.main_currency_spinner)
            currencyPrice = findViewById(R.id.main_cur_price)
            exchangeName = findViewById(R.id.main_exch_spinner)
            progressLayout = findViewById(R.id.progressLayout)
            anyChartView = findViewById(R.id.anyChartView)
            goToCurBtn = findViewById(R.id.go_to_currency)
            root = findViewById(R.id.root)

            curAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
            exchAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)

            curAdapter.add("Currency")
            exchAdapter.add("Exchange")

            currencyName.adapter = curAdapter
            exchangeName.adapter = exchAdapter

            viewManager = LinearLayoutManager(this)

            val savedAdapter = storage.getValue(SAVED_CURRENCIES_ADAPTER, SAVED_CURRENCIES_ADAPTER_BINANCE)
            pairsAdapter = storage.getValue(savedAdapter, PairsAdapter(mutableListOf(), app = app))
            pairsAdapter.app = app
            currenciesRecyclerView = findViewById<RecyclerView>(R.id.main_cur_list).apply {
                adapter = pairsAdapter
                layoutManager = viewManager
            }

            exchangeName.setSelection(0, true)

            exchangeName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    startActivity(Intent(applicationContext, ExchangeActivity::class.java).apply{
                        putExtra(EXTRA_EXCHANGE_ICO, app.currentExchange?.exId)
                    })
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val exchName = parent?.getItemAtPosition(position)
                    val exId = app.exchangeNamesList!!.find { it.name == exchName }!!.id
                    //log_d("item selected pos: $position, name: $exchName")

                    startActivity(Intent(applicationContext, ExchangeActivity::class.java).apply{
                        putExtra(EXTRA_EXCHANGE_ICO, getIcoId(exchName.toString()))
                        putExtra(EXTRA_EXCHANGE_ID, exId)
                    })
                }

                fun getIcoId(exName: String) = when(exName){
                    "binanceExchange" -> R.drawable.binance
                    "p2pb2b" -> R.drawable.p2pb2b
                    else -> throw IllegalArgumentException("ex $exName icon id  not found")
                }

            }



            currencyName.setSelection(0, false)

            currencyName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (curIdx == position) return
                    curIdx = position
                    val curName = parent?.getItemAtPosition(position)
                    log_trace("item selected pos: $position, name: $curName, id: $id")
                    val curs = curName.toString().split("/")
                    app.currentCur1 = curs[0]
                    app.currentCur2 = curs[1]
                    startActivity(Intent(applicationContext, CurrencyActivity::class.java).apply {
                        putExtra(EXTRA_CURRENCY_NAME_1, curs[0])
                        putExtra(EXTRA_CURRENCY_NAME_2, curs[1])
                        putExtra(EXTRA_CUR_ICO, app.baseContext.resources.getIdentifier(curs[0].toLowerCase(), "drawable", app.baseContext.packageName))
                        //putExtra(EXTRA_EXCHANGE_ICO, exchangeName.selectedItem as String)
                    })
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            goToCurBtn.setOnClickListener {
                startActivity(Intent(applicationContext, CurrencyActivity::class.java).apply {
                    val exName = exchangeName.selectedItem as String
                    val curs = currencyName.getItemAtPosition(curIdx).toString().split("/")
                    app.currentCur1 = curs[0]
                    app.currentCur2 = curs[1]
                    putExtra(EXTRA_CURRENCY_NAME_1, curs[0] )
                    putExtra(EXTRA_CURRENCY_NAME_2, curs[1] )
                   // putExtra(EXTRA_EXCHANGE_NAME, exName)
                    putExtra(EXTRA_EXCHANGE_ID, app.exchangeNamesList!!.find { it.name == exName }!!.id)
                    putExtra(EXTRA_CUR_ICO, app.baseContext.resources.getIdentifier(curs[0].toLowerCase(), "drawable", app.baseContext.packageName))

                })
            }



            //val cartesian = AnyChart.line()

            log_d("Main activity created")

        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    override fun updateExchangeData(exchange: Exchange){
        log_d("updateExData")
        super.updateExchangeData(exchange)
        //if(app.currentExchange != null) exchange.pairs.addAll(app.currentExchange!!.pairs.intersect(exchange.pairs)) //todo
        app.currentExchange = exchange
        log_d("incoming pairs: ${exchange.pairs.joinToString{it.symbol}}")
        val adapter = currenciesRecyclerView.adapter as PairsAdapter
        adapter.dataPairs.clear()
        adapter.dataPairs.addAll(exchange.pairs)
        adapter.notifyDataSetChanged()
    }

    override fun updatePairData(list: MutableList<CurrencyPair>) {
        log_d("updatePairData")
        log_d(list.joinToString{"${it.symbol} | ${it.exchangeName}"})
        app.currentCur1 = list[0].baseCurrency
        app.currentCur2 = list[0].quoteCurrency
        super.updatePairData(list)
        app.currentPairInfo = list
        var count = 0.0
        list.forEach { count += it.price }
        currencyPrice.text = (count / list.size).toNumeric()
        val cur = list.find { it.exId == app.currentExchange?.exId ?: 1 }!!
        log_d("current currency in graph: $cur")
        //val(xLabel, dataList) = createChartValueDataList(cur.priceHistory)
        log_d("priceHistory:" + cur.priceHistory.joinToString())
        log_d("priceHistory truncated:" + cur.priceHistory.subList(cur.priceHistory.size - 10, cur.priceHistory.lastIndex + 1).joinToString())
        if (cur.priceHistory.isEmpty()) {
            root.removeView(anyChartView)
            val notice = TextView(app.baseContext).apply {
                text = "Data not available"
            }
            root.addView(notice, 4)
        } else GraphFactory(anyChartView, "1h").createSmallGraph(cur.priceHistory.subList(cur.priceHistory.size - 10, cur.priceHistory.lastIndex + 1))
    }

    override fun task() {
        log_d("task")
        if (currentDataIsNull()){
            log_d("current data  is null")
            return
        }
        log_d( "pairs: " + app.currentExchange!!.pairs.filter{it.visible}.map { it.symbol }.toTypedArray().joinToString())
        model.getActualExchange(ExchangePayload(
            app.currentExchange!!.exId,
            app.currentInterval,
            app.currentExchange!!.pairs.filter{it.visible}.map { it.baseCurrency + it.quoteCurrency }.toTypedArray().plus(arrayOf(app.currentCur1 + app.currentCur2))
        ))
        model.getActualPair(app.currentPairInfo!![0].baseCurrency , app.currentPairInfo!![0].quoteCurrency, "1h", CURRENCY_HISTORIES_MAIN_NUMBER)
    }

    private suspend fun firstLoadActivity(): Boolean{
        var res = false
        startProgress()
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

    fun initData(exchangeNamesList: List<ExchangeNamesObject>){
        log_d("init data")
        try {
            app.exchangeNamesList = exchangeNamesList
            GlobalScope.launch {
                save(SAVED_EXCHANGE_NAME_LIST to exchangeNamesList)
                log_d("list saved")

            }
            log_d("get exchange")
            val defaultExchName = exchangeNamesList[0] //todo ??
            model.getActualExchange(ExchangePayload(1, app.currentInterval, emptyArray()))
            model.getActualPair(
                "BCC", "BTC", //todo hardcode
                CURRENCY_HISTORIES_MAIN_NUMBER
            ) //todo default exch and pair
            updateExchangesList(exchangeNamesList.map { it.name })
            updateCurrenciesList(exchangeNamesList[0].pairs)//todo
        }catch (e: Exception){
            log_e("exception in init method")
            e.printStackTrace()
        }

    }

    private fun updateExchangesList(exchangeNames: List<String>?){
        if (exchangeNames == null) return
        log_d( "exchanges: $exchangeNames")
        with(exchAdapter){clear(); addAll(exchangeNames); notifyDataSetChanged()}
        exchangeName.setSelection(storage.getValue(SAVED_EX_IDX, 0))
    }

    private fun updateCurrenciesList(curNames: List<String>?){
        if (curNames == null) return
        log_d( "curNames : $curNames")
        with(curAdapter){clear(); addAll(curNames); notifyDataSetChanged()}
        currencyName.setSelection(curIdx)
    }

    override fun startProgress(){
        super.startProgress()
        log_trace( "Snack started..")
        Snackbar.make(currenciesRecyclerView, "Первичная загрузка данных, подождите", Snackbar.LENGTH_LONG).show()
    }

    override fun saveState() {
        super.saveState()
        val adapterName = when(app.currentExchange?.exId){
            1 -> SAVED_CURRENCIES_ADAPTER_BINANCE
            else -> SAVED_CURRENCIES_ADAPTER_P2PB2B
        }
        save(SAVED_EX_IDX to exchangeName.selectedItemPosition,
            SAVED_CUR_IDX to currencyName.selectedItemPosition,
            SAVED_CURRENCIES_ADAPTER to adapterName,
            adapterName to currenciesRecyclerView.adapter!!,
            SAVED_CURRENCIES_NAMES to (app.currentExchange?.pairs?.map { it.symbol }?.toTypedArray() ?: arrayOf("ETCBTC"))) //todo hardcode
    }


    override fun onResume() {
        super.onResume()
        try {
            app = this.application as MyApp
            var flag = true
            log_d("main onresume")
            val listsReq = GlobalScope.launch(Dispatchers.IO) {
                log_d("start coroutine")
                if (storage.getValue(IS_FIRST_LOAD, true)) {
                    log_d("before first load")
                    flag = firstLoadActivity()
                    log_d("flaq is $flag")
                }

                else {
                    log_trace("Saved lists loaded")
                    try{
                        if (app.exchangeNamesList == null) app.exchangeNamesList = storage.loadObjectFromJson(SAVED_EXCHANGE_NAME_LIST, ArrayList<ExchangeNamesObject>())
                        log_d("ds")

                    }catch (e: FileNotFoundException){
                        flag = false
                        stopProgress()
                        storage.storeValue(IS_FIRST_LOAD, true)
                        return@launch
                    }catch (e: InvalidClassException){
                        log_e("Class model of ex names list deprecated")
                        flag = firstLoadActivity()
                    }catch (e: Exception){
                        log_e(e.message.toString())
                    }
                    curIdx = storage.getValue(SAVED_CUR_IDX, 0)
                    exIdx = storage.getValue(SAVED_EX_IDX, 0)
                    val cur1 = storage.getValue(CURRENT_CUR_1, "AGIBTC")
                    val cur2 = storage.getValue(CURRENT_CUR_2, "AGIBTC")
                    val exId = storage.getValue(CURRENT_EXCHANGE_ID, 1)
                    val pairs = storage.getValue(SAVED_CURRENCIES_NAMES, arrayOf("AGIBTC")) //todo hardcode
                    app.currentCur1 = cur1
                    app.currentCur2 = cur2


                    model.getActualExchange(ExchangePayload(
                        exId,
                        app.currentInterval,
                        app.currentExchange?.pairs?.map { it.baseCurrency + it.quoteCurrency }?.toTypedArray()?.plus(
                            arrayOf(app.currentCur1 + app.currentCur2)) ?: pairs)
                    )
                    model.getActualPair(cur1, cur2, "1h", CURRENCY_HISTORIES_MAIN_NUMBER)


                }

            }

            runBlocking { listsReq.join() }
            if (!flag) {
                toast("Не удалось подключиться к серверу. Проверте интернет подключение и перезапустите приложение")

                return
            }else model.ping()

            updateExchangesList(app.exchangeNamesList?.map { it.name })
            updateCurrenciesList(app.exchangeNamesList?.find { it.id == app.currentExchange?.exId }?.pairs)
            currencyPrice.text = cur?.price?.toNumeric() ?: "0.0"

        }catch (e: Exception){e.printStackTrace()}
    }




}

package ru.exrates.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.serialization.internal.MapEntry
import ru.exrates.mobile.logic.Model
import ru.exrates.mobile.logic.Storage
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.viewadapters.PairsAdapter
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

class MainActivity : ExratesActivity() {

    private lateinit var currencyName: Spinner
    private lateinit var currencyPrice: TextView
    private lateinit var exchangeName: Spinner
    private lateinit var currenciesRecyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var pairsAdapter: PairsAdapter
    private lateinit var curAdapter: ArrayAdapter<String>
    private lateinit var exchAdapter: ArrayAdapter<String>


    private var currenciesList: List<String>? = null
    private var exchangesList: List<String>? = null
    private var curIdx = 0
    private var exIdx = 0
    private var exch: Exchange? = null
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

            curAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
            exchAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)

            currencyName.adapter = curAdapter
            exchangeName.adapter = exchAdapter

            viewManager = LinearLayoutManager(this)

            pairsAdapter = PairsAdapter(mutableListOf())
            currenciesRecyclerView = findViewById<RecyclerView>(R.id.main_cur_list).apply {
                adapter = pairsAdapter
                layoutManager = viewManager
            }




            //snakBar()



            log_d("Main activity created")

        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    override fun updateExchangeData(exchange: Exchange){
        app.currentExchange = exchange
        val adapter = currenciesRecyclerView.adapter as PairsAdapter
        adapter.dataPairs.clear()
        adapter.dataPairs.addAll(exchange.pairs)
        adapter.notifyDataSetChanged()
    }

    override fun updatePairData(list: List<CurrencyPair>) {
        app.currentPairInfo = list
        var count = 0.0
        list.forEach { count += it.price }
        currencyPrice.text = (count / list.size).toNumeric()
    }

    override suspend fun firstLoadActivity(): Boolean{
        var res = true
        coroutineScope {
            var lists: Map<String, List<String>> = mapOf()
            try {
                log_d("before request")
                lists = app.restService.lists().execute().body()!! //todo replace with async
                log_d(lists.size.toString())
            }catch (e: Exception){
                log_e("exception")
                res = false
                return@coroutineScope
            }
            currenciesList = lists["currencies"]
            exchangesList = lists["exchanges"]

            launch {
                save(
                    MapEntry(SAVED_CURRENCY_NAME_LIST, currenciesList!!),
                    MapEntry(SAVED_EXCHANGE_NAME_LIST, exchangesList!!)
                )
                log_d("list saved")
                //storage.saveObject(currenciesList, SAVED_CURRENCY_NAME_LIST)
                //storage.saveObject(exchangesList, SAVED_EXCHANGE_NAME_LIST)
            }
            log_d("get exchange")
            exch = app.restService.getExchange(ExchangePayload(exchangesList?.get(0) ?: "binanceExchange", "1h", emptyArray())).execute().body() //todo null check refactor
            app.currentPairInfo = app.restService.getPair("VENBTC").execute().body() //todo pairName
            cur = exch!!.pairs.get(0) //todo check null refactoring
            app.currentExchange = exch!!
            launch { save(MapEntry(SAVED_EXCHANGE, exch!!)) }
        }
        if (res) storage.storeValue(IS_FIRST_LOAD, false)
        return res

        //cur = app.restService.getPair().execute().body()


    }

    private fun updateExchangesList(exchNames: List<String>){
        log_d( "exchNames: $exchNames")
        with(exchAdapter){clear(); addAll(exchNames); notifyDataSetChanged()}
    }

    private fun updateCurrenciesList(curNames: List<String>){
        log_d( "curNames : $curNames")
        with(curAdapter){clear(); addAll(curNames); notifyDataSetChanged()}
    }

    fun snakBar(){
        log_d( "Snack started..")
        Snackbar.make(currenciesRecyclerView, "Загрузка данных, подождите", Snackbar.LENGTH_LONG).show()
        //todo progressbar
    }

    override fun onResume() {
        super.onResume()
        try {
            app = this.application as MyApp
            //snakBar()
            var flag = true
            log_d("before")
            val listsReq = GlobalScope.launch(Dispatchers.IO) {
                log_d("start coroutine")
                if (storage.getValue(IS_FIRST_LOAD, true)) {
                    log_d("before first load")
                    flag = firstLoadActivity()
                    log_d("flaq is $flag")
                }

                else {
                    log_d("Saved lists loaded")
                    exch = app.currentExchange ?: storage.loadObject(SAVED_EXCHANGE)
                    cur = storage.loadObject(CURRENT_PAIR, CurrencyPair.createEmptyInstance()) //NULL mb currentPairInfo null
                    currenciesList = storage.loadObject(SAVED_CURRENCY_NAME_LIST)
                    exchangesList = storage.loadObject(SAVED_EXCHANGE_NAME_LIST)
                    curIdx = storage.getValue(SAVED_CUR_IDX, 0)
                    exIdx = storage.getValue(SAVED_EX_IDX, 0)
                    app.currentExchange = exch
                    app.currentPairInfo = storage.loadObject(CURRENT_PAIR_INFO)
                }

            }

            runBlocking { listsReq.join() }
            if (!flag) {
                Toast.makeText(
                    applicationContext,
                    "Не удалось подклюситься к серверу",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            updateCurrenciesList(currenciesList!!)
            updateExchangesList(exchangesList!!)
            currencyName.setSelection(
                curAdapter.getPosition(
                    cur?.symbol ?: DEFAULT_MAIN_CURRENCY_NAME
                )
            )
            //exchangeName.setSelection(exchAdapter.getPosition(exch?.name))
            currencyPrice.text = cur?.price?.toNumeric() ?: "0.0"

            exchangeName.setSelection(0, true)

            exchangeName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    log_d("items was not be selected")
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val curName = parent?.getItemAtPosition(position)
                    log_d("item selected pos: $position, name: $curName")
                    startActivity(Intent(applicationContext, ExchangeActivity::class.java).apply{
                        putExtra(EXTRA_EXCHANGE_NAME, curName.toString())
                    })
                }

            }
        }catch (e: Exception){e.printStackTrace()}
    }




}

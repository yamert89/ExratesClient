package ru.exrates.mobile

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import ru.exrates.mobile.viewadapters.MainPairsAdapter
import java.util.*

class MainActivity : ExratesActivity() {

    private lateinit var currencyName: Spinner
    private lateinit var currencyPrice: TextView
    private lateinit var exchangeName: Spinner
    private lateinit var currenciesRecyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var pairsAdapter: MainPairsAdapter
    private lateinit var curAdapter: ArrayAdapter<String>
    private lateinit var exchAdapter: ArrayAdapter<String>
    private lateinit var model: Model

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

            app = this.application as MyApp
            model = Model(app, this)

            currencyName = findViewById(R.id.main_currency_spinner)
            currencyPrice = findViewById(R.id.main_cur_price)
            exchangeName = findViewById(R.id.main_exch_spinner)

            curAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
            exchAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)

            currencyName.adapter = curAdapter
            exchangeName.adapter = exchAdapter

            viewManager = LinearLayoutManager(this)

            pairsAdapter = MainPairsAdapter(mutableListOf())
            currenciesRecyclerView = findViewById<RecyclerView>(R.id.main_cur_list).apply {
                adapter = pairsAdapter
                layoutManager = viewManager
            }
            snakBar()

            timer.schedule(object :  TimerTask(){
                override fun run() {
                    model.getActualExchange(ExchangePayload("binanceExchange", "1h", arrayOf("VENBTC")))
                }
            }, 15000L, 180000L)

        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    override fun updateExchangeData(exchange: Exchange){
        app.currentExchange = exchange
        val adapter = currenciesRecyclerView.adapter as MainPairsAdapter
        adapter.dataPairs.clear()
        adapter.dataPairs.addAll(exchange.pairs)
        adapter.notifyDataSetChanged()
    }

    override fun updatePairData(map: Map<String, CurrencyPair>) {
        app.currentPairInfo = map
        var count = 0.0
        map.forEach { count += it.value.price }
        currencyPrice.text = (count / map.size).toNumeric()
    }

    override suspend fun firstLoadActivity(){
        coroutineScope {
            storage.storeValue(IS_FIRST_LOAD, false)
            val lists = app.restService.lists().execute().body()!!
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
            cur = exch!!.pairs.get(0) //todo check null refactoring
            app.currentExchange = exch!!
            launch { save(MapEntry(SAVED_EXCHANGE, exch!!)) }
        }

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
        snakBar()

        val listsReq = GlobalScope.launch(Dispatchers.IO) {
            if(storage.getValue(IS_FIRST_LOAD, true)) firstLoadActivity()
            else{
                log_d("Saved lists loaded")
                exch = app.currentExchange ?: storage.loadObject(SAVED_EXCHANGE)
                cur = storage.loadObject<CurrencyPair>(CURRENT_PAIR) //NULL mb currentPairInfo null
                currenciesList = storage.loadObject(SAVED_CURRENCY_NAME_LIST)
                exchangesList = storage.loadObject(SAVED_EXCHANGE_NAME_LIST)
                curIdx = storage.getValue(SAVED_CUR_IDX, 0)
                exIdx = storage.getValue(SAVED_EX_IDX, 0)
                app.currentExchange = exch
            }

        }

        runBlocking { listsReq.join() }
        updateCurrenciesList(currenciesList ?: throw NullPointerException("cur list is null"))
        updateExchangesList(exchangesList ?: throw NullPointerException("exch list is null"))
        currencyName.setSelection(
            curAdapter.getPosition(
                cur?.symbol ?: DEFAULT_MAIN_CURRENCY_NAME
            )
        )
        exchangeName.setSelection(exchAdapter.getPosition(exch?.name))
        currencyPrice.text = cur?.price?.toNumeric() ?: "0.0"
    }




}

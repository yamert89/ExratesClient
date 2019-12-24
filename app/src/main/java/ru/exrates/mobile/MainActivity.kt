package ru.exrates.mobile

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.internal.MapEntry
import ru.exrates.mobile.logic.Model
import ru.exrates.mobile.logic.Storage
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.viewadapters.MainPairsAdapter
import java.util.*

class MainActivity : AppCompatActivity(), ExratesActivity {

    private lateinit var currencyName: Spinner
    private lateinit var currencyPrice: TextView
    private lateinit var exchangeName: Spinner
    private lateinit var currenciesRecyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var pairsAdapter: MainPairsAdapter
    private lateinit var curAdapter: ArrayAdapter<String>
    private lateinit var exchAdapter: ArrayAdapter<String>
    private lateinit var app: MyApp
    private lateinit var model: Model

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
            loadActivity()
            Timer().schedule(object :  TimerTask(){
                override fun run() {
                    model.getActualExchange(ExchangePayload("binanceExchange", "1h", arrayOf("VENBTC")))
                }
            }, 20000L, 20000L)

        }catch (e: Exception){
            e.printStackTrace()
        }

    }




    override fun updateExchangeData(exchange: Exchange){
        val adapter = currenciesRecyclerView.adapter as MainPairsAdapter
        adapter.dataPairs.clear()
        adapter.dataPairs.addAll(exchange.pairs)
        adapter.notifyDataSetChanged()
    }

    override fun updatePairData(map: Map<String, CurrencyPair>) {
        var count = 0.0
        map.forEach { count += it.value.price }
        currencyPrice.text = (count / map.size).toNumeric()
    }

    override fun loadActivity(){
        val storage = Storage(applicationContext)
        var currenciesList: List<String>? = null
        var exchangesList: List<String>? = null
        var curIdx = 0
        var exIdx = 0
        var exch: Exchange? = null
        var cur: CurrencyPair? = null

        val listsReq = GlobalScope.launch(Dispatchers.IO) {
            if(storage.getValue(IS_FIRST_LOAD, true)){
                storage.storeValue(IS_FIRST_LOAD, false)
                val lists = app.restService.lists().execute().body()!!
                currenciesList = lists["currencies"]
                exchangesList = lists["exchanges"]

                exch = app.restService.getExchange(ExchangePayload(exchangesList?.get(0) ?: "binanceExchange", "1h", emptyArray())).execute().body() //todo null check refactor
                cur = exch!!.pairs.get(0) //todo check null refactoring
                //cur = app.restService.getPair().execute().body()


                launch {
                    saveState(MapEntry(SAVED_CURRENCY_NAME_LIST, currenciesList!!), MapEntry(
                        SAVED_EXCHANGE_NAME_LIST, exchangesList!!)
                    )
                    //storage.saveObject(currenciesList, SAVED_CURRENCY_NAME_LIST)
                    //storage.saveObject(exchangesList, SAVED_EXCHANGE_NAME_LIST)
                }

            } else{
                log_d("Saved lists loaded")
                exch = storage.loadObject(SAVED_EXCHANGE)
                cur = storage.loadObject<CurrencyPair>(SAVED_CURRENCY)
                currenciesList = storage.loadObject(SAVED_CURRENCY_NAME_LIST)
                exchangesList = storage.loadObject(SAVED_EXCHANGE_NAME_LIST)
                curIdx = storage.getValue(SAVED_CUR_IDX, 0)
                exIdx = storage.getValue(SAVED_EX_IDX, 0)
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

    private fun updateExchangesList(exchNames: List<String>){
        log_d( "exchNames: $exchNames")
        exchAdapter.clear()
        exchAdapter.addAll(exchNames)
        exchAdapter.notifyDataSetChanged()
    }

    private fun updateCurrenciesList(curNames: List<String>){
        log_d( "curNames : $curNames")
        curAdapter.clear()
        curAdapter.addAll(curNames)
        curAdapter.notifyDataSetChanged()
    }

    fun snakBar(){
        log_d( "Snack started..")
        Snackbar.make(currenciesRecyclerView, "Загрузка данных, подождите", Snackbar.LENGTH_LONG).show()
        //todo progressbar
    }

    override fun saveState(vararg args : MapEntry<String, Any>) {
        args.forEach { Storage(applicationContext).storeValue(it.key, it.value) }
        log_d("savestate: ${args.size} objects saved")
    }

    override fun onResume() {
        super.onResume()
        snakBar()
    }


}

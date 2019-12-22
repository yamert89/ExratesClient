package ru.exrates.mobile

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.exrates.mobile.logic.Storage
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.logic.rest.OneExchangeCallback
import ru.exrates.mobile.viewmodel.MainPairsAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var currencyName: Spinner
    private lateinit var currencyPrice: TextView
    private lateinit var exchangeName: Spinner
    private lateinit var currenciesRecyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var pairsAdapter: MainPairsAdapter
    private lateinit var curAdapter: ArrayAdapter<String>
    private lateinit var exchAdapter: ArrayAdapter<String>
    private lateinit var app: MyApp

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)

            app = this.application as MyApp

            //check first load or not
            currencyName = findViewById(R.id.main_currency_spinner)
            currencyPrice = findViewById(R.id.main_cur_price)
            exchangeName = findViewById(R.id.main_exch_spinner)

            curAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
            exchAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)

            //curAdapter.addAll(app.dataProvider.getMainSavedCurrencyNameList(applicationContext))
            //exchAdapter.addAll(app.dataProvider.getMainSavedExchangesNameList(applicationContext))

            currencyName.adapter = curAdapter
            exchangeName.adapter = exchAdapter

            viewManager = LinearLayoutManager(this)

            pairsAdapter = MainPairsAdapter(mutableListOf())
            currenciesRecyclerView = findViewById<RecyclerView>(R.id.main_cur_list).apply {
                adapter = pairsAdapter
                layoutManager = viewManager
            }
            snakBar()
            updateActivity()






        }catch (e: Exception){
            e.printStackTrace()
        }



    }

    fun test(){
        GlobalScope.launch(Dispatchers.Main){
            delay(4000)
            currencyPrice.text = "Yes"

        }


    }

    override fun onResume() {
        super.onResume()
        snakBar()
    }

    fun updateExchangeData(exchange: Exchange){

    }

    fun updateExchangesList(exchNames: List<String>){
        exchAdapter.clear()
        exchAdapter.addAll(exchNames)
        exchAdapter.notifyDataSetChanged()
    }

    fun updateCurrenciesList(curNames: List<String>){
        curAdapter.clear()
        curAdapter.addAll(curNames)
        curAdapter.notifyDataSetChanged()
    }

    fun updateActivity(){
        //curAdapter.addAll(app.dataProvider.getMainSavedCurrencyNameList(applicationContext))
        //exchAdapter.addAll(app.dataProvider.getMainSavedExchangesNameList(applicationContext))
        //pairsAdapter = MainPairsAdapter(app.dataProvider.getMainSavedListCurrencies(applicationContext))
        GlobalScope.launch(Dispatchers.Main) {
            val storage = Storage(applicationContext)
            if(storage.getValue(IS_FIRST_LOAD, true)){
                storage.storeValue(IS_FIRST_LOAD, false)
                val lists = app.restService.lists().execute().body()!!
                val currenciesList = lists["currencies"]
                val exchangesList = lists["exchanges"]
                updateCurrenciesList(currenciesList ?: throw NullPointerException("cur list is null"))
                updateExchangesList(exchangesList ?: throw NullPointerException("exch list is null"))


            }

        }







        val oldExch = app.dataProvider.getSavedExchange(this)
        val oldCur = Storage(applicationContext).loadObject<CurrencyPair>(SAVED_CURRENCY)


        currencyName.setSelection(
            curAdapter.getPosition(
                oldCur?.symbol ?: DEFAULT_MAIN_CURRENCY_NAME
            )
        )
        currencyPrice.text = oldCur?.price.toString()
        exchangeName.setSelection(exchAdapter.getPosition(oldExch.name))



        val exchangePayload =
            ExchangePayload(
                oldExch.name,
                "1h",
                arrayOf("BTCLTC")
            ) //todo timeout

        app.restService.getExchange(exchangePayload).enqueue(OneExchangeCallback(this))
    }

    fun snakBar(){
        Log.d("Exrates", "Snack started..")
        Snackbar.make(currenciesRecyclerView, "Загрузка данных, подождите", Snackbar.LENGTH_LONG).show()
        //todo progressbar
    }


}

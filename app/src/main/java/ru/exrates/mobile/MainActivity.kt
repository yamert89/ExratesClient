package ru.exrates.mobile

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            app = this.application as MyApp

            curAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
            exchAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
            val oldExch = app.dataProvider.getSavedExchange(this)
            val oldCur = Storage(applicationContext).loadObject<CurrencyPair>(SAVED_CURRENCY)

            curAdapter.addAll(app.dataProvider.getMainSavedCurrencyNameList(applicationContext))
            exchAdapter.addAll(app.dataProvider.getMainSavedExchangesNameList(applicationContext))

            currencyName = findViewById(R.id.main_currency_spinner)
            currencyPrice = findViewById(R.id.main_cur_price)
            exchangeName = findViewById(R.id.main_exch_spinner)

            currencyName.adapter = curAdapter
            currencyName.setSelection(
                curAdapter.getPosition(
                    oldCur?.symbol ?: DEFAULT_MAIN_CURRENCY_NAME
                )
            )
            currencyPrice.text = oldCur?.price.toString()
            exchangeName.setSelection(exchAdapter.getPosition(oldExch.name))

            viewManager = LinearLayoutManager(this)


            pairsAdapter =
                MainPairsAdapter(app.dataProvider.getMainSavedListCurrencies(applicationContext))
            currenciesRecyclerView = findViewById<RecyclerView>(R.id.main_cur_list).apply {
                adapter = pairsAdapter
                layoutManager = viewManager
            }

            val exchangePayload =
                ExchangePayload(
                    oldExch.name,
                    "1h",
                    arrayOf("BTCLTC")
                ) //todo timeout

            app.restService.getExchange(exchangePayload).enqueue(OneExchangeCallback(this))
        }catch (e: Exception){
            e.printStackTrace()
        }



    }

    fun updateExchangeData(exchange: Exchange){
        curAdapter.clear()
        curAdapter.addAll(exchange.pairs.map{it.symbol}.toList())
        curAdapter.notifyDataSetChanged()
    }

    fun updateExchangesList(exchNames: String){

    }


}

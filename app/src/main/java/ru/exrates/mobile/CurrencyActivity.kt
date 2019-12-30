package ru.exrates.mobile

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.exrates.mobile.logic.Model
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.viewadapters.ExchangesAdapter
import java.lang.NullPointerException
import java.util.concurrent.ArrayBlockingQueue

class CurrencyActivity : ExratesActivity() {
    private lateinit var currencyName: TextView
    private lateinit var currencyInterval: Button
    private lateinit var currencyIntervalValue: TextView
    private var currentInterval = "1h"
    private lateinit var currencyExchange: TextView
    private lateinit var currencyExchanges: RecyclerView
    private lateinit var exchangesAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try{
            setContentView(R.layout.currency)
            app = this.application as MyApp
            currencyExchange = findViewById(R.id.cur_exchange)
            currencyName = findViewById(R.id.cur_name)
            currencyInterval = findViewById(R.id.cur_interval)
            currencyIntervalValue = findViewById(R.id.cur_intervalValue)

            model = Model(app, this)

            if(currentDataIsNull()){
                app.currentExchange = storage.loadObject(CURRENT_EXCHANGE)
                app.currentPairInfo = storage.loadObject(CURRENT_PAIR_INFO)
                currentInterval = storage.getValue(CURRENT_INTERVAL, "1h")
                log_d("Loaded saved pair data from storage")
            }

            if (currentDataIsNull()) throw NullPointerException("current data is null")


            val currName = intent.getStringExtra(EXTRA_CURRENCY_NAME)
            //val exchanges = app.dataProvider.exchanges.values.toList()


            currencyName.text = currName

            exchangesAdapter = ExchangesAdapter(app.currentPairInfo!!, currName!!, currentInterval)  //todo update pair data rest req
            viewManager = LinearLayoutManager(this)

            currencyExchanges = findViewById<RecyclerView>(R.id.cur_exchanges).apply{
                adapter = exchangesAdapter
                layoutManager = viewManager

            }
        }catch (e: Exception){
            e.printStackTrace()
        }




    }

    override fun updatePairData(list: List<CurrencyPair>) {
        super.updatePairData(list)
        val adapter = currencyExchanges.adapter as ExchangesAdapter
        adapter.interval = currencyIntervalValue.text.toString()
        adapter.notifyDataSetChanged()
    }

    override fun task() {
        model.getActualPair(currencyName.text.toString())
    }




}
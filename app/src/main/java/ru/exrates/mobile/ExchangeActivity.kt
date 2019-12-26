package ru.exrates.mobile

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.internal.MapEntry
import ru.exrates.mobile.logic.Model
import ru.exrates.mobile.logic.Storage
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.viewadapters.PairsAdapter
import java.util.concurrent.ArrayBlockingQueue

class ExchangeActivity : ExratesActivity() {
    private lateinit var exchName: TextView
    private lateinit var intervalBtn: Button
    private lateinit var hideBtn: Button
    private lateinit var intervalValue: TextView
    private lateinit var pairs: RecyclerView
    private lateinit var pairsAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var currentInterval = "1h"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.exchange)
            storage = Storage(applicationContext)


            exchName = findViewById(R.id.exName)
            intervalBtn = findViewById(R.id.cur_interval)
            hideBtn = findViewById(R.id.exch_btn_hide_show)
            intervalValue = findViewById(R.id.intervalValue)

            model = Model(app, this)


            //val queue = ArrayBlockingQueue<Double>(20)

            //val exchName: String? = savedInstanceState!!.getString(EXCH_NAME)

            //currentExchange =

            //val currentInterval = storage.getValue(CURRENT_INTERVAL, "1h")
            if (currentDataIsNull()){
                app.currentExchange = storage.loadObject(CURRENT_EXCHANGE)
                app.currentPairInfo = storage.loadObject(CURRENT_PAIR_INFO)
                currentInterval = storage.getValue(CURRENT_INTERVAL, "1h")
            }
            if (currentDataIsNull()) throw NullPointerException("current data is null")


            val pairsOfAdapter = if (app.currentExchange!!.showHidden) app.currentExchange!!.pairs else app.currentExchange!!.pairs.filter{it.visible}.toMutableList()
            pairsAdapter = PairsAdapter(pairsOfAdapter, currentInterval)
            viewManager = LinearLayoutManager(this)

            pairs = findViewById<RecyclerView>(R.id.pairs).apply {
                layoutManager = viewManager
                adapter = pairsAdapter
            }

            intervalBtn.setOnClickListener {
                val adapter = pairs.adapter as PairsAdapter
                adapter.currentInterval = intervalValue.text.toString()
                adapter.notifyDataSetChanged()
            }

            val exName = savedInstanceState?.getString(EXTRA_CURRENCY_NAME) ?: throw NullPointerException("extra cur name is null")
            exchName.text = exName
            val interval = storage.getValue(CURRENT_INTERVAL, "1h")
            model.getActualExchange(ExchangePayload(exName, interval, arrayOf()))

        }catch (e: Exception){
            e.printStackTrace()

        }


    }

    override fun updateExchangeData(exchange: Exchange) {
        val adapter = pairs.adapter as PairsAdapter
        with(adapter.dataPairs){clear(); addAll(exchange.pairs)}
        adapter.notifyDataSetChanged()
    }

    override fun updatePairData(map: Map<String, CurrencyPair>) {

    }





}
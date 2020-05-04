package ru.exrates.mobile

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.exrates.mobile.logic.Model
import ru.exrates.mobile.logic.Storage
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.viewadapters.PairsAdapter

class ExchangeActivity : ExratesActivity() {
    private lateinit var exIco: ImageView
    private lateinit var intervalBtn: Button
    private lateinit var intervalValue: TextView
    private lateinit var pairs: RecyclerView
    private lateinit var pairsAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var currentInterval = "1h"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.exchange)
            //storage = Storage(applicationContext)


            exIco = findViewById(R.id.exIco)
            intervalBtn = findViewById(R.id.cur_interval)
            intervalValue = findViewById(R.id.intervalValue)
            progressLayout = findViewById(R.id.progressLayout)

            model = Model(app, this)

            if (currentNameListsIsNull()){
                app.exchangeNamesList = storage.loadObjectFromJson(SAVED_EXCHANGE_NAME_LIST) //todo ? delete
                currentInterval = storage.getValue(CURRENT_INTERVAL, "1h")
            }
            if (currentNameListsIsNull()) throw NullPointerException("current data is null")

            val pairsOfAdapter = if(currentDataIsNull()) mutableListOf<CurrencyPair>() else
                if (app.currentExchange!!.showHidden) app.currentExchange!!.pairs else app.currentExchange!!.pairs.filter{it.visible}.toMutableList() //todo base filtering on server
            pairsAdapter = PairsAdapter(pairsOfAdapter, currentInterval, app)
            viewManager = LinearLayoutManager(this)

            pairs = findViewById<RecyclerView>(R.id.pairs).apply {
                layoutManager = viewManager
                adapter = pairsAdapter
            }

            intervalBtn.setOnClickListener {
                intervalValue.text = app.currentPairInfo!![0].priceChange
                    .higherKey(intervalValue.text.toString()) ?: app.currentPairInfo!![0].priceChange.firstKey()
                val adapter = pairs.adapter as PairsAdapter
                adapter.currentInterval = intervalValue.text.toString()
                adapter.notifyDataSetChanged()
            }

            val icoId = intent.getIntExtra(EXTRA_EXCHANGE_ICO, 0)
            exIco.setImageDrawable(ResourcesCompat.getDrawable(app.resources, icoId, null ))
            //val exId = intent.getIntExtra(EXTRA_EXCHANGE_ID, 1)
            model.getActualExchange(ExchangePayload(app.currentExchange!!.exId, currentInterval, arrayOf()))
            startProgress()

        }catch (e: Exception){
            e.printStackTrace()

        }


    }

    override fun updateExchangeData(exchange: Exchange) {
        super.updateExchangeData(exchange)
        app.currentExchange = exchange
        val adapter = pairs.adapter as PairsAdapter
        with(adapter.dataPairs){clear(); addAll(exchange.pairs)}
        adapter.notifyDataSetChanged()
    }

    override fun task() {
        if (currentDataIsNull()) throw NullPointerException("current data in task is null")
        model.getActualExchange(
            ExchangePayload(
                app.currentExchange!!.exId,
                currentInterval,
                app.currentExchange!!.pairs.filter{it.visible}.map { it.baseCurrency + it.quoteCurrency }.toTypedArray()
            )
        )
    }






}
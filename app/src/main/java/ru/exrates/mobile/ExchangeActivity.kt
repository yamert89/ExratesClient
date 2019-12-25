package ru.exrates.mobile

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.internal.MapEntry
import ru.exrates.mobile.logic.Storage
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.viewadapters.PairsAdapter
import java.util.concurrent.ArrayBlockingQueue

class ExchangeActivity : ExratesActivity() {
    private lateinit var exchName: TextView
    private lateinit var intervalBtn: Button
    private lateinit var intervalValue: TextView
    private lateinit var pairs: RecyclerView
    private lateinit var pairsAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var currentExchange: Exchange

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.exchange)

            exchName = findViewById(R.id.exName)
            intervalBtn = findViewById(R.id.cur_interval)
            intervalValue = findViewById(R.id.intervalValue)


            //val queue = ArrayBlockingQueue<Double>(20)

            //val exchName: String? = savedInstanceState!!.getString(EXCH_NAME)

            currentExchange = storage.loadObject(SAVED_EXCHANGE)

            val currentInterval = storage.getValue(CURRENT_INTERVAL, "1h")

            val pairsOfAdapter = if (currentExchange.showHidden) currentExchange.pairs else currentExchange.pairs.filter{it.visible}.toMutableList()
            pairsAdapter = PairsAdapter(pairsOfAdapter, currentInterval)
            viewManager = LinearLayoutManager(this)

            pairs = findViewById<RecyclerView>(R.id.pairs).apply {
                layoutManager = viewManager
                adapter = pairsAdapter
            }

            intervalBtn.setOnClickListener {
                (pairsAdapter as? PairsAdapter)?.currentInterval = intervalValue.text.toString()
                currentExchange.pairs.add(CurrencyPair("Temp", 34.454, mapOf("1d" to 53.64), arrayOf(0L), ArrayBlockingQueue(2)))
                pairsAdapter.notifyDataSetChanged()
            }
        }catch (e: Exception){
            e.printStackTrace()

        }


    }

    override fun updateExchangeData(exchange: Exchange) {

    }

    override fun updatePairData(map: Map<String, CurrencyPair>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun firstLoadActivity() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}
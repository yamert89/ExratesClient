package ru.exrates.mobile

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.exrates.mobile.logic.Storage
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.viewmodel.PairsAdapter
import java.util.concurrent.ArrayBlockingQueue

class ExchangeActivity : AppCompatActivity() {
    private lateinit var exchName: TextView
    private lateinit var intervalBtn: Button
    private lateinit var intervalValue: TextView
    private lateinit var pairs: RecyclerView
    private lateinit var pairsAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var app: MyApp
    private lateinit var currentExchange: Exchange

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.exchange)
            app = this.application as MyApp
            exchName = findViewById(R.id.exName)
            intervalBtn = findViewById(R.id.cur_interval)
            intervalValue = findViewById(R.id.intervalValue)

            //val queue = ArrayBlockingQueue<Double>(20)

            //val exchName: String? = savedInstanceState!!.getString(EXCH_NAME)



            currentExchange = app.dataProvider.getSavedExchange(this.applicationContext)

            val currentInterval = Storage(applicationContext).getStringValue(CURRENT_INTERVAL, "1h", "1h")

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

    fun getTestExchange(): Exchange {
        val queue = ArrayBlockingQueue<Double>(20)
        val arr = arrayOfNulls<Long?>(1)
        return Exchange(
            "testExchange",
            mutableListOf(
                CurrencyPair(
                    "btc_ltc",
                    0.345,
                    mapOf("1m" to 4453.023, "1w" to 5433.3230),
                    arr,
                    queue
                ),
                CurrencyPair(
                    "etc_ltc",
                    0.543,
                    mapOf("1m" to 324.0323, "1w" to 6673.32340),
                    arr,
                    queue
                ),
                CurrencyPair(
                    "etc_ltc",
                    0.543,
                    mapOf("1m" to 324.0323, "1w" to 6673.32340),
                    arr,
                    queue
                ),
                CurrencyPair(
                    "etc_ltc",
                    0.543,
                    mapOf("1m" to 324.0323, "1w" to 6673.32340),
                    arr,
                    queue
                ),
                CurrencyPair(
                    "etc_ltc",
                    0.543,
                    mapOf("1m" to 324.0323, "1w" to 6673.32340),
                    arr,
                    queue
                ),
                CurrencyPair(
                    "etc_ltc",
                    0.543,
                    mapOf("1m" to 324.0323, "1w" to 6673.32340),
                    arr,
                    queue
                ),
                CurrencyPair(
                    "etc_ltc",
                    0.543,
                    mapOf("1m" to 324.0323, "1w" to 6673.32340),
                    arr,
                    queue
                ),
                CurrencyPair(
                    "etc_ltc",
                    0.543,
                    mapOf("1m" to 324.0323, "1w" to 6673.32340),
                    arr,
                    queue
                ),
                CurrencyPair(
                    "etc_ltc",
                    0.543,
                    mapOf("1m" to 324.0323, "1w" to 6673.32340),
                    arr,
                    queue
                ),
                CurrencyPair(
                    "etc_ltc",
                    0.543,
                    mapOf("1m" to 324.0323, "1w" to 6673.32340),
                    arr,
                    queue
                ),
                CurrencyPair(
                    "etc_ltc",
                    0.543,
                    mapOf("1m" to 324.0323, "1w" to 6673.32340),
                    arr,
                    queue
                ),
                CurrencyPair(
                    "etc_ltc",
                    0.543,
                    mapOf("1m" to 324.0323, "1w" to 6673.32340),
                    arr,
                    queue
                ),
                CurrencyPair(
                    "etc_ltc",
                    0.543,
                    mapOf("1m" to 324.0323, "1w" to 6673.32340),
                    arr,
                    queue
                ),
                CurrencyPair(
                    "etc_ltc",
                    0.543,
                    mapOf("1m" to 324.0323, "1w" to 6673.32340),
                    arr,
                    queue
                ),
                CurrencyPair(
                    "etc_ltc",
                    0.543,
                    mapOf("1m" to 324.0323, "1w" to 6673.32340),
                    arr,
                    queue
                )
            ),
            listOf("1s", "1d")
        )
    }





}
package ru.exrates.mobile

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.viewadapters.ExchangesAdapter
import java.util.concurrent.ArrayBlockingQueue

class CurrencyActivity : AppCompatActivity() {
    private lateinit var currencyName: TextView
    private lateinit var currencyInterval: Button
    private lateinit var currencyIntervalValue: TextView
    private lateinit var currencyExchange: TextView
    private lateinit var currencyExchanges: RecyclerView
    private lateinit var exchangesAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var app: MyApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try{
            setContentView(R.layout.currency)
            app = this.application as MyApp
            currencyExchange = findViewById(R.id.cur_exchange)
            currencyName = findViewById(R.id.cur_name)
            currencyInterval = findViewById(R.id.cur_interval)
            currencyIntervalValue = findViewById(R.id.cur_intervalValue)

            val currName = savedInstanceState?.getString(EXTRA_CURRENCY_NAME, "btc_ltc") ?: "btc_ltc"
            //val exchanges = app.dataProvider.exchanges.values.toList()


            currencyName.text = currName

            exchangesAdapter = ExchangesAdapter(testExchanges(), currName)
            viewManager = LinearLayoutManager(this)

            currencyExchanges = findViewById<RecyclerView>(R.id.cur_exchanges).apply{
                adapter = exchangesAdapter
                layoutManager = viewManager

            }
        }catch (e: Exception){
            e.printStackTrace()
        }




    }



    fun testExchanges(): List<Exchange> {
        val ex1 = Exchange(
            "testExchange",
            mutableListOf(
                CurrencyPair(
                    "btc_ltc",
                    0.345,
                    mapOf("1m" to 4453.023, "1h" to 5433.3230),
                    arrayOf(0L),
                    ArrayBlockingQueue(2)
                )
            ),
            listOf("1s", "1h")
        )

        val ex2 = Exchange(
            "testExchange 2",
            mutableListOf(
                CurrencyPair(
                    "btc_ltc",
                    0.657,
                    mapOf("1m" to 4343.023, "1h" to 45333.3230),
                    arrayOf(0L),
                    ArrayBlockingQueue(2)
                )
            ),
            listOf("1s", "1h")
        )

        return listOf(ex1, ex2)
    }
}
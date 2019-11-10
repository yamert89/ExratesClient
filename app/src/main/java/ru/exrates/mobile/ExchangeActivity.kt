package ru.exrates.mobile

import android.os.Bundle
import android.widget.Adapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.viewmodel.PairsAdapter
import java.lang.Exception
import java.util.concurrent.ArrayBlockingQueue

class ExchangeActivity : AppCompatActivity() {
    private lateinit var exchName: TextView
    private lateinit var intervalBtn: Button
    private lateinit var intervalValue: TextView
    private lateinit var pairs: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val app: MyApp = this.application as MyApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.exchange)
            exchName = findViewById(R.id.exName)
            intervalBtn = findViewById(R.id.interval)
            intervalValue = findViewById(R.id.intervalValue)

            val queue = ArrayBlockingQueue<Double>(20)

            val exchName: String? = savedInstanceState!!.getString(EXCH_NAME)



            val exchange: Exchange = app.dataProvider.getSavedExchange(this.applicationContext)

            viewAdapter = PairsAdapter(exchange)
            viewManager = LinearLayoutManager(this)

            pairs = findViewById<RecyclerView>(R.id.pairs).apply {
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }catch (e: Exception){
            e.printStackTrace()

        }


    }




}
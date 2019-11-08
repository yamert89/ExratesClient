package ru.exrates.mobile

import android.os.Bundle
import android.widget.Adapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.exrates.mobile.viewmodel.PairsAdapter

class ExchangeActivity : AppCompatActivity() {
    private lateinit var exchName: TextView
    private lateinit var intervalBtn: Button
    private lateinit var intervalValue: TextView
    private lateinit var pairs: RecyclerView
    private lateinit var viewAdapter: Adapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.exchange)
        exchName = findViewById(R.id.exName)
        intervalBtn = findViewById(R.id.interval)
        intervalValue = findViewById(R.id.intervalValue)

        viewAdapter = PairsAdapter()
        viewManager = LinearLayoutManager(this)

        pairs = findViewById<RecyclerView>(R.id.pairs).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }


    }


}
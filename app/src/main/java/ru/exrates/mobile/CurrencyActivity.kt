package ru.exrates.mobile

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CurrencyActivity : AppCompatActivity() {
    private lateinit var currencyName: TextView
    private lateinit var currencyInterval: Button
    private lateinit var currencyIntervalValue: TextView
    private lateinit var currencyExchange: TextView
    private lateinit var currencyExchangesList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currencyExchange = findViewById(R.id.cur_exchange)
        currencyExchangesList = findViewById(R.id.cur_exchanges)
        currencyName = findViewById(R.id.cur_name)
        currencyInterval = findViewById(R.id.cur_interval)
        currencyIntervalValue = findViewById(R.id.cur_intervalValue)

        currencyExchangesList.ada



    }
}
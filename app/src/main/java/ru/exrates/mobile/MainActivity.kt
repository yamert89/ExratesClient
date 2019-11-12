package ru.exrates.mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var currencyName: TextView
    private lateinit var currencyPrice: TextView
    private lateinit var exchangeName: TextView
    private lateinit var currenciesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        currencyName = findViewById(R.id.main_currency)
        currencyPrice = findViewById(R.id.main_cur_price)
        exchangeName = findViewById(R.id.main_exchange)

    }
}

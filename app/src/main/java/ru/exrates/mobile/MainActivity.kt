package ru.exrates.mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.exrates.mobile.logic.Storage
import ru.exrates.mobile.logic.entities.CurrencyPair

class MainActivity : AppCompatActivity() {

    private lateinit var currencyName: Spinner
    private lateinit var currencyPrice: TextView
    private lateinit var exchangeName: Spinner
    private lateinit var currenciesRecyclerView: RecyclerView
    private lateinit var app: MyApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        app = this.application as MyApp



        val curAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        val exchAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        val exch = app.dataProvider.getSavedExchange(this)
        val oldCur = Storage(applicationContext).loadObject<CurrencyPair>(SAVED_CURRENCY)

        curAdapter.addAll() //todo list currencies
        exchAdapter.addAll() //todo exch list

        currencyName = findViewById(R.id.main_currency_spinner)
        currencyPrice = findViewById(R.id.main_cur_price)
        exchangeName = findViewById(R.id.main_exch_spinner)

        currencyName.adapter = curAdapter
        val defaultCur = "" //todo
        currencyName.setSelection(curAdapter.getPosition(oldCur?.symbol ?: defaultCur))
















    }
}

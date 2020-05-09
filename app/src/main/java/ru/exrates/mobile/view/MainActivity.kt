package ru.exrates.mobile.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import lecho.lib.hellocharts.view.LineChartView
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.R
import ru.exrates.mobile.logic.rest.RestModel
import ru.exrates.mobile.logic.*
import ru.exrates.mobile.view.graph.GraphFactory
import ru.exrates.mobile.view.listeners.ExchangeSpinnerItemSelectedListener
import ru.exrates.mobile.view.listeners.SearchButtonClickListener
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangeNamesObject
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.presenters.MainPresenter
import ru.exrates.mobile.presenters.Presenter
import ru.exrates.mobile.view.viewAdapters.PairsAdapter
import java.io.FileNotFoundException
import java.io.InvalidClassException

class MainActivity : ExratesActivity() {
//fixme Skipped 39 frames!  The application may be doing too much work on its main thread.
    private lateinit var currencyName: Spinner
    private lateinit var currencyPrice: TextView
    private lateinit var exchangeName: Spinner
    private lateinit var goToCurBtn: ImageButton
    private lateinit var currenciesRecyclerView: RecyclerView
    private lateinit var anyChartView: LineChartView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var root: ConstraintLayout
    private lateinit var searchBtn: ImageView
    private lateinit var autoCompleteTextView: AutoCompleteTextView

    private lateinit var presenter: MainPresenter



    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        try {
            logTrace("main oncreate")
            setContentView(R.layout.activity_main)

            currencyName = findViewById(R.id.main_currency_spinner)
            currencyPrice = findViewById(R.id.main_cur_price)
            exchangeName = findViewById(R.id.main_exch_spinner)
            progressLayout = findViewById(R.id.progressLayout)
            anyChartView = findViewById(R.id.anyChartView)
            goToCurBtn = findViewById(R.id.go_to_currency)
            root = findViewById(R.id.root)
            searchBtn = findViewById(R.id.main_search_btn)
            autoCompleteTextView = findViewById(R.id.main_autoComplete)
            presenter = MainPresenter(app.presenter)

            currencyName.adapter = presenter.getCurSpinnerAdapter()
            exchangeName.adapter =  presenter.getExSpinnerAdapter()

            viewManager = LinearLayoutManager(this)


            currenciesRecyclerView = findViewById<RecyclerView>(R.id.main_cur_list).apply {
                adapter = presenter.getCurrencyAdapter()
                layoutManager = viewManager
            }

            exchangeName.setSelection(0)

            currencyName.setSelection(0)

            currencyName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = startCurActivity(position)
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            goToCurBtn.setOnClickListener { startCurActivity() }

            searchBtn.setOnClickListener(
                SearchButtonClickListener(
                    autoCompleteTextView,
                    currencyName,
                    this
                )
            )

            autoCompleteTextView.setAdapter(presenter.getSearchAdapter())

            exchangeName.onItemSelectedListener = ExchangeSpinnerItemSelectedListener(this, app, presenter )

            logD("Main activity created")

        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    fun selectExchangeItem(idx: Int){
        exchangeName.setSelection(idx)
    }

    fun selectPairItem(idx: Int){
        currencyName.setSelection(idx)
    }

    fun setCurrencyPrice(value: String){
        currencyPrice.text = value
    }

    private fun startCurActivity(position: Int = Int.MAX_VALUE){
        if (position != Int.MAX_VALUE) {
            if (currencyName.selectedItemPosition == position) return
            presenter.updateCurIdx(position)
        }

        startActivity(Intent(applicationContext, CurrencyActivity::class.java).apply {
            val preparedValues = presenter.prepareStartCurActivity()

            putExtra(EXTRA_CURRENCY_NAME_1, preparedValues.first )
            putExtra(EXTRA_CURRENCY_NAME_2, preparedValues.second )
            // putExtra(EXTRA_EXCHANGE_NAME, exName)
            // putExtra(EXTRA_EXCHANGE_ID, app.exchangeNamesList!!.find { it.name == exName }!!.id)
            var id = app.baseContext.resources.getIdentifier(preparedValues.first.toLowerCase(), "drawable", app.baseContext.packageName)
            if (id == 0) id = android.R.drawable.ic_menu_help
            putExtra(EXTRA_CUR_ICO, id)
            putExtra(EXTRA_EXCHANGE_ID, preparedValues.third)

        })
    }



    fun updateGraph(cur: CurrencyPair) {
        if (cur.priceHistory.isEmpty()) {
            root.removeView(anyChartView)
            logE("Graph removed")
            val notice = TextView(app.baseContext).apply {
                text = "Data not available"
            }
            root.addView(notice, 4)
        } else GraphFactory(anyChartView, "1h")
            .createSmallGraph(cur.priceHistory.subList(cur.priceHistory.size - 10, cur.priceHistory.lastIndex + 1))
    }







    override fun startProgress(){
        super.startProgress()
        logTrace("Snack started..")
        Snackbar.make(currenciesRecyclerView, "Первичная загрузка данных, подождите", Snackbar.LENGTH_LONG).show()
    }




    override fun onResume() {
        super.onResume()
        if (!this::presenter.isInitialized) presenter = MainPresenter(app.presenter)
        presenter.attachView(this, null)
        presenter.resume()
    }




}

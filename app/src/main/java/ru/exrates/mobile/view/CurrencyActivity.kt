package ru.exrates.mobile.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import lecho.lib.hellocharts.view.LineChartView
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.R
import ru.exrates.mobile.logic.rest.RestModel
import ru.exrates.mobile.logic.*
import ru.exrates.mobile.view.graph.GraphFactory
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.SelectedExchange
import ru.exrates.mobile.logic.structures.IntervalComparator
import ru.exrates.mobile.presenters.CurrencyPresenter
import ru.exrates.mobile.presenters.Presenter
import ru.exrates.mobile.view.viewAdapters.ExchangesAdapter
import java.util.*

class CurrencyActivity : ExratesActivity() {
    private lateinit var currencyName: TextView
    private lateinit var currencyInterval: Button
    private lateinit var currencyIntervalValue: TextView
    private lateinit var anyChartView: LineChartView
    //private lateinit var currencyExchange: TextView
    private lateinit var currencyExchanges: RecyclerView

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var historyPeriodSpinner: Spinner
    private lateinit var root: ConstraintLayout
    private lateinit var curIco : ImageView

    private lateinit var presenter: CurrencyPresenter


        //private var activeExchangeName = "binanceExchange"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try{
            setContentView(R.layout.currency)
            app = this.application as MyApp
            //currencyExchange = findViewById(R.id.cur_exchange)
            currencyName = findViewById(R.id.cur_name)
            currencyInterval = findViewById(R.id.cur_interval)
            currencyIntervalValue = findViewById(R.id.cur_intervalValue)
            progressLayout = findViewById(R.id.progressLayout)
            historyPeriodSpinner = findViewById(R.id.cur_history_period)
            anyChartView = findViewById(R.id.anyChartView_cur)
            curIco = findViewById(R.id.cur_ico)
            root = findViewById(R.id.currency)
            //storage = Storage(applicationContext)
            presenter = CurrencyPresenter(app)
            presenter.attachView(this)



            val currName1: String = intent.getStringExtra(EXTRA_CURRENCY_NAME_1)!!
            val currName2: String = intent.getStringExtra(EXTRA_CURRENCY_NAME_2)!!
            presenter.setCurNames(currName1, currName2)
            presenter.start()
            //app.currentExchangeId = intent.getIntExtra(EXTRA_EXCHANGE_ID, 1)

            //updateIntervals()

            val curIcoId = intent.getIntExtra(EXTRA_CUR_ICO, 0)
            curIco.setImageDrawable(ResourcesCompat.getDrawable(app.resources, curIcoId, null))

            historyPeriodSpinner.adapter = presenter.getHistorySpinnerAdapter()


            //historyPeriodSpinner.setSelection(currentGraphIntervalIdx)
            historyPeriodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?){}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    //val interval = parent?.getItemAtPosition(position) as String
                    presenter.selectHistoryInterval(position)
                }
            }

            currencyName.text = "$currName1 / $currName2"

            viewManager = LinearLayoutManager(this)

            currencyExchanges = findViewById<RecyclerView>(R.id.cur_exchanges).apply{
                adapter = presenter.getExchAdapter()
                layoutManager = viewManager
            }

            //currencyIntervalValue.text = intervals.first()

            currencyInterval.setOnClickListener {
                currencyIntervalValue.text = presenter.clickOnInterval()

            }

        }catch (e: Exception){
            Log.d(null, "Current activity start failed", e)
        }

    }

    fun selectHistory(idx: Int) = historyPeriodSpinner.setSelection(idx)

    fun setInterval(value: String){
        currencyIntervalValue.text = value
    }

    fun updateGraph(list: List<Double>, currentGraphInterval: String){
        //val data = mutableListOf<ValueDataEntry>()
        if (list.isEmpty()) {
            root.removeView(anyChartView)
            val notice = TextView(app.baseContext).apply {
                text = "Data not available"
            }
            root.addView(notice, 2)
        } else {
            GraphFactory(
                anyChartView,
                currentGraphInterval
            ).createBigGraph(list)

            //list.forEach { data.add(ValueDataEntry("1", it)) }
            logD("updating graph with ${list.joinToString()}")
        }

    }

}
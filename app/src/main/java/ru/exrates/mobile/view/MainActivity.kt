package ru.exrates.mobile.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import lecho.lib.hellocharts.view.LineChartView
import ru.exrates.mobile.R
import ru.exrates.mobile.logic.*
import ru.exrates.mobile.view.graph.GraphFactory
import ru.exrates.mobile.view.listeners.SearchButtonClickListener
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.presenters.MainPresenter
import ru.exrates.mobile.view.prefs.SettingsActivity

class MainActivity : ExratesActivity() {

    private lateinit var currencyName: Spinner
    private lateinit var currencyPrice: TextView
    private lateinit var exchangeName: Spinner
    private lateinit var goToCurBtn: ImageButton
    private lateinit var goToExBtn: ImageButton
    private lateinit var currenciesRecyclerView: RecyclerView
    private lateinit var anyChartView: LineChartView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var root: ConstraintLayout
    private lateinit var searchBtn: ImageView
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var chartProgress: ConstraintLayout

    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        try {
            logD("START MAIN ACTIVITY ")
            setContentView(R.layout.activity_main)

            currencyName = findViewById(R.id.main_currency_spinner)
            currencyPrice = findViewById(R.id.main_cur_price)
            exchangeName = findViewById(R.id.main_exch_spinner)
            progressLayout = findViewById(R.id.progress)
            anyChartView = findViewById(R.id.anyChartView)
            goToCurBtn = findViewById(R.id.go_to_currency)
            root = findViewById(R.id.root)
            searchBtn = findViewById(R.id.main_search_btn)
            autoCompleteTextView = findViewById(R.id.main_autoComplete)
            goToExBtn = findViewById(R.id.main_go_to_ex)
            chartProgress = findViewById(R.id.main_chart_progress)
            presenter = MainPresenter(app)
            presenter.attachView(this)

            currencyName.adapter = presenter.getCurSpinnerAdapter()
            exchangeName.adapter =  presenter.getExSpinnerAdapter()

            viewManager = LinearLayoutManager(this)
            currenciesRecyclerView = findViewById<RecyclerView>(R.id.main_cur_list)
            GlobalScope.launch(Dispatchers.Main){
                currenciesRecyclerView.adapter = presenter.getCurrencyAdapter()
                currenciesRecyclerView.layoutManager = viewManager
                currencyName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = startCurActivity(position)
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

                goToCurBtn.setOnClickListener { startCurActivity() }

                goToExBtn.setOnClickListener(presenter.getExSpinnerItemSelectedListener())

                searchBtn.setOnClickListener(
                    SearchButtonClickListener(
                        autoCompleteTextView,
                        currencyName,
                        goToCurBtn,
                        this@MainActivity
                    )
                )

                autoCompleteTextView.setAdapter(presenter.getSearchAdapter())


                exchangeName.onItemSelectedListener = presenter.getExSpinnerItemSelectedListener()
                logD("init main activity coroutine done")
            }




            logD("Main activity created")

        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    fun selectExchangeItem(idx: Int){
        /*val listener = exchangeName.onItemSelectedListener as ExchangeSpinnerItemSelectedListener
        listener.activated = false
        logD("Listener deactivated")*/
        logD("Select exchange item from presenter with old idx: ${exchangeName.selectedItemPosition} and new idx: $idx")
        exchangeName.setSelection(idx)
    }

    fun getSelectedExchangeIdx() = exchangeName.selectedItemPosition

    fun selectPairItem(idx: Int){
        currencyName.setSelection(idx)
    }

    fun setCurrencyPrice(value: String){
        currencyPrice.text = value
    }

    private fun startCurActivity(position: Int = Int.MAX_VALUE){
        if (position != Int.MAX_VALUE) {
            if (presenter.getCurIdx() == position) return
            logD("start cur activity from spinner with position $position")
            presenter.updateCurIdx(position)
        } else logD("start cur activity from go button")

        startActivity(Intent(applicationContext, CurrencyActivity::class.java).apply {
            val preparedValues = presenter.prepareStartCurActivity()
            putExtra(EXTRA_CURRENCY_NAME_1, preparedValues.first )
            putExtra(EXTRA_CURRENCY_NAME_2, preparedValues.second )
            var id = app.baseContext.resources.getIdentifier(preparedValues.first.toLowerCase(), "drawable", app.baseContext.packageName)
            if (id == 0) id = android.R.drawable.ic_menu_help
            putExtra(EXTRA_CUR_ICO, id)
        })
    }

    fun updateGraph(cur: CurrencyPair) {
        if (cur.priceHistory.isEmpty()) {
            root.removeView(anyChartView)
            logE("Graph removed")
            val id = View.generateViewId()
            val notice = TextView(app.baseContext).apply {
                text = "Graphic data not available"
                this.id = id
            }
            root.addView(notice, 6)
            ConstraintSet().run {
                clone(root)
                connect(id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 200)
                connect(id, ConstraintSet.TOP, R.id.main_cur_price, ConstraintSet.BOTTOM, 200)
                applyTo(root)
            }

        } else GraphFactory(anyChartView, "1h")
            .createSmallGraph(cur.priceHistory.subList(cur.priceHistory.size - 10, cur.priceHistory.lastIndex + 1))
        chartProgress.visibility = View.INVISIBLE
    }

    override fun startProgress(){
        super.startProgress()
        logT("Snack started..")
        print(System.currentTimeMillis())
    }

    fun firstNotice(){
        Snackbar.make(currenciesRecyclerView, "Первичная загрузка данных, подождите", Snackbar.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        logD("onresume")
        presenter.resume()
    }

    override fun onPause() {
        super.onPause()
        presenter.pause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings -> {
                startActivity(Intent(applicationContext, SettingsActivity::class.java))
                return true
            }
            else -> {
                throw IllegalArgumentException("menu item not found")
            }
        }

    }

}

package ru.exrates.mobile.view

import android.os.Bundle
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.exrates.mobile.R
import ru.exrates.mobile.logic.EXTRA_EXCHANGE_ICO
import ru.exrates.mobile.logic.logD
import ru.exrates.mobile.presenters.ExchangePresenter
import ru.exrates.mobile.view.listeners.CurNamesSpinnerItemSelectedListener
import ru.exrates.mobile.view.viewAdapters.PairsAdapter

class ExchangeActivity : ExratesActivity() {
    private lateinit var exIco: ImageView
    private lateinit var intervalBtn: Button
    private lateinit var intervalValue: TextView
    private lateinit var pairs: RecyclerView
    private lateinit var addCurrency: Spinner
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var presenter: ExchangePresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            logD("START EXCHANGE ACTIVITY ")
            setContentView(R.layout.exchange)
            //storage = Storage(applicationContext)

            exIco = findViewById(R.id.exIco)
            intervalBtn = findViewById(R.id.cur_interval)
            intervalValue = findViewById(R.id.intervalValue)
            progressLayout = findViewById(R.id.progress)
            addCurrency = findViewById(R.id.ex_pairs)
            presenter = ExchangePresenter(app)
            presenter.attachView(this)
            presenter.start()

            viewManager = LinearLayoutManager(this)

            addCurrency.adapter = presenter.getCurNamesAdapter()
           // addCurrency.setSelection(-1)
            addCurrency.onItemSelectedListener = CurNamesSpinnerItemSelectedListener(presenter)



            intervalBtn.setOnClickListener {
                setInterval(presenter.changeInterval())
            }

            val icoId = intent.getIntExtra(EXTRA_EXCHANGE_ICO, 0)
            exIco.setImageDrawable(ResourcesCompat.getDrawable(app.resources, icoId, null ))

            //startProgress()

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun setInterval(value: String){
        intervalValue.text = value
    }

    fun setPairsAdapter(pairsAdapter: PairsAdapter){
        pairs = findViewById<RecyclerView>(R.id.pairs).apply {
            layoutManager = viewManager
            adapter = pairsAdapter
        }
    }






}
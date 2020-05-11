package ru.exrates.mobile.view

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.exchange.*
import ru.exrates.mobile.R
import ru.exrates.mobile.logic.CURRENT_INTERVAL
import ru.exrates.mobile.logic.EXTRA_EXCHANGE_ICO
import ru.exrates.mobile.logic.rest.RestModel
import ru.exrates.mobile.logic.SAVED_EXCHANGE_NAME_LIST
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.presenters.ExchangePresenter
import ru.exrates.mobile.view.viewAdapters.PairsAdapter

class ExchangeActivity : ExratesActivity() {
    private lateinit var exIco: ImageView
    private lateinit var intervalBtn: Button
    private lateinit var intervalValue: TextView
    private lateinit var pairs: RecyclerView

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var presenter: ExchangePresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.exchange)
            //storage = Storage(applicationContext)


            exIco = findViewById(R.id.exIco)
            intervalBtn = findViewById(R.id.cur_interval)
            intervalValue = findViewById(R.id.intervalValue)
            progressLayout = findViewById(R.id.progressLayout)
            presenter = ExchangePresenter(app)
            presenter.attachView(this)

            viewManager = LinearLayoutManager(this)

            pairs = findViewById<RecyclerView>(R.id.pairs).apply {
                layoutManager = viewManager
                adapter = presenter.getPairsAdapt()
            }

            intervalBtn.setOnClickListener {
                intervalValue.text = presenter.changeInterval(intervalValue.text.toString())
            }

            val icoId = intent.getIntExtra(EXTRA_EXCHANGE_ICO, 0)
            exIco.setImageDrawable(ResourcesCompat.getDrawable(app.resources, icoId, null ))
            //val exId = intent.getIntExtra(EXTRA_EXCHANGE_ID, 1)

            //startProgress()

        }catch (e: Exception){
            e.printStackTrace()

        }


    }





}
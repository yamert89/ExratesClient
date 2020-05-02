package ru.exrates.mobile.viewadapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import ru.exrates.mobile.CURRENCY_HISTORIES_CUR_NUMBER
import ru.exrates.mobile.R
import ru.exrates.mobile.log_d
import ru.exrates.mobile.logic.Model
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.toNumeric

class ExchangesAdapter(val pairsByExchanges: MutableList<CurrencyPair>,
                       private val model: Model,
                       var interval: String = "1h",
                       private val defaultExId: Int = 1 ): RecyclerView.Adapter<ExchangesAdapter.ExchangeViewHolder>() {
    private val selectedColor = Color.parseColor("#4003A9F4")
    private val defaultColor = Color.parseColor("#40E4E4E4")
    private var selectedRow: LinearLayout? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeViewHolder {
        val linearLayout = LayoutInflater.from(parent.context).inflate(R.layout.exchange_row,
            parent, false) as LinearLayout
        return ExchangeViewHolder(linearLayout)
    }

    override fun getItemCount() = pairsByExchanges.size

    override fun onBindViewHolder(holder: ExchangeViewHolder, position: Int) {
        val pair = pairsByExchanges[position]
        if (pair.exId == defaultExId) holder.linearLayout.setBackgroundColor(selectedColor)
        holder.linearLayout.findViewById<TextView>(R.id.cur_exchanges_name).text = pair.exchangeName
        holder.linearLayout.findViewById<TextView>(R.id.cur_exchanges_price).text = pair.price.toNumeric().toString()
        holder.linearLayout.findViewById<TextView>(R.id.cur_exchanges_change).text = pair.priceChange[interval].toString() + "%"
        holder.linearLayout.setOnClickListener {
            log_d("old selected row: $selectedRow, selected item id: ${it.id}")
            model.getPriceHistory(pair.baseCurrency, pair.quoteCurrency, pair.exId, interval, CURRENCY_HISTORIES_CUR_NUMBER)
            if (it == selectedRow) return@setOnClickListener
            selectedRow?.setBackgroundColor(defaultColor)
            it.setBackgroundColor(selectedColor)
            selectedRow = it as LinearLayout
            //todo current exchange select, graph intervals update
        }


    }

    class ExchangeViewHolder(val linearLayout: LinearLayout): RecyclerView.ViewHolder(linearLayout)
}
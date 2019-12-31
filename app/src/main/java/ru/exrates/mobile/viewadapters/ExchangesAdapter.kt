package ru.exrates.mobile.viewadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.exrates.mobile.R
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.toNumeric

class ExchangesAdapter(val pairsByExchanges: MutableList<CurrencyPair>, private val pairName : String, var interval: String = "1h"): RecyclerView.Adapter<ExchangesAdapter.ExchangeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeViewHolder {
        val linearLayout = LayoutInflater.from(parent.context).inflate(R.layout.exchange_row,
            parent, false) as LinearLayout
        return ExchangeViewHolder(linearLayout)
    }

    override fun getItemCount() = pairsByExchanges.size

    override fun onBindViewHolder(holder: ExchangeViewHolder, position: Int) {
        val pair = pairsByExchanges[position]
        holder.linearLayout.findViewById<TextView>(R.id.cur_exchanges_name).text = pair.exchangeName
        holder.linearLayout.findViewById<TextView>(R.id.cur_exchanges_price).text = pair.price.toNumeric().toString()
        holder.linearLayout.findViewById<TextView>(R.id.cur_exchanges_change).text = pair.priceChange[interval].toString()
    }

    class ExchangeViewHolder(val linearLayout: LinearLayout): RecyclerView.ViewHolder(linearLayout)
}
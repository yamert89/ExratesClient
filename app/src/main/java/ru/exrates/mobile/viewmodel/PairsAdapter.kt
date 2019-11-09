package ru.exrates.mobile.viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.currency_row.view.*
import ru.exrates.mobile.R
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange


class PairsAdapter(private val dataExchange: Exchange) : RecyclerView.Adapter<PairsAdapter.PairsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairsViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_row, parent, false) as LinearLayout

        return PairsViewHolder(linearLayout)
    }

    override fun getItemCount() = dataExchange.pairs.size

    override fun onBindViewHolder(holder: PairsViewHolder, position: Int) {
        val pair = dataExchange.pairs[position]
        holder.linearLayout.findViewById<TextView>(R.id.rec_cur_name).text = pair.symbol
        holder.linearLayout.findViewById<TextView>(R.id.rec_cur_price).text = pair.price.toString()
        val currentInterval = "1m" //todo push out
        holder.linearLayout.findViewById<TextView>(R.id.rec_cur_change).text = pair.priceChange[currentInterval].toString()

    }

    class PairsViewHolder(val linearLayout: LinearLayout): RecyclerView.ViewHolder(linearLayout) {

    }
}




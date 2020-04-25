package ru.exrates.mobile.viewadapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import ru.exrates.mobile.R
import ru.exrates.mobile.log_d
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.toNumeric

class ExchangesAdapter(val pairsByExchanges: MutableList<CurrencyPair>, var interval: String = "1h", var selectedRow: LinearLayout? = null): RecyclerView.Adapter<ExchangesAdapter.ExchangeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeViewHolder {
        val linearLayout = LayoutInflater.from(parent.context).inflate(R.layout.exchange_row,
            parent, false) as LinearLayout

       /* linearLayout.setOnClickListener {
            log_d("old selected row: $selectedRow, selected item id: ${it.id}")
            if (it.id == selectedRow) return@setOnClickListener
            it.setBackgroundColor(Color.RED)
            parent[selectedRow].setBackgroundColor(Color.BLUE)
            selectedRow = it.id
        }*/
        return ExchangeViewHolder(linearLayout)
    }

    override fun getItemCount() = pairsByExchanges.size

    override fun onBindViewHolder(holder: ExchangeViewHolder, position: Int) {
        val pair = pairsByExchanges[position]
        holder.linearLayout.findViewById<TextView>(R.id.cur_exchanges_name).text = pair.exchangeName
        holder.linearLayout.findViewById<TextView>(R.id.cur_exchanges_price).text = pair.price.toNumeric().toString()
        holder.linearLayout.findViewById<TextView>(R.id.cur_exchanges_change).text = pair.priceChange[interval].toString() + "%"
        holder.linearLayout.setOnClickListener {
            log_d("old selected row: $selectedRow, selected item id: ${it.id}")
            if (it == selectedRow) return@setOnClickListener
            selectedRow?.setBackgroundColor(Color.BLUE)
            it.setBackgroundColor(Color.RED)
            selectedRow = it as LinearLayout
        }



    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(observer)
    }

    class ExchangeViewHolder(val linearLayout: LinearLayout): RecyclerView.ViewHolder(linearLayout)
}
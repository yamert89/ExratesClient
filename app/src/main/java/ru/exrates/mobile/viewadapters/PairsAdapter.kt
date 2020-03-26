package ru.exrates.mobile.viewadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.exrates.mobile.R
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.toNumeric


open class PairsAdapter() : RecyclerView.Adapter<PairsAdapter.PairsViewHolder>() {
    lateinit var dataPairs: MutableList<CurrencyPair>
    lateinit var currentInterval: String

    constructor(dataPairs: MutableList<CurrencyPair>, currentInterval: String = "1h") : this(){
        this.dataPairs = dataPairs
        this.currentInterval = currentInterval

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairsViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_row, parent, false) as LinearLayout
        return PairsViewHolder(linearLayout)
    }

    override fun getItemCount() = dataPairs.size

    override fun onBindViewHolder(holder: PairsViewHolder, position: Int) {
        val pair = dataPairs[position]
        holder.linearLayout.findViewById<TextView>(R.id.rec_cur_name).text = pair.symbol
        holder.linearLayout.findViewById<TextView>(R.id.rec_cur_price).text = pair.price.toNumeric().toString()
        holder.linearLayout.findViewById<TextView>(R.id.rec_cur_change).text = pair.priceChange[currentInterval].toString()
        holder.linearLayout.findViewById<CheckBox>(R.id.rec_cur_visible).isChecked = pair.visible
    }

    class PairsViewHolder(val linearLayout: LinearLayout): RecyclerView.ViewHolder(linearLayout)
}




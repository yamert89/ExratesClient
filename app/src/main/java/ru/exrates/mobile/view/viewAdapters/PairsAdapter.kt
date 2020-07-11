package ru.exrates.mobile.view.viewAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import ru.exrates.mobile.*
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.logD
import ru.exrates.mobile.logic.logE
import ru.exrates.mobile.logic.logW
import ru.exrates.mobile.logic.toNumeric
import java.math.BigDecimal
import java.math.MathContext

@JsonIgnoreProperties("itemCount", "app")
open class PairsAdapter() : RecyclerView.Adapter<PairsAdapter.PairsViewHolder>() {
    lateinit var dataPairs: MutableList<CurrencyPair>
    lateinit var currentInterval: String
    lateinit var app: MyApp

    constructor(dataPairs: MutableList<CurrencyPair>, currentInterval: String = "1h", app: MyApp) : this(){
        this.dataPairs = dataPairs
        this.currentInterval = currentInterval
        this.app = app
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairsViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_row, parent, false) as LinearLayout
        return PairsViewHolder(
            linearLayout
        )
    }

    override fun getItemCount() = dataPairs.size

    override fun onBindViewHolder(holder: PairsViewHolder, position: Int) {
        val pair = dataPairs[position]
        /*if (pair.priceChange.isEmpty()) {
            logW("Empty price change in pairs adapter of pair: $pair")
            return
        }*/
        var change: String
        try{
            var res = app.baseContext.resources.getIdentifier(pair.baseCurrency.toLowerCase(), "drawable", app.baseContext.packageName)
            if (res == 0) res = android.R.drawable.ic_menu_help
            holder.linearLayout.findViewById<ImageView>(R.id.rec_cur_ico).setImageDrawable(ResourcesCompat.getDrawable(
                app.resources,
                res,
                null)
            )
            holder.linearLayout.findViewById<TextView>(R.id.rec_cur_name).text = pair.symbolItem()
            holder.linearLayout.findViewById<TextView>(R.id.rec_cur_price).text = pair.price.toNumeric().toString()
            logD(pair.priceChange.toString())
            val value: Double
            if (pair.priceChange.isNotEmpty()){
                value = pair.priceChange[currentInterval]!!
                change = BigDecimal( value, MathContext(2)).toDouble().toString()
                if (change.length > 5) change = "0.0"
                holder.linearLayout.findViewById<TextView>(R.id.rec_cur_change).text = if (value == Double.MAX_VALUE) "?" else "$change%"
            } else holder.linearLayout.findViewById<TextView>(R.id.rec_cur_change).text = "?"

            val cross = holder.linearLayout.findViewById<ImageView>(R.id.rec_cur_delete)
            cross.setOnClickListener {
                dataPairs.removeIf {it2 -> it2.symbol == pair.symbol }
                logD("${pair.symbol} deleted")
                notifyDataSetChanged()
                app.currentExchange?.pairs?.removeIf { it3 -> it3.symbol == pair.symbol }
            }
        }catch (e: Exception){
            e.printStackTrace()
            logE("pair: $pair , current Interval: $currentInterval")
        }


    }


    class PairsViewHolder(val linearLayout: LinearLayout): RecyclerView.ViewHolder(linearLayout)
}




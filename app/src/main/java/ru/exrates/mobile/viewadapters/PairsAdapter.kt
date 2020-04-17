package ru.exrates.mobile.viewadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Transient
import okhttp3.internal.lockAndWaitNanos
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.R
import ru.exrates.mobile.log_d
import ru.exrates.mobile.logic.entities.BindedImageView
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.toNumeric
import java.math.BigDecimal
import java.math.MathContext

@JsonIgnoreProperties("itemCount", "app")
open class PairsAdapter() : RecyclerView.Adapter<PairsAdapter.PairsViewHolder>() {
    lateinit var dataPairs: MutableList<CurrencyPair>
    lateinit var currentInterval: String
    lateinit var app: MyApp

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
        var res = app.baseContext.resources.getIdentifier(pair.baseCurrency.toLowerCase(), "drawable", app.baseContext.packageName)
        if (res == 0) res = R.drawable.etc //todo default ico
        holder.linearLayout.findViewById<ImageView>(R.id.rec_cur_ico).setImageDrawable(ResourcesCompat.getDrawable(
            app.resources,
            res,
            null)
        )
        holder.linearLayout.findViewById<TextView>(R.id.rec_cur_name).text = "${pair.baseCurrency} / ${pair.quoteCurrency}"
        holder.linearLayout.findViewById<TextView>(R.id.rec_cur_price).text = pair.price.toNumeric().toString()
        var change = BigDecimal(pair.priceChange[currentInterval]!! , MathContext(2)).toDouble().toString()
        if (change.length > 5) change = "0.0"
        holder.linearLayout.findViewById<TextView>(R.id.rec_cur_change).text = "$change%"
        val cross = holder.linearLayout.findViewById<ImageView>(R.id.rec_cur_delete)
        cross.setOnClickListener {
            dataPairs.removeIf {it2 -> it2.symbol == pair.symbol }
            log_d("${pair.symbol} deleted")
            notifyDataSetChanged()
            app.currentExchange?.pairs?.removeIf { it3 -> it3.symbol == pair.symbol }
        }

    }


    class PairsViewHolder(val linearLayout: LinearLayout): RecyclerView.ViewHolder(linearLayout)
}




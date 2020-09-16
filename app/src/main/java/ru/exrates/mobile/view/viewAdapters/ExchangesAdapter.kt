package ru.exrates.mobile.view.viewAdapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import ru.exrates.mobile.*
import ru.exrates.mobile.logic.CURRENCY_HISTORIES_CUR_NUMBER
import ru.exrates.mobile.logic.rest.RestModel
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.SelectedExchange
import ru.exrates.mobile.logic.logD
import ru.exrates.mobile.logic.toNumeric
/**
 * Adapter for exchanges spinner on main activity
 * @param pairsByExchanges list of currency pair
 * @param restModel restModel from presenter
 * @param app this app
 * @param interval curreny change interval
 * @param selectedExchange selected exchange*/
class ExchangesAdapter(val pairsByExchanges: MutableList<CurrencyPair>,
                       private val restModel: RestModel,
                       private val app: MyApp,
                       var interval: String,
                       val selectedExchange: SelectedExchange ): RecyclerView.Adapter<ExchangesAdapter.ExchangeViewHolder>() {
    private val selectedColor = Color.parseColor("#4003A9F4")
    private val defaultColor = Color.parseColor("#40E4E4E4")
    private var selectedRow: LinearLayout? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeViewHolder {
        val linearLayout = LayoutInflater.from(parent.context).inflate(R.layout.exchange_row,
            parent, false) as LinearLayout
        return ExchangeViewHolder(
            linearLayout
        )
    }

    override fun getItemCount() = pairsByExchanges.size

    override fun onBindViewHolder(holder: ExchangeViewHolder, position: Int) {
        val pair = pairsByExchanges[position]
        logD("Exchange adapter: updated pair - $pair")
        if (pair.exId == selectedExchange.id) holder.linearLayout.setBackgroundResource(R.drawable.selected_item)
        holder.linearLayout.findViewById<TextView>(R.id.cur_exchanges_price).text = pair.price.toNumeric()
        holder.linearLayout.findViewById<TextView>(R.id.cur_exchanges_change).text =
            if (pair.priceChange[interval] == null || pair.priceChange[interval] == Double.MAX_VALUE) "?" else pair.priceChange[interval].toString() + "%"
        if (pair.exId == selectedExchange.id) selectedRow = holder.linearLayout
        val res = when(pair.exId){
            1 -> R.drawable.binance
            2 -> R.drawable.p2pb2b
            3 -> R.drawable.coinbase
            else -> 1
        }
        holder.linearLayout.findViewById<ImageView>(R.id.cur_exchanges_logo).setImageDrawable(ResourcesCompat.getDrawable( //fixme add huobi
            app.resources,
            res,
            null
        ))
        holder.linearLayout.setOnClickListener {
            logD("old selected row: $selectedRow, selected item id: ${it.id}")
            restModel.getPriceHistory(pair.baseCurrency, pair.quoteCurrency, pair.exId, interval,
                CURRENCY_HISTORIES_CUR_NUMBER
            )
            if (it == selectedRow) return@setOnClickListener
            selectedRow?.setBackgroundColor(defaultColor)
            it.setBackgroundResource(R.drawable.selected_item)
            selectedRow = it as LinearLayout
            selectedExchange.id = pair.exId
        }


    }

    class ExchangeViewHolder(val linearLayout: LinearLayout): RecyclerView.ViewHolder(linearLayout)
}
package ru.exrates.mobile.viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.currency_row.view.*
import ru.exrates.mobile.R


class PairsAdapter(private val dataset: Array<String>) : RecyclerView.Adapter<PairsAdapter.PairsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairsViewHolder {
        val linearLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_row, parent, false) as LinearLayout

        return PairsViewHolder(linearLayout)
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: PairsViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class PairsViewHolder(val linearLayout: LinearLayout): RecyclerView.ViewHolder(linearLayout) {

    }
}




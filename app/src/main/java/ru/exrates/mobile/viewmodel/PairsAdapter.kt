package ru.exrates.mobile.viewmodel

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class PairsAdapter(private val dataset: Array<String>) : RecyclerView.Adapter<PairsAdapter.PairsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairsViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: PairsViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class PairsViewHolder(val text: TextView): RecyclerView.ViewHolder(text) {

    }
}




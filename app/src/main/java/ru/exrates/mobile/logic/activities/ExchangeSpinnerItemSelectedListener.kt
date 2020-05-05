package ru.exrates.mobile.logic.activities

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import ru.exrates.mobile.*

class ExchangeSpinnerItemSelectedListener(private val mainActivity: MainActivity, private val app: MyApp) : AdapterView.OnItemSelectedListener {
    private var activated = false

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (!activated) {
            activated = true
            return
        }
        if (app.exchangeNamesList == null || parent == null || parent.count < 2) return

        val exchName = parent.getItemAtPosition(position)
        val exId = app.exchangeNamesList!!.find { it.name == exchName }?.id ?: throw IllegalArgumentException("ex id not found in exchangeNamesList with $exchName ex name")

        mainActivity.startActivity(Intent(mainActivity.applicationContext, ExchangeActivity::class.java).apply{
            putExtra(EXTRA_EXCHANGE_ICO, getIcoId(exId))
            putExtra(EXTRA_EXCHANGE_ID, exId)
        })
    }

    private fun getIcoId(exId: Int) = when(exId){
        1 -> R.drawable.binance
        2 -> R.drawable.p2pb2b
        else -> throw IllegalArgumentException("ex $exId icon id  not found")
    }
}
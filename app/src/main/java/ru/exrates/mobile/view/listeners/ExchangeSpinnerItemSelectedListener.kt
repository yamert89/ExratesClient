package ru.exrates.mobile.view.listeners

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import ru.exrates.mobile.*
import ru.exrates.mobile.logic.EXTRA_EXCHANGE_ICO
import ru.exrates.mobile.logic.EXTRA_EXCHANGE_ID
import ru.exrates.mobile.presenters.MainPresenter
import ru.exrates.mobile.view.ExchangeActivity
import ru.exrates.mobile.view.MainActivity

class ExchangeSpinnerItemSelectedListener(private val mainActivity: MainActivity,
                                          private val app: MyApp, private val presenter: MainPresenter) : AdapterView.OnItemSelectedListener {
    private var activated = false

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (!activated) {
            activated = true
            return
        }
        if (app.exchangeNamesList == null || parent == null || parent.count < 2) return

        presenter.updateExIdx(position)
        val exchName = parent.getItemAtPosition(position)
        val exId = app.exchangeNamesList!!.find { it.name == exchName }?.id ?: throw IllegalArgumentException("ex id not found in exchangeNamesList with $exchName ex name")

        mainActivity.startActivity(Intent(mainActivity.applicationContext, ExchangeActivity::class.java).apply{
            putExtra(EXTRA_EXCHANGE_ICO, getIcoId(exId))
            //putExtra(EXTRA_EXCHANGE_ID, exId)
        })
    }

    private fun getIcoId(exId: Int) = when(exId){
        1 -> R.drawable.binance
        2 -> R.drawable.p2pb2b
        else -> throw IllegalArgumentException("ex $exId icon id  not found")
    }
}
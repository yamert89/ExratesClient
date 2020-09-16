package ru.exrates.mobile.view.listeners

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import ru.exrates.mobile.*
import ru.exrates.mobile.data.Storage
import ru.exrates.mobile.logic.EXTRA_EXCHANGE_ICO
import ru.exrates.mobile.logic.EXTRA_EXCHANGE_ID
import ru.exrates.mobile.logic.SAVED_EXID
import ru.exrates.mobile.logic.logD
import ru.exrates.mobile.presenters.MainPresenter
import ru.exrates.mobile.view.ExchangeActivity
import ru.exrates.mobile.view.MainActivity

class ExchangeSpinnerItemSelectedListener(private val mainActivity: MainActivity,
                                          private val app: MyApp,
                                          private val presenter: MainPresenter) : AdapterView.OnItemSelectedListener, View.OnClickListener {
   var activated = false

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        logD("EXCHANGE Item selected")
        if (!activated) {
            logD("exchange listener not activated")
            activated = true
            return
        }
        if (app.exchangeNamesList.isEmpty() || parent == null || parent.count < 2) return
        presenter.updateExIdx(position)
        val exchName = parent.getItemAtPosition(position)
        val exId = app.exchangeNamesList.values.find { it.name == exchName }?.id ?: throw IllegalArgumentException("ex id not found in exchangeNamesList with $exchName ex name")
        startActivity(exId)
    }

    override fun onClick(v: View?) {
        logD("goToExchange btn clicked")
        val exName = presenter.getSelectedExchangeItem()
        val exId = app.exchangeNamesList.values.find { it.name == exName }!!.id
        startActivity(exId)
    }



    private fun startActivity(exId: Int){
        presenter.save(SAVED_EXID to exId)
        mainActivity.startActivity(Intent(mainActivity.applicationContext, ExchangeActivity::class.java).apply{
            putExtra(EXTRA_EXCHANGE_ICO, getIcoId(exId))
            //putExtra(EXTRA_EXCHANGE_ID, exId)
        })
    }

    private fun getIcoId(exId: Int) = when(exId){
        1 -> R.drawable.binance
        2 -> R.drawable.p2pb2b
        3 -> R.drawable.coinbase
        else -> R.drawable.binance
    }


}
package ru.exrates.mobile.view.listeners

import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import ru.exrates.mobile.logic.EMPTY_CUR_ITEM
import ru.exrates.mobile.presenters.ExchangePresenter

class CurNamesSpinnerItemSelectedListener(private val presenter: ExchangePresenter) : AdapterView.OnItemSelectedListener{
    private var activated = false
    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (!activated) {
            activated = true
            return
        }
        if ((view as TextView).text == EMPTY_CUR_ITEM) return
        presenter.selectCurItem(position)
    }
}
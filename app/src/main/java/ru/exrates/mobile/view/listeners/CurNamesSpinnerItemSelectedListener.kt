package ru.exrates.mobile.view.listeners

import android.view.View
import android.widget.AdapterView
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
        presenter.selectCurItem(position)
    }
}
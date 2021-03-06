package ru.exrates.mobile.presenters

import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.view.ExratesActivity

interface Presenter {
    fun task()

    fun saveState()

    fun updateExchangeData(exchange: Exchange)

    fun updatePairData(list: MutableList<CurrencyPair>)

    fun handleError(func: (MyApp) -> Unit)

    fun start()

    fun pause()

    fun stop()

    fun resume()

    fun destroy()

    fun attachView(view: ExratesActivity)

    fun detachView()
}
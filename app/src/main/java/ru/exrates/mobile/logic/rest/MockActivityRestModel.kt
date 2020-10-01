package ru.exrates.mobile.logic.rest

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.presenters.InitPresenter
import ru.exrates.mobile.presenters.Presenter
import ru.exrates.mobile.view.ExratesActivity
import ru.exrates.mobile.view.InitialActivity

class MockActivityRestModel(app: MyApp, val activity: ExratesActivity, val presenter: Presenter): BaseActivityRestModel(app, activity, presenter) {
    override fun getActualExchange(payload: ExchangePayload, callback: ExCallback<Exchange>) {
    }

    override fun getPriceChange(exchange: Exchange) {
    }

    override fun getActualPair(c1: String, c2: String, historyinterval: String, limit: Int) {
    }

    override fun getActualPair(c1: String, c2: String, limit: Int) {
    }

    override fun addOnePair(c1: String, c2: String, exId: Int, currentInterval: String) {
    }

    override fun getLists() {
    }

    override fun ping() {
    }

    override fun getPriceHistory(
        c1: String,
        c2: String,
        exchId: Int,
        historyinterval: String,
        limit: Int
    ) {
    }

    override fun checkMessages(versionToken: String) {
        presenter as InitPresenter
        presenter.showMessage(ClientCodes.CLIENT_NOTHING to "")

    }
}
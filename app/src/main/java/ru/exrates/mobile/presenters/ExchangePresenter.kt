package ru.exrates.mobile.presenters

import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.CURRENT_INTERVAL
import ru.exrates.mobile.logic.SAVED_EXID
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.view.ExchangeActivity
import ru.exrates.mobile.view.ExratesActivity
import ru.exrates.mobile.view.viewAdapters.PairsAdapter

class ExchangePresenter(app: MyApp) : BasePresenter(app){
    private var currentInterval = "ff"
    private var exId = 1
    private lateinit var exchangeActivity: ExchangeActivity

    /*
     *************************************************************************
     * Binded methods
    ***************************************************************************/

    override fun start() {
        exId = storage.getValue(SAVED_EXID, 1)
        currentInterval = storage.getValue(CURRENT_INTERVAL, app.currentPairInfo?.find { it.exId == exId }?.historyPeriods?.get(0)
            ?: "1h")
        if (currentNameListsIsNull()) throw NullPointerException("current data is null")

        restModel.getActualExchange(ExchangePayload(exId, currentInterval, arrayOf()))
        exchangeActivity.setInterval(currentInterval)
    }


    /*
     *******************************************************************************
     * Callback methods
     *******************************************************************************/


    /*
     *******************************************************************************
     * Private methods
     *******************************************************************************/


    /*
    *******************************************************************************
    * Basic methods
    *******************************************************************************/

    override fun task() {
        if (currentDataIsNull()) throw NullPointerException("current data in task is null")
        restModel.getActualExchange(
            ExchangePayload(
                app.currentExchange!!.exId,
                currentInterval,
                app.currentExchange!!.pairs.map { it.baseCurrency + it.quoteCurrency }.toTypedArray()
            )
        )

    }

    override fun attachView(view: ExratesActivity) {
        super.attachView(view)
        exchangeActivity = activity as ExchangeActivity
    }

    override fun saveState() {
        super.saveState()
        save(CURRENT_INTERVAL to currentInterval)
    }


    /*
     ******************************************************************************
     * Public methods for activity
     *******************************************************************************/

    fun getPairsAdapt() : PairsAdapter{
        val pairsOfAdapter = if(currentDataIsNull()) mutableListOf() else
            if (app.currentExchange!!.showHidden) app.currentExchange!!.pairs else app.currentExchange!!.pairs.toMutableList() //todo base filtering on server
        pairsAdapter = PairsAdapter(
            pairsOfAdapter,
            currentInterval,
            app
        )
        return pairsAdapter
    }

    /**
     * @return text representation of interval for cur. interval view
     * */
    fun changeInterval(): String{
        val periods = app.currentExchange!!.changePeriods
        var idx = periods.indexOf(currentInterval) + 1
        if (idx >= periods.size) idx = 0
        val newInterval = periods[idx]
        currentInterval = newInterval
        pairsAdapter.currentInterval = newInterval
        pairsAdapter.notifyDataSetChanged()
        return newInterval
        /*app.currentPairInfo!![0].priceChange
            .higherKey(oldValue) ?: app.currentPairInfo!![0].priceChange.firstKey()*/

    }




}
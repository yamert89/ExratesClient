package ru.exrates.mobile.presenters

import androidx.recyclerview.widget.RecyclerView
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.CURRENT_INTERVAL
import ru.exrates.mobile.logic.SAVED_EXCHANGE_NAME_LIST
import ru.exrates.mobile.logic.SAVED_EXID
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.view.viewAdapters.PairsAdapter

class ExchangePresenter(app: MyApp) : BasePresenter(app){
    private var currentInterval = "1h"
    private var exId = 1



    /*
     *************************************************************************
     * Binded methods
    ***************************************************************************/

    override fun start() {
        exId = storage.getValue(SAVED_EXID, 1)
        if (currentNameListsIsNull()){
            app.exchangeNamesList = storage.loadObjectFromJson(SAVED_EXCHANGE_NAME_LIST) //todo ? delete
            currentInterval = storage.getValue(CURRENT_INTERVAL, "1h")
        }
        if (currentNameListsIsNull()) throw NullPointerException("current data is null")

        restModel.getActualExchange(ExchangePayload(exId, currentInterval, arrayOf()))


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
                app.currentExchange!!.pairs.filter{it.visible}.map { it.baseCurrency + it.quoteCurrency }.toTypedArray()
            )
        )

    }


    /*
     ******************************************************************************
     * Public methods for activity
     *******************************************************************************/

    fun getPairsAdapt() : PairsAdapter{
        val pairsOfAdapter = if(currentDataIsNull()) mutableListOf<CurrencyPair>() else
            if (app.currentExchange!!.showHidden) app.currentExchange!!.pairs else app.currentExchange!!.pairs.filter{it.visible}.toMutableList() //todo base filtering on server
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
    fun changeInterval(oldValue: String): String{ //todo replace oldValue with backend value
        pairsAdapter.currentInterval = oldValue
        pairsAdapter.notifyDataSetChanged()
        return app.currentPairInfo!![0].priceChange
            .higherKey(oldValue) ?: app.currentPairInfo!![0].priceChange.firstKey()

    }




}
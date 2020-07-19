package ru.exrates.mobile.presenters

import android.widget.ArrayAdapter
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.CURRENT_INTERVAL
import ru.exrates.mobile.logic.EMPTY_CUR_ITEM
import ru.exrates.mobile.logic.SAVED_EXID
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.CursPeriod
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.logic.logD
import ru.exrates.mobile.view.ExchangeActivity
import ru.exrates.mobile.view.ExratesActivity
import ru.exrates.mobile.view.viewAdapters.PairsAdapter

class ExchangePresenter(app: MyApp) : BasePresenter(app){
    private var currentInterval = "ff"
    private var exId = 1
    private lateinit var exchangeActivity: ExchangeActivity
    private lateinit var cursAdapter: ArrayAdapter<String>

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


    fun updateChangePeriod(cursPeriod: CursPeriod){
        if (cursPeriod.values.isEmpty()) return
        cursPeriod.values.forEach {
            app.currentExchange!!.pairs.find { p -> p.symbol == it.key }!!.priceChange[cursPeriod.interval] = it.value
        }
        updateExchangeData(app.currentExchange!!)
    }

    fun addPair(pair: CurrencyPair){
        app.currentExchange!!.pairs.add(pair)
        pairsAdapter.dataPairs.add(pair)
        pairsAdapter.notifyDataSetChanged()
    }

    override fun updateExchangeData(exchange: Exchange) {
        if (!super.pairsAdapterIsInitialized()) initPairsAdapt(exchange)
        super.updateExchangeData(exchange)
        if (cursAdapter.isEmpty) updateCurNames(exchange.exId)
        exchangeActivity.setSeekBarRange(exchange.changePeriods.size - 1)
    }


    /*override fun updateExchangeData(exchange: Exchange) {
        super.updateExchangeData(exchange)
        cursAdapter.clear()
        cursAdapter.addAll(exchange.pairs.map { it.symbolItem() })
        cursAdapter.notifyDataSetChanged()
    }*/


    /*
     *******************************************************************************
     * Private methods
     *******************************************************************************/

    private fun updateCurNames(exId: Int){
        cursAdapter.clear()
        cursAdapter.addAll(listOf(EMPTY_CUR_ITEM).plus(app.exchangeNamesList!!.get(exId)!!.pairs))
        cursAdapter.notifyDataSetChanged()
    }

    fun initPairsAdapt(exchange: Exchange){
        restModel.getPriceChange(exchange)
        val pairsOfAdapter = if(currentDataIsNull()) mutableListOf() else
            if (app.currentExchange!!.showHidden) app.currentExchange!!.pairs else app.currentExchange!!.pairs.toMutableList() //todo base filtering on server
        pairsAdapter = PairsAdapter(
            pairsOfAdapter,
            currentInterval,
            app
        )
        exchangeActivity.setPairsAdapter(pairsAdapter)
    }


    /*
    *******************************************************************************
    * Basic methods
    *******************************************************************************/


    override fun task() {
        if (currentDataIsNull()) throw NullPointerException("current data in task is null")
        val delimiter = app.exchangeNamesList!!.get(app.currentExchange!!.exId)!!.delimiter
        restModel.getActualExchange(
            ExchangePayload(
                app.currentExchange!!.exId,
                currentInterval,
                app.currentExchange!!.pairs.map { "${it.baseCurrency}${delimiter}${it.quoteCurrency}" }.toTypedArray()
            )
        )

    }

    override fun attachView(view: ExratesActivity) {
        super.attachView(view)
        exchangeActivity = activity as ExchangeActivity
    }

    override fun saveState() {
        super.saveState()
        savePairsAdapter()
    }


    /*
     ******************************************************************************
     * Public methods for activity
     *******************************************************************************/



    /**
     * @return text representation of interval for cur. interval view
     * */
    fun changeInterval(intervalPosition: Int): String{
        val newInterval = app.currentExchange!!.changePeriods[intervalPosition]
        currentInterval = newInterval
        pairsAdapter.currentInterval = newInterval
        pairsAdapter.notifyDataSetChanged()
        return newInterval
        /*app.currentPairInfo!![0].priceChange
            .higherKey(oldValue) ?: app.currentPairInfo!![0].priceChange.firstKey()*/
    }

    fun getCurNamesAdapter(): ArrayAdapter<String>{
        cursAdapter = ArrayAdapter<String>(exchangeActivity, android.R.layout.simple_spinner_item)
        return cursAdapter
    }


    fun selectCurItem(position: Int){

        /*val pairNames = app.exchangeNamesList!!.get(app.currentExchange!!.exId)!!.pairs.map {
            val curs = parseSymbol(it)
            "${curs.first}${app.currentExchange!!.delimiter}${curs.second}"
        }.toMutableList()*/
        logD("Selecting cur item")
        //val pairNames = app.currentExchange!!.pairs.map { it.symbol }.toMutableList()
        val curs = app.exchangeNamesList.iterator().next().value.getSplitedCurNames(cursAdapter.getItem(position)!!)
        if(pairsAdapter.dataPairs.any { it.baseCurrency == curs.first && it.quoteCurrency == curs.second }) return
        exchangeActivity.startProgress()
        //val newCur = "${curs.first}${app.currentExchange!!.delimiter}${curs.second}"
        //if (!pairNames.contains(newCur)) pairNames.add(newCur)
        restModel.addOnePair(curs.first, curs.second, app.currentExchange!!.exId, currentInterval)
        //restModel.getActualExchange(ExchangePayload(app.currentExchange!!.exId, currentInterval, pairNames.toTypedArray())) //todo replace with one pair req

    }




}
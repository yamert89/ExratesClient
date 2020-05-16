package ru.exrates.mobile.presenters

import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.processNextEventInCurrentThread
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.*
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.SelectedExchange
import ru.exrates.mobile.logic.rest.RestModel
import ru.exrates.mobile.logic.structures.IntervalComparator
import ru.exrates.mobile.view.CurrencyActivity
import ru.exrates.mobile.view.ExratesActivity
import ru.exrates.mobile.view.MainActivity
import ru.exrates.mobile.view.graph.GraphFactory
import ru.exrates.mobile.view.viewAdapters.ExchangesAdapter
import java.util.*

class CurrencyPresenter(app: MyApp) : BasePresenter(app){

    private lateinit var curActivity: CurrencyActivity
    private lateinit var exchangesAdapter: RecyclerView.Adapter<*>
    private lateinit var historyAdapter: ArrayAdapter<String>
    private var intervals: TreeSet<String> = TreeSet(IntervalComparator())
    private var currentInterval = ""
    private var currentGraphInterval = ""
    private var currentGraphIntervalIdx = 0
    private var selectedExchange = SelectedExchange(1)
    private var currName1: String = ""
    private var currName2: String = ""

    /*
    *************************************************************************
    * Binded methods
   ***************************************************************************/

    override fun start() {
        selectedExchange.id = storage.getValue(SAVED_EXID, 1)
       /* currentGraphInterval = storage.getValue(
            CURRENT_GRAPH_INTERVAL,
            app.currentPairInfo?.find { selectedExchange.id == it.exId }?.historyPeriods?.get(0) ?: "1h"
        )*/
        updateIntervals()
        val key = "$CURRENT_GRAPH_INTERVAL_IDX${selectedExchange.id}${app.currentPairInfo!![0].symbol}"
        currentGraphIntervalIdx = storage.getValue(key, 0)

        currentGraphInterval = if (this::historyAdapter.isInitialized) historyAdapter.getItem(currentGraphIntervalIdx)!! else currentInterval

        restModel.getActualPair(currName1, currName2, currentGraphInterval,
            CURRENCY_HISTORIES_CUR_NUMBER
        )
        logD(intervals.joinToString())
        if(currentNameListsIsNull()){
            currentInterval = storage.getValue(CURRENT_INTERVAL, intervals.first())
            logD("Loaded saved pair data from storage")
        }
        if (currentNameListsIsNull()) throw NullPointerException("current data is null")

        selectedExchange.listener = {
            //val historyAdapter = historyPeriodSpinner.adapter as ArrayAdapter<String>
            historyAdapter.clear()
            historyAdapter.addAll(
                app.currentPairInfo?.find { it.exId == selectedExchange.id }?.historyPeriods!!
            )
            historyAdapter.notifyDataSetChanged()
            val key1 = "$CURRENT_GRAPH_INTERVAL_IDX${selectedExchange.id}${app.currentPairInfo!![0].symbol}"
            currentGraphIntervalIdx = storage.getValue(key1, 0)
            curActivity.selectHistory(currentGraphIntervalIdx)

        }


    }

    override fun pause() {
        super.pause()

    }

    override fun destroy() {
        super.destroy()
        detachView()
    }



     /*******************************************************************************
     * Callback methods
     ******************************************************************************/



     fun updateHistory(list: List<Double>){
         curActivity.updateGraph(list, currentGraphInterval)
     }

    override fun updatePairData(list: MutableList<CurrencyPair>) {
        super.updatePairData(list)
        app.currentPairInfo = list
       // updateIntervals()
        val adapter = exchangesAdapter as ExchangesAdapter
        adapter.interval = currentInterval
        adapter.pairsByExchanges.clear()
        adapter.pairsByExchanges.addAll(list)
        adapter.notifyDataSetChanged()
        //val pair = list.find { it.exId == selectedExchange.id }
        //updateGraph(pair?.priceHistory ?: throw NullPointerException("pair not found in exId: ${selectedExchange.id}, and pairData: ${list.joinToString()}"))
        if (historyAdapter.isEmpty) {
            historyAdapter.clear()
            historyAdapter.addAll(
                list[0].historyPeriods!! //fixme
            )//todo not null?
            historyAdapter.notifyDataSetChanged()
            curActivity.selectHistory(currentGraphIntervalIdx)
        }

        val cur = list.find { it.exId == selectedExchange.id }!!
        curActivity.updateGraph(cur.priceHistory, currentGraphInterval)

    }



    /*******************************************************************************
     * Private methods
     ******************************************************************************/

    private fun updateIntervals(){
        app.currentPairInfo!!.forEach { //fixme npe with connection failed and other
            intervals.addAll(it.historyPeriods!!.subtract(intervals))
        }
        val interval = intervals.first()
        currentInterval = interval

        val pairSymbol = app.currentPairInfo!![0].symbol
        val key = "$CURRENT_GRAPH_INTERVAL_IDX${selectedExchange.id}$pairSymbol"
       // currentGraphIntervalIdx = storage.getValue(key, 0) // sync with currentGraphInterval
       // currentGraphInterval = storage.getValue(CURRENT_GRAPH_INTERVAL, "1h")
        logD("Graph interval loaded with key $key and value $currentGraphIntervalIdx")
        //curActivity.setInterval(interval)

    }





    /*
     *******************************************************************************
     * Basic methods
     *******************************************************************************/


    override fun task() {
        logD("task cur activ started with cur1: ${app.currentCur1}, cur2: ${app.currentCur2}, curGraphInterval: $currentGraphInterval")
        restModel.getActualPair(app.currentCur1, app.currentCur2, currentGraphInterval,
            CURRENCY_HISTORIES_CUR_NUMBER
        )
    }

    override fun saveState() {
        super.saveState()
        //storage.storeValue(CURRENT_GRAPH_INTERVAL, currentGraphInterval)
        save(CURRENT_GRAPH_INTERVAL_IDX to currentGraphIntervalIdx,
        CURRENT_INTERVAL to currentInterval)

    }

    override fun attachView(view: ExratesActivity) {
        super.attachView(view)
        curActivity = activity as CurrencyActivity
    }

    override fun detachView() {
        activity = null
    }


     /******************************************************************************
     * Public methods for activity
     ******************************************************************************/

    fun getExchAdapter(): ExchangesAdapter{
         exchangesAdapter = ExchangesAdapter(
             app.currentPairInfo ?: mutableListOf(),
             restModel,
             app,
             app.currentPairInfo?.get(0)!!.historyPeriods?.get(0)!!,
             selectedExchange
         )
         return exchangesAdapter as ExchangesAdapter
     }

    fun getHistorySpinnerAdapter() : ArrayAdapter<String>{
        historyAdapter = ArrayAdapter<String>(app.baseContext, android.R.layout.simple_spinner_item)
        return historyAdapter
    }

    fun selectHistoryInterval(position: Int){
        val interval = historyAdapter.getItem(position)
        currentGraphInterval = interval ?: throw IllegalArgumentException("interval $interval not found")
        currentGraphIntervalIdx = position
        restModel.getPriceHistory(currName1, currName2, selectedExchange.id, interval,
            CURRENCY_HISTORIES_CUR_NUMBER
        )
        val key = "$CURRENT_GRAPH_INTERVAL_IDX${selectedExchange.id}${app.currentPairInfo!![0].symbol}"
        storage.storeValue(key, position )
        logD("Graph interval saved with key $key and value $position")
    }

    fun setCurNames(cur1: String, cur2: String){
        currName1 = cur1
        currName2 = cur2
    }

    fun clickOnInterval(): String{
        val interval = if(currentDataIsNull()) app.currentPairInfo?.get(0)!!.historyPeriods?.get(0)!! else
            intervals.higher(currentInterval)
                ?: app.currentPairInfo!![0].priceChange.firstKey()

        currentInterval = interval
        val adapter = exchangesAdapter as ExchangesAdapter
        adapter.interval = currentInterval
        adapter.notifyDataSetChanged()
        return interval
    }




}
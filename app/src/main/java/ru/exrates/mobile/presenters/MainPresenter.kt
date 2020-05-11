package ru.exrates.mobile.presenters

import android.widget.ArrayAdapter
import kotlinx.coroutines.*
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.*
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangeNamesObject
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.logic.rest.RestModel
import ru.exrates.mobile.view.ExratesActivity
import ru.exrates.mobile.view.MainActivity
import ru.exrates.mobile.view.viewAdapters.ExchangesAdapter
import ru.exrates.mobile.view.viewAdapters.PairsAdapter
import java.io.FileNotFoundException
import java.io.InvalidClassException

class MainPresenter (app: MyApp) : BasePresenter(app){
    private lateinit var searchAdapter: ArrayAdapter<String>
    private lateinit var curAdapter: ArrayAdapter<String>
    private lateinit var exchAdapter: ArrayAdapter<String>

    //private var activity: ExratesActivity? = null
    private lateinit var mainActivity: MainActivity

    private var curIdx = 0
    private var exIdx = 0





    /*
     *************************************************************************
     * Binded methods
    ***************************************************************************/

    override fun start() {

    }

    override fun pause() {
        saveState()
    }

    override fun resume() {
        var exId = 0
        try {

            var flag = true
            logTrace("main onresume")
            val listsReq = GlobalScope.launch(Dispatchers.IO) {
                logTrace("start coroutine")
                if (storage.getValue(IS_FIRST_LOAD, true)) {
                    logTrace("before first load")
                    flag = firstLoad()
                    logTrace("flaq is $flag")
                }

                else {
                    logTrace("Saved lists loaded")
                    try{
                        if (app.exchangeNamesList == null) app.exchangeNamesList = storage.loadObjectFromJson(
                            SAVED_EXCHANGE_NAME_LIST, ArrayList<ExchangeNamesObject>())

                    }catch (e: FileNotFoundException){
                        flag = false
                        activity?.stopProgress()
                        storage.storeValue(IS_FIRST_LOAD, true)
                        return@launch
                    }catch (e: InvalidClassException){
                        logE("Class model of ex names list deprecated")
                        flag = firstLoad()
                    }catch (e: Exception){
                        logE(e.message.toString())
                    }
                    curIdx = storage.getValue(SAVED_CUR_IDX, 0)
                    exIdx = storage.getValue(SAVED_EX_IDX, 0)
                    val mockPair = "AGI/BTC"
                    val curs =  parseSymbol(if (!curAdapter.isEmpty) curAdapter.getItem(0) ?: mockPair else mockPair)
                    val cur1 = storage.getValue(CURRENT_CUR_1, curs.first)
                    val cur2 = storage.getValue(CURRENT_CUR_2, curs.second)
                    exId = storage.getValue(SAVED_EXID, 1)
                    val pairs = storage.getValue(SAVED_CURRENCIES_NAMES, arrayOf(mockPair))
                    app.currentCur1 = cur1
                    app.currentCur2 = cur2

                    restModel.getActualExchange(
                        ExchangePayload(
                        exId,
                        app.currentInterval,
                        app.currentExchange?.pairs?.map { it.baseCurrency + it.quoteCurrency }?.toTypedArray()?.plus(
                            arrayOf(app.currentCur1 + app.currentCur2)) ?: pairs)
                    )
                    restModel.getActualPair(cur1, cur2, "1h",
                        CURRENCY_HISTORIES_MAIN_NUMBER
                    )
                }
            }

            runBlocking { listsReq.join() }
            if (!flag) {
                activity?.toast("Не удалось подключиться к серверу. Проверте интернет подключение и перезапустите приложение")
                return
            }else restModel.ping()
            //exchangeName.setSelection((exchangeName.adapter as ArrayAdapter<String>).getPosition(app.exchangeNamesList?.find { it.id == exId }?.name))

            GlobalScope.launch(Dispatchers.Main) {
                if (app.exchangeNamesList == null || pairsAdapter.itemCount == 0) {
                    logD("exchange names list is null or currency name adapter is empty")
                    return@launch
                }

                updateExchangesList(app.exchangeNamesList!!.map { it.name })
                val allPairs = getListWithAllPairs(app.exchangeNamesList!!)
                updateCurrenciesList(allPairs)
                if (!this@MainPresenter::searchAdapter.isInitialized) searchAdapter = ArrayAdapter<String>(app.baseContext, android.R.layout.simple_dropdown_item_1line )
                searchAdapter.addAll(allPairs)
            }
            //mainActivity.updateCurrencyPrice(cur?.price?.toNumeric() ?: "0.0")

        }catch (e: Exception){e.printStackTrace()}
    }

    /*
     *******************************************************************************
     * Callback methods
     *******************************************************************************/

    fun initData(exchangeNamesList: List<ExchangeNamesObject>){
        logTrace("init data")
        try {
            app.exchangeNamesList = exchangeNamesList
            GlobalScope.launch {
               save(SAVED_EXCHANGE_NAME_LIST to exchangeNamesList)
                logTrace("list saved")

            }
            logTrace("get exchange")


            val allPairs = getListWithAllPairs(exchangeNamesList).sorted()
            val defExId = exchangeNamesList.find { it.pairs.contains(allPairs[0]) }!!.id
            restModel.getActualExchange(ExchangePayload(defExId, app.currentInterval, emptyArray()))
            val curs = parseSymbol(allPairs[0])
            restModel.getActualPair(
                curs.first, curs.second,
                CURRENCY_HISTORIES_MAIN_NUMBER
            )
            updateExchangesList(exchangeNamesList.map { it.name })

            updateCurrenciesList(allPairs)

            if (!this::searchAdapter.isInitialized) searchAdapter = ArrayAdapter<String>(app.baseContext, android.R.layout.simple_dropdown_item_1line )

            searchAdapter.addAll(allPairs)
            rebuildExAdapter(defExId)

        }catch (e: Exception){
            logE("exception in init method")
            e.printStackTrace()
        }

    }

    override fun updatePairData(list: MutableList<CurrencyPair>) {

        logD("updatePairData")
        if (list.isEmpty()){
            logE("Incoming list of pairData is empty")
            return
        }
        logD(list.joinToString { "${it.symbol} | ${it.exchangeName}" })
        app.currentCur1 = list[0].baseCurrency
        app.currentCur2 = list[0].quoteCurrency
        app.currentPairInfo = list
        var count = 0.0
        list.forEach { count += it.price }
        mainActivity.setCurrencyPrice((count / list.size).toNumeric())
        val cur = list.find { it.exId == storage.getValue(SAVED_EXID, list[0].exId)}!!
        logTrace("current currency in graph: $cur")
        //val(xLabel, dataList) = createChartValueDataList(cur.priceHistory)
        logTrace("priceHistory:" + cur.priceHistory.joinToString())
        logTrace(
            "priceHistory truncated:" + cur.priceHistory.subList(
                cur.priceHistory.size - 10,
                cur.priceHistory.lastIndex + 1
            ).joinToString()
        )
        mainActivity.updateGraph(cur)
    }

    /*
     *******************************************************************************
     * Private methods
     *******************************************************************************/
    private suspend fun firstLoad(): Boolean{
        var res = false
        mainActivity.startProgress()
        coroutineScope {
            try {
                logTrace("before request")
                restModel.getLists()
            }catch (e: Exception){
                logE("exception")
                return@coroutineScope
            }
            res = true

        }
        if (res) storage.storeValue(IS_FIRST_LOAD, false)
        return res
    }

    private fun updateExchangesList(exchangeNames: List<String>?){
        if (exchangeNames == null) return
        if (!this::exchAdapter.isInitialized) exchAdapter = ArrayAdapter<String>(app.baseContext, android.R.layout.simple_spinner_dropdown_item)
        logD("exchanges: $exchangeNames")
        with(exchAdapter){clear(); addAll(exchangeNames); notifyDataSetChanged()}

        //exchangeName.setSelection(storage.getValue(SAVED_EX_IDX, 0))

    }

    private fun updateCurrenciesList(curNames: List<String>?){
        if (curNames == null) return
        if(!this::curAdapter.isInitialized) curAdapter = ArrayAdapter<String>(app.baseContext, android.R.layout.simple_spinner_dropdown_item)
        logD("curNames : $curNames")
        with(curAdapter){clear(); addAll(curNames); notifyDataSetChanged()}
        mainActivity.selectPairItem(curIdx)

    }

    private fun getListWithAllPairs(exchangeNamesList: List<ExchangeNamesObject>): List<String>{
        val allPairs = ArrayList<String>(2000)
        exchangeNamesList.forEach { allPairs.addAll(it.pairs.subtract(allPairs)) }
        return allPairs.sorted()
    }

    private fun rebuildExAdapter(exId : Int){
        logD("rebuildExSpinner")
        val pos = exchAdapter.getPosition(app.exchangeNamesList?.find { it.id == exId }?.name)
        val ex = exchAdapter.getItem(pos)
        exchAdapter.remove(ex)
        exchAdapter.insert(ex, 0)
        mainActivity.selectExchangeItem(0)
    }

    /*
     *******************************************************************************
     * Basic methods
     *******************************************************************************/

    override fun task() {
        if (currentDataIsNull()){
            logTrace("current data  is null")
            return
        }
        logTrace("pairs: " + app.currentExchange!!.pairs.filter { it.visible }
            .map { it.symbol }.toTypedArray().joinToString())
        restModel.getActualExchange(ExchangePayload(
            app.currentExchange!!.exId,
            app.currentInterval,
            app.currentExchange!!.pairs.filter{it.visible}.map { it.baseCurrency + it.quoteCurrency }.toTypedArray().plus(arrayOf(app.currentCur1 + app.currentCur2))
        ))
        restModel.getActualPair(app.currentPairInfo!![0].baseCurrency , app.currentPairInfo!![0].quoteCurrency, "1h",
            CURRENCY_HISTORIES_MAIN_NUMBER
        )
    }

    override fun saveState() {
        val adapterName = when(app.currentExchange?.exId){
            1 -> SAVED_CURRENCIES_ADAPTER_BINANCE
            else -> SAVED_CURRENCIES_ADAPTER_P2PB2B
        }
        /*val adapterValues = mutableListOf<String>()
        for (i: Int in 0 until curAdapter.count){
            adapterValues.add(curAdapter.getItem(i)!!)
        }*/
        save(
            SAVED_EX_IDX to exIdx,
            SAVED_CUR_IDX to curIdx,
            SAVED_CURRENCIES_ADAPTER to adapterName,
            adapterName to pairsAdapter,
            SAVED_CURRENCIES_NAMES to (app.currentExchange?.pairs?.map { it.symbol }?.toTypedArray() ?: arrayOf("AGI/BTC"))/*,
            SAVED_EXID to (app.currentExchange?.exId ?: 1)*/)
    }

    override fun attachView(view: ExratesActivity) {
        super.attachView(view)
        mainActivity = activity as MainActivity
    }

    override fun detachView() {
        activity = null
    }

    /*
     ******************************************************************************
     * Public methods for activity
     *******************************************************************************/

    fun getCurrencyAdapter(): PairsAdapter{
        val savedAdapterName = storage.getValue(
            SAVED_CURRENCIES_ADAPTER,
            SAVED_CURRENCIES_ADAPTER_BINANCE
        )
        //val values = storage.getValue(savedAdapterName, mutableListOf<String>())
        pairsAdapter = storage.getValue(savedAdapterName, PairsAdapter(
            mutableListOf(),
            app = app
        ) )
        pairsAdapter.app = app
        return pairsAdapter
    }

    fun getExSpinnerAdapter(): ArrayAdapter<String>{
        if (!this::exchAdapter.isInitialized) exchAdapter = ArrayAdapter<String>(mainActivity, android.R.layout.simple_spinner_item)
        return exchAdapter
    }

    fun getCurSpinnerAdapter(): ArrayAdapter<String>{
        if (!this::curAdapter.isInitialized) curAdapter = ArrayAdapter<String>(mainActivity, android.R.layout.simple_spinner_item)
        return curAdapter
    }

    fun getSearchAdapter(): ArrayAdapter<String>{
        if (!this::searchAdapter.isInitialized) searchAdapter = ArrayAdapter<String>(mainActivity, android.R.layout.simple_spinner_item )
        return searchAdapter
    }

    fun getCurIdx() = curIdx

    fun updateExIdx(idx: Int){
        exIdx = idx
    }

    fun updateCurIdx(idx: Int){
        curIdx = idx
    }

    /**
     * @return
     * - base currency name
     * - quote currency name
     * */

    fun prepareStartCurActivity(): Pair<String, String>{
        val symbol = curAdapter.getItem(curIdx).toString()
        val curs = parseSymbol(symbol)
        app.currentCur1 = curs.first
        app.currentCur2 = curs.second
        save(CURRENT_CUR_1 to curs.first, CURRENT_CUR_2 to curs.second)
        val defExId = if (app.exchangeNamesList!![0].pairs.contains(symbol)) 1 else {
            app.exchangeNamesList!!.find { it.pairs.contains(symbol) }!!.id
        }
        rebuildExAdapter(defExId)

        save(SAVED_EXID to defExId/*, SAVED_EX_IDX to pos*/)
        return Pair(curs.first, curs.second)

    }












}

package ru.exrates.mobile.presenters

import android.widget.ArrayAdapter
import kotlinx.coroutines.*
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.*
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangeNamesObject
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.view.ExratesActivity
import ru.exrates.mobile.view.MainActivity
import ru.exrates.mobile.view.viewAdapters.PairsAdapter
import java.io.FileNotFoundException
import java.io.InvalidClassException

class MainPresenter (private val basic: BasePresenter) : Presenter by basic{
    private lateinit var pairsAdapter: PairsAdapter
    private lateinit var searchAdapter: ArrayAdapter<String>
    private lateinit var curAdapter: ArrayAdapter<String>
    private lateinit var exchAdapter: ArrayAdapter<String>
    private val storage = basic.storage
    private val app = basic.app
    private val restModel = basic.restModel
    private var activity: ExratesActivity = object : ExratesActivity(){}
    private val mainActivity = activity as MainActivity

    override fun attachView(view: ExratesActivity) {
        activity = view
    }

    override fun start() {

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
                        activity.stopProgress()
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
                    val cur1 = storage.getValue(CURRENT_CUR_1, "AGIBTC")
                    val cur2 = storage.getValue(CURRENT_CUR_2, "AGIBTC")
                    exId = storage.getValue(SAVED_EXID, 1)
                    val pairs = storage.getValue(SAVED_CURRENCIES_NAMES, arrayOf("AGIBTC")) //todo hardcode
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
                activity.toast("Не удалось подключиться к серверу. Проверте интернет подключение и перезапустите приложение")

                return
            }else restModel.ping()
            //exchangeName.setSelection((exchangeName.adapter as ArrayAdapter<String>).getPosition(app.exchangeNamesList?.find { it.id == exId }?.name))


            GlobalScope.launch(Dispatchers.Main) {
                if (app.exchangeNamesList == null || pairsAdapter.itemCount != 0) {
                    logD("exchange names list is null or currency name adapter is empty")
                    return@launch
                }


                updateExchangesList(app.exchangeNamesList!!.map { it.name })
                val allPairs = getListWithAllPairs(app.exchangeNamesList!!)
                updateCurrenciesList(allPairs)
                searchAdapter.addAll(allPairs)
            }

            //mainActivity.updateCurrencyPrice(cur?.price?.toNumeric() ?: "0.0")



        }catch (e: Exception){e.printStackTrace()}
    }

    override fun task() {
        if (basic.currentDataIsNull()){
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

    fun initData(exchangeNamesList: List<ExchangeNamesObject>){
        logTrace("init data")
        try {
            app.exchangeNamesList = exchangeNamesList
            GlobalScope.launch {
                basic.save(SAVED_EXCHANGE_NAME_LIST to exchangeNamesList)
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

            searchAdapter.addAll(allPairs)
            rebuildExSpinner(defExId)

        }catch (e: Exception){
            logE("exception in init method")
            e.printStackTrace()
        }

    }

    fun getCurrencyAdapter(): PairsAdapter{
        val savedAdapter = storage.getValue(
            SAVED_CURRENCIES_ADAPTER,
            SAVED_CURRENCIES_ADAPTER_BINANCE
        )
        pairsAdapter = storage.getValue(savedAdapter,
            PairsAdapter(
                mutableListOf(),
                app = app
            )
        )
        pairsAdapter.app = app
        return pairsAdapter
    }

    override fun saveState() {
        val adapterName = when(app.currentExchange?.exId){
            1 -> SAVED_CURRENCIES_ADAPTER_BINANCE
            else -> SAVED_CURRENCIES_ADAPTER_P2PB2B
        }
        basic.save(
            SAVED_EX_IDX to exchangeName.selectedItemPosition,
            SAVED_CUR_IDX to currencyName.selectedItemPosition,
            SAVED_CURRENCIES_ADAPTER to adapterName,
            adapterName to currenciesRecyclerView.adapter!!,
            SAVED_CURRENCIES_NAMES to (app.currentExchange?.pairs?.map { it.symbol }?.toTypedArray() ?: arrayOf("ETCBTC"))/*, //todo hardcode
            SAVED_EXID to (app.currentExchange?.exId ?: 1)*/)
    }

    override fun updateExchangeData(exchange: Exchange) {
        app.currentExchange = exchange
        logD("incoming pairs: ${exchange.pairs.joinToString { it.symbol }}")
        val adapter = currenciesRecyclerView.adapter as PairsAdapter
        adapter.dataPairs.clear()
        adapter.dataPairs.addAll(exchange.pairs)
        adapter.notifyDataSetChanged()
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
        super.updatePairData(list)
        app.currentPairInfo = list
        var count = 0.0
        list.forEach { count += it.price }
        currencyPrice.text = (count / list.size).toNumeric()
        val cur = list.find { it.exId == storage.getValue(SAVED_EXID, list[0].exId)}!! //todo right? app.currentExchange outdated. how to choose right pair?
        logTrace("current currency in graph: $cur")
        //val(xLabel, dataList) = createChartValueDataList(cur.priceHistory)
        logTrace("priceHistory:" + cur.priceHistory.joinToString())
        logTrace(
            "priceHistory truncated:" + cur.priceHistory.subList(
                cur.priceHistory.size - 10,
                cur.priceHistory.lastIndex + 1
            ).joinToString()
        )
    }

    private fun updateExchangesList(exchangeNames: List<String>?){
        if (exchangeNames == null) return
        logD("exchanges: $exchangeNames")
        with(exchAdapter){clear(); addAll(exchangeNames); notifyDataSetChanged()}

        //exchangeName.setSelection(storage.getValue(SAVED_EX_IDX, 0))

    }

    private fun rebuildExSpinner(exId : Int){
        logD("rebuildExSpinner")
        val adapter = (exchangeName.adapter as ArrayAdapter<String>)
        val pos = adapter.getPosition(app.exchangeNamesList?.find { it.id == exId }?.name)
        val ex = adapter.getItem(pos)
        adapter.remove(ex)
        adapter.insert(ex, 0)
        exchangeName.setSelection(0)
    }

    private fun parseSymbol(symbol: String): Pair<String, String>{
        val arr = symbol.split("/")
        return arr[0] to arr[1]
    }

    private fun updateCurrenciesList(curNames: List<String>?){
        if (curNames == null) return
        logD("curNames : $curNames")
        with(curAdapter){clear(); addAll(curNames); notifyDataSetChanged()}
        //currencyName.setSelection(curIdx)
    }

    private fun getListWithAllPairs(exchangeNamesList: List<ExchangeNamesObject>): List<String>{
        val allPairs = ArrayList<String>(2000)
        exchangeNamesList.forEach { allPairs.addAll(it.pairs.subtract(allPairs)) }
        return allPairs.sorted()
    }


}

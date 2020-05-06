package ru.exrates.mobile.presenters

import android.widget.ArrayAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.*
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
                if (app.exchangeNamesList == null || !currencyName.adapter.isEmpty) {
                    logD("exchange names list is null or currency name adapter is empty")
                    return@launch
                }


                updateExchangesList(app.exchangeNamesList!!.map { it.name })
                val allPairs = getListWithAllPairs(app.exchangeNamesList!!)
               updateCurrenciesList(allPairs)
                val adapter = autoCompleteTextView.adapter as ArrayAdapter<String>
                adapter.addAll(allPairs)
            }


            currencyPrice.text = cur?.price?.toNumeric() ?: "0.0"


        }catch (e: Exception){e.printStackTrace()}
    }

    private fun firstLoad(): Boolean{

    }

    private fun updateExchangesList(exchangeNames: List<String>?){
        if (exchangeNames == null) return
        logD("exchanges: $exchangeNames")
        with(exchAdapter){clear(); addAll(exchangeNames); notifyDataSetChanged()}

        //exchangeName.setSelection(storage.getValue(SAVED_EX_IDX, 0))

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

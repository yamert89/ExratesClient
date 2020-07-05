package ru.exrates.mobile.view.prefs

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.preference.DialogPreference
import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragmentCompat
import org.florescu.android.rangeseekbar.RangeSeekBar
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.R
import ru.exrates.mobile.logic.*
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.json.ExchangeNamesObject
import ru.exrates.mobile.logic.rest.ServiceModel
import ru.exrates.mobile.services.MainService
import java.math.BigDecimal

class NotificationPreferenceDialogFragment(private val app: MyApp): PreferenceDialogFragmentCompat(),
    DialogPreference.TargetFragment,
    ServiceCallbackReceiver {
    private lateinit var prefSeekBar: RangeSeekBar<Float>
    private lateinit var exchName: Spinner
    private lateinit var curSymbol: Spinner
    private lateinit var minValueT: TextView
    private lateinit var maxValueT: TextView
    private lateinit var notifPreference: NotificationPreference
    private val restModel =
        ServiceModel(app.restService, this)
    private var activateListeners = true

    companion object A{
        fun newInstance(app: MyApp, key: String): NotificationPreferenceDialogFragment {
            return NotificationPreferenceDialogFragment(
                app
            ).apply {
                arguments = Bundle(1).apply {
                    putString(ARG_KEY, key)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        prefSeekBar = view.findViewById(R.id.pref_seekBar)
        exchName = view.findViewById(R.id.pref_exch)
        curSymbol = view.findViewById(R.id.pref_cur)
        minValueT = view.findViewById(R.id.pref_minValue)
        maxValueT = view.findViewById(R.id.pref_maxValue)

        notifPreference = preference as NotificationPreference

        prefSeekBar.setRangeValues(notifPreference.min, notifPreference.max)

        exchName.adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item
        ).apply {
            addAll(app.exchangeNamesList.values.map(ExchangeNamesObject::name))
        }
        var exOb: ExchangeNamesObject
        curSymbol.adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item
        ).apply {
            exOb = app.exchangeNamesList.values.find { it.id == notifPreference.exId }!!
            addAll(exOb.pairs)
        }

        var ad = exchName.adapter as ArrayAdapter<String>
        exchName.setSelection(ad.getPosition(exOb.name))
        ad = curSymbol.adapter as ArrayAdapter<String>
        if (notifPreference.symbol.isEmpty()){
            curSymbol.setSelection(0)
            notifPreference.symbol = curSymbol.selectedItem as String
        } else curSymbol.setSelection(ad.getPosition(notifPreference.symbol))

        exchName.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val exObj = getActualExchangeNamesObject()
                notifPreference.exId = exObj.id
                val curAdapter = curSymbol.adapter as ArrayAdapter<String>
               curAdapter.clear()
                curAdapter.addAll(exObj.pairs)
                curAdapter.notifyDataSetChanged()
                curSymbol.setSelection(if(curSymbol.selectedItemPosition == 0) 1 else 0)
                logD("ex name item selected")
            }

        }

        curSymbol.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                logD("nothing selected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                notifPreference.symbol = (view as TextView).text.toString()
                val curs = getActualExchangeNamesObject().getSplitedCurNames(notifPreference.symbol)
                restModel.onePair(curs.first, curs.second, notifPreference.exId)
                logD("cur name item selected")
            }
        }

        prefSeekBar.setOnRangeSeekBarChangeListener { bar, minValue, maxValue ->
            logD("on range listener minvalue: $minValue, max: $maxValue, ${bar.selectedMinValue}, ${bar.selectedMaxValue}")
            minValueT.text = minValue.toNumeric()
            maxValueT.text = maxValue.toNumeric()
        }

        minValueT.addTextChangedListener {
            text: Editable? ->
            run {
                if (!activateListeners) return@run
                val maxText = maxValueT.text.toString()
                if (maxText.isEmpty()) return@run
                val value = text.toString().toSafetyFloat()
                val max = maxText.toSafetyFloat()
                when{
                    value > max || value > prefSeekBar.absoluteMaxValue -> {
                        prefSeekBar.selectedMinValue = max
                        minValueT.text = max.toNumeric()
                    }
                    value < prefSeekBar.absoluteMinValue -> minValueT.text = prefSeekBar.absoluteMinValue.toNumeric()
                    else -> prefSeekBar.selectedMinValue = value
                }

            }

        }
        maxValueT.addTextChangedListener {
                text: Editable? ->
            run {
                if (!activateListeners) return@run
                val minText = minValueT.text.toString()
                if( minText.isEmpty()) return@run
                val value = text.toString().toSafetyFloat()
                val min = minText.toSafetyFloat()
                when{
                    value < min || value < prefSeekBar.absoluteMinValue -> {
                        prefSeekBar.selectedMaxValue = min
                        maxValueT.text = min.toNumeric()
                    }
                    value > prefSeekBar.absoluteMaxValue -> maxValueT.text = prefSeekBar.absoluteMaxValue.toNumeric()
                    else -> prefSeekBar.selectedMaxValue = value
                }

            }
        }

    }


    override fun onDialogClosed(positiveResult: Boolean) {
        if (!positiveResult) return
        val exObject: ExchangeNamesObject
        with(notifPreference){
            min = prefSeekBar.selectedMinValue
            max = prefSeekBar.selectedMaxValue
            symbol = curSymbol.selectedItem as String
            exObject = app.exchangeNamesList.values.find { it.name == exchName.selectedItem as String }!!
            exId = exObject.id
        }
        app.stopService(
            Intent(
                app.applicationContext,
                MainService::class.java
            )
        )
        val curNames = exObject.getSplitedCurNames(notifPreference.symbol)
        app.startForegroundService(
            Intent(
                app.applicationContext,
                MainService::class.java
            ).apply {
            putExtra(EXTRA_CURRENCY_NAME_1, curNames.first)
            putExtra(EXTRA_CURRENCY_NAME_2, curNames.second)
            putExtra(EXTRA_MAX_LIMIT, notifPreference.max)
            putExtra(EXTRA_MIN_LIMIT, notifPreference.min)
            putExtra(EXTRA_PERIOD, 60000L)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    /**
     * Callback method for update price range
     * */
    fun updateRange(pair: CurrencyPair){
        val min = BigDecimal(pair.price * 0.01).toFloat()
        val max = BigDecimal(pair.price * 100).toFloat()
        val step = BigDecimal(max.toDouble() / 100).toFloat()
        prefSeekBar.setRangeValues(min, max, step)
        prefSeekBar.selectedMaxValue = max
        prefSeekBar.selectedMinValue = min
        activateListeners = false
        minValueT.text = min.toNumeric()
        maxValueT.text = max.toNumeric()
        activateListeners = true
        logD("update range with pair price: ${pair.price}, min: ${min}, max: ${max}")
    }


    override fun <T : Preference?> findPreference(key: CharSequence): T? {
        return null
    }

    private fun getActualExchangeNamesObject(): ExchangeNamesObject {
        return app.exchangeNamesList.values.find { it.name == exchName.selectedItem as String }!!
    }
}
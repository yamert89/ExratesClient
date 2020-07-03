package ru.exrates.mobile.view.prefs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.AttributeSet
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
import java.math.MathContext
import java.text.DecimalFormat

class NotificationPreference(context: Context, attributeSet: AttributeSet): DialogPreference(context, attributeSet) {
    var min = 0.0f
        set(value){
            field = value
            with(sharedPreferences.edit()){
                putFloat(PREF_NOTIFICATION_MIN, value)
                apply()
            }
        }
    var max = 0.0f
        set(value){
            field = value
            with(sharedPreferences.edit()){
                putFloat(PREF_NOTIFICATION_MAX, value)
                apply()
            }
        }
    var exId = 1
        set(value){
            field = value
            with(sharedPreferences.edit()){
                putInt(PREF_NOTIFICATION_EX, value)
                apply()
            }
        }
    var symbol = ""
        set(value){
            field = value
            with(sharedPreferences.edit()){
                putString(PREF_NOTIFICATION_CUR, value)
                apply()
            }
        }

    override fun onSetInitialValue(defaultValue: Any?) {
        min = sharedPreferences.getFloat(PREF_NOTIFICATION_MIN, 0.0f)
        max = sharedPreferences.getFloat(PREF_NOTIFICATION_MAX, 100f)
        exId = sharedPreferences.getInt(PREF_NOTIFICATION_EX, 1)
        symbol = sharedPreferences.getString(PREF_NOTIFICATION_CUR, "3")!!
    }

    override fun getDialogLayoutResource(): Int {
        return R.layout.notification_preference
    }
}

class NotificationPreferenceDialogFragment(private val app: MyApp): PreferenceDialogFragmentCompat(), DialogPreference.TargetFragment, ServiceCallbackReceiver{
    private lateinit var prefSeekBar: RangeSeekBar<Float>
    private lateinit var exchName: Spinner
    private lateinit var curSymbol: Spinner
    private lateinit var minValueT: TextView
    private lateinit var maxValueT: TextView
    private lateinit var notifPreference: NotificationPreference
    private val restModel = ServiceModel(app.restService, this)

    companion object A{
        fun newInstance(app: MyApp, key: String): NotificationPreferenceDialogFragment{
            return NotificationPreferenceDialogFragment(app).apply {
                arguments = Bundle(1).apply {
                    putString(ARG_KEY, key)
                }
            }
        }
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        prefSeekBar = view.findViewById(R.id.pref_seekBar)
        exchName = view.findViewById(R.id.pref_exch)
        curSymbol = view.findViewById(R.id.pref_cur)
        minValueT = view.findViewById(R.id.pref_minValue)
        maxValueT = view.findViewById(R.id.pref_maxValue)

        notifPreference = preference as NotificationPreference

        prefSeekBar.setRangeValues(notifPreference.min, notifPreference.max)

        exchName.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item).apply {
            addAll(app.exchangeNamesList.values.map(ExchangeNamesObject::name))
        }
        var exOb: ExchangeNamesObject
        curSymbol.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item).apply {
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


        curSymbol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?,  position: Int, id: Long) {
                notifPreference.symbol = (view as TextView).text.toString()
                val curs = exOb.getSplitedCurNames(notifPreference.symbol)
                restModel.onePair(curs.first, curs.second, notifPreference.exId)
            }
        }

        prefSeekBar.setOnRangeSeekBarChangeListener { bar, minValue, maxValue ->
            logD("on range listener minvalue: $minValue, max: $maxValue, ${bar.selectedMinValue}, ${bar.selectedMaxValue}")
            minValueT.text = minValue.toDouble().toNumeric()
            maxValueT.text = maxValue.toDouble().toNumeric()
        }

        minValueT.addTextChangedListener {
            text: Editable? -> prefSeekBar.selectedMinValue = text.toString().toFloat()

        }
        maxValueT.addTextChangedListener {
                text: Editable? -> prefSeekBar.selectedMaxValue = text.toString().toFloat()
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
        app.stopService(Intent(app.applicationContext, MainService::class.java))
        val curNames = exObject.getSplitedCurNames(notifPreference.symbol)
        app.startForegroundService(Intent(app.applicationContext, MainService::class.java).apply {
            putExtra(EXTRA_CURRENCY_NAME_1, curNames.first)
            putExtra(EXTRA_CURRENCY_NAME_2, curNames.second)
            putExtra(EXTRA_MAX_LIMIT, notifPreference.max)
            putExtra(EXTRA_MIN_LIMIT, notifPreference.min)
            putExtra(EXTRA_PERIOD, 15000L)
            addFlags( Intent.FLAG_ACTIVITY_NEW_TASK )
        })
    }

    fun updateRange(pair: CurrencyPair){
        logD("update range with pair price: ${pair.price}, min: ${pair.price * 0.2}, max: ${pair.price * 20}")
        val min = BigDecimal(pair.price * 0.01)
        val max = BigDecimal(pair.price * 100)
        minValueT.text = min.toDouble().toNumeric()
        maxValueT.text = max.toDouble().toNumeric()
        val step = BigDecimal(max.toDouble()/100)
        prefSeekBar.setRangeValues(min.toFloat(), max.toFloat(), step.toFloat())
    }


    override fun <T : Preference?> findPreference(key: CharSequence): T? {
        return null
    }
}
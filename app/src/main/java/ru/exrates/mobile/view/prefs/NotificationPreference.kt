package ru.exrates.mobile.view.prefs

import android.content.Context
import android.content.res.TypedArray
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.preference.DialogPreference
import androidx.preference.PreferenceDialogFragmentCompat
import kotlinx.android.synthetic.main.notification_preference.view.*
import org.florescu.android.rangeseekbar.RangeSeekBar
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.R
import ru.exrates.mobile.data.Storage
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangeNamesObject

class NotificationPreference(context: Context): DialogPreference(context) {
    var min = 0.0f
        set(value){
            field = value
            persistFloat(value)
        }
    var max = 0.0f
        set(value){
            field = value
            persistFloat(value)
        }
    var exId = 0
        set(value){
            field = value
            persistInt(value)
        }
    var symbol = ""
        set(value){
            field = value
            persistString(value)
        }

    override fun onSetInitialValue(defaultValue: Any?) {
        min = getPersistedFloat(0.0f)
        max = getPersistedFloat(100f)
        exId = getPersistedInt(0)
        symbol = getPersistedString("")
    }

    override fun getDialogLayoutResource(): Int {
        return R.layout.notification_preference
    }
}

class NotificationPreferenceDialogFragment(private val app: MyApp): PreferenceDialogFragmentCompat(){
    private lateinit var prefSeekBar: RangeSeekBar<Float>
    private lateinit var exchName: Spinner
    private lateinit var curSymbol: Spinner
    private val notifPreference = preference as NotificationPreference

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        prefSeekBar = view.findViewById(R.id.pref_seekBar)
        exchName = view.findViewById(R.id.pref_exch)
        curSymbol = view.findViewById(R.id.pref_cur)



        prefSeekBar.setRangeValues(notifPreference.min, notifPreference.max)

        exchName.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item).apply {
            addAll(app.exchangeNamesList!!.map(ExchangeNamesObject::name))
        }
        var exOb: ExchangeNamesObject
        curSymbol.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item).apply {
            exOb = app.exchangeNamesList!!.find { it.id == notifPreference.exId }!!
            addAll(exOb.pairs)
        }

        var ad = exchName.adapter as ArrayAdapter<String>
        exchName.setSelection(ad.getPosition(exOb.name))
        ad = curSymbol.adapter as ArrayAdapter<String>
        curSymbol.setSelection(ad.getPosition(notifPreference.symbol))

    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (!positiveResult) return
        with(notifPreference){
            min = prefSeekBar.selectedMinValue
            max = prefSeekBar.selectedMaxValue
            symbol = curSymbol.selectedItem as String
            exId = app.exchangeNamesList!!.find { it.name == exchName.selectedItem as String }!!.id
        }
    }




}
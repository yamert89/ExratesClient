package ru.exrates.mobile.view.prefs

import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.preference.DialogPreference
import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragmentCompat
import kotlinx.android.synthetic.main.notification_preference.view.*
import org.florescu.android.rangeseekbar.RangeSeekBar
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.R
import ru.exrates.mobile.data.Storage
import ru.exrates.mobile.logic.*
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.entities.json.ExchangeNamesObject
import ru.exrates.mobile.services.MainService

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

class NotificationPreferenceDialogFragment(private val app: MyApp): PreferenceDialogFragmentCompat(), DialogPreference.TargetFragment{
    private lateinit var prefSeekBar: RangeSeekBar<Float>
    private lateinit var exchName: Spinner
    private lateinit var curSymbol: Spinner
    private lateinit var notifPreference: NotificationPreference

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
        curSymbol.setSelection(ad.getPosition(notifPreference.symbol))

        //TODO "min and max value"

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

    override fun <T : Preference?> findPreference(key: CharSequence): T? {
        return null
    }
}
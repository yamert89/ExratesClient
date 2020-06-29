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
import ru.exrates.mobile.R

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

    override fun getDialogLayoutResource(): Int {
        return R.layout.notification_preference
    }
}

class NotificationPreferenceDialogFragment: PreferenceDialogFragmentCompat(){
    lateinit var prefSeekBar: RangeSeekBar<Float>
    lateinit var exchName: Spinner
    lateinit var curSymbol: Spinner

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        prefSeekBar = view.findViewById(R.id.pref_seekBar)
        exchName = view.findViewById(R.id.pref_exch)
        curSymbol = view.findViewById(R.id.pref_cur)

        val exAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        val curAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)



        val notifPreference = preference as NotificationPreference
        prefSeekBar.setRangeValues(notifPreference.min, notifPreference.max)
        //curSymbol.text = notifPreference.symbol



    }

    override fun onDialogClosed(positiveResult: Boolean) {
        TODO("Not yet implemented")
    }




}
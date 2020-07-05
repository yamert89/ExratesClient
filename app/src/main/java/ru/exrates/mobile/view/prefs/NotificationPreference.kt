package ru.exrates.mobile.view.prefs

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import ru.exrates.mobile.R
import ru.exrates.mobile.logic.*

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


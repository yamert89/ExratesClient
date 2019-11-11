package ru.exrates.mobile.logic

import android.content.Context
import ru.exrates.mobile.EXCH_STORAGE

class Storage(private val context: Context) {

    private fun getStringValue(storage: String, value: String, def: String): String =
        context.getSharedPreferences(storage, Context.MODE_PRIVATE).getString(value, def) ?: def

    private fun storeStringValue(storage: String, key: String, value: String){
        val editor = context.getSharedPreferences(storage, Context.MODE_PRIVATE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getStoreExchangeStringValue(value: String, def: String) = getStringValue(EXCH_STORAGE, value, def)

    fun saveStoreExchangeStringValue(key: String, value: String) = storeStringValue(EXCH_STORAGE, key, value)



}
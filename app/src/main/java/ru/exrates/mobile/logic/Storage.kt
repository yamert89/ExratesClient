package ru.exrates.mobile.logic

import android.content.Context
import ru.exrates.mobile.EXCH_STORAGE
import java.io.*

class Storage(private val context: Context) {

    private fun getStringValue(storage: String, value: String, def: String): String =
        context.getSharedPreferences(storage, Context.MODE_PRIVATE).getString(value, def) ?: def

    private fun storeStringValue(storage: String, key: String, value: String){
        val editor = context.getSharedPreferences(storage, Context.MODE_PRIVATE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    //fun getStoreExchangeStringValue(value: String, def: String) =


    //fun saveStoreExchangeStringValue(key: String, value: String) = storeStringValue(EXCH_STORAGE, key, value)

    fun <T> saveObject(obj: T, fileName: String){
        val os = ObjectOutputStream(FileOutputStream(File(context.filesDir, fileName)))
        os.writeObject(obj)
        os.flush()
        os.close()
    }

    fun <T> loadObject(fileName: String): T {
        val _is = ObjectInputStream(FileInputStream(File(context.filesDir, fileName)))
        val ob = _is.readObject()
        _is.close()
        return ob as T
    }





}
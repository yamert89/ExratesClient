package ru.exrates.mobile.logic

import android.content.Context
import ru.exrates.mobile.DEFAULT_STORAGE
import java.io.*

class Storage(private val context: Context) {


    fun <T> getValue(key: String, def: T, storage: String = DEFAULT_STORAGE): T{
       val sp = context.getSharedPreferences(storage, Context.MODE_PRIVATE)
       return when(def){
           is String -> sp.getString(key, def) as T ?: def
           is Int -> sp.getInt(key, def) as T ?: def
           is Boolean -> sp.getBoolean(key, def) as T ?: def
           else -> {
               loadObject(key, def)
           }
        }
    }

    fun <T> storeValue(key: String, value: T, storage: String = DEFAULT_STORAGE){
        val editor = context.getSharedPreferences(storage, Context.MODE_PRIVATE).edit()
        when(value){
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Long -> editor.putLong(key, value)
            else -> {
                saveObject(value, key)
                editor.clear()
                return
            }
        }
        editor.apply()
    }

    fun <T> saveObject(obj: T, fileName: String){
        val os = ObjectOutputStream(FileOutputStream(File(context.filesDir, fileName)))
        os.writeObject(obj)
        os.flush()
        os.close()
    }

    fun <T> loadObject(fileName: String, def: T? = null): T {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            if(def != null) return def else throw FileNotFoundException("File < $fileName > not found in storage")
        }
        val _is = ObjectInputStream(FileInputStream(File(context.filesDir, fileName)))
        val ob = _is.readObject()
        _is.close()
        return ob as T
    }







}
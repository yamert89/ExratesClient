package ru.exrates.mobile.data

import android.content.Context
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import ru.exrates.mobile.logic.DEFAULT_STORAGE
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths

class Storage(val context: Context, val om: ObjectMapper) {


    inline fun <reified T> getValue(key: String, def: T, storage: String = DEFAULT_STORAGE): T{
       val sp = context.getSharedPreferences(storage, Context.MODE_PRIVATE)
       return when(def){
           is String -> sp.getString(key, def) as T ?: def
           is Int -> sp.getInt(key, def) as T ?: def
           is Boolean -> sp.getBoolean(key, def) as T ?: def
           else -> loadObjectFromJson(key, def)
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
                try {
                    saveObjectAsJson(value, key)
                }catch (e: NotSerializableException){
                    saveObjectAsJson(value, key)
                }
                editor.clear()
                return
            }
        }
        editor.apply()
    }

    @Deprecated("old type serialization", ReplaceWith("saveObjectAsJson") )
    fun <T> saveObject(obj: T, fileName: String){
        val os = ObjectOutputStream(FileOutputStream(File(context.filesDir, fileName)))
        os.writeObject(obj)
        os.flush()
        os.close()
    }

    private fun <T> saveObjectAsJson(obj: T, fileName: String){
        Files.write(Paths.get("${context.filesDir}/$fileName"), om.writeValueAsBytes(obj))
    }

    inline fun <reified T> loadObjectFromJson(fileName: String, def: T? = null): T{
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            if(def != null) return def else throw FileNotFoundException("File < $fileName > not found in storage")
        }
        val ob: Any? = om.readValue(File(context.filesDir, fileName), object : TypeReference<T>(){})
        return ob as T
    }

    @Deprecated("old type deserialization", ReplaceWith("loadObjectFromJson") )
    inline fun <reified T> loadObject(fileName: String, def: T? = null): T {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            if(def != null) return def else throw FileNotFoundException("File < $fileName > not found in storage")
        }
        val _is = ObjectInputStream(FileInputStream(File(context.filesDir, fileName)))
        var ob: Any? = null
        ob = try{
            _is.readObject()
        }catch (e: InvalidClassException){
            throw InvalidClassException("Class model was changed: ${e.message}")
        }catch (e: NotSerializableException){
            ObjectMapper().readValue(File(context.filesDir, fileName), T::class.java)
            //ob = ObjectMapper().readValue
        }
        _is.close()
        return ob as T
    }







}
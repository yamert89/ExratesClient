package ru.exrates.mobile.logic.entities.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.std.MapDeserializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import ru.exrates.mobile.logic.entities.CurrencyPair
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class ArrayBlockingDeserializer : JsonDeserializer<ArrayBlockingQueue<Double>>(){
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): ArrayBlockingQueue<Double> {
        val list: ArrayList<Double> = p!!.readValueAs(object :
            TypeReference<java.util.ArrayList<Double>?>() {})
        if (list.isEmpty()) return ArrayBlockingQueue(1)

        return ArrayBlockingQueue(list.size, false, list)

    }

}

class UpdateTimesDeserializer: StdDeserializer<TreeMap<String, Double>>(TreeMap::class.java){
    override fun deserialize(
        p: JsonParser?,
        ctxt: DeserializationContext?
    ): TreeMap<String, Double> {
        val map = p!!.readValueAs<Map<String, Double>>(object: TypeReference<Map<String, Double>>(){})
        val comparator = kotlin.Comparator<String>{
            o1, o2 ->
            val numToken1 = o1.split("\\D".toRegex())





            o1.compareTo(o2)

        }
        val treeMap: MutableMap<String, Double> = TreeMap(comparator)
        treeMap.putAll(map)
        return TreeMap(treeMap)
    }

    fun String.compareStringToken(s: String): Int{
        val sequence = "mhdwMY"
        return sequence.indexOf(this) - sequence.indexOf(s)
    }

}
package ru.exrates.mobile.logic.entities.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.io.Serializable
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

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
        val comparator = KeyComparator()
        val treeMap: MutableMap<String, Double> = TreeMap(comparator)
        treeMap.putAll(map)
        return TreeMap(treeMap as SortedMap)
    }





}

class KeyComparator : Comparator<String>, Serializable{
    override fun compare(o1: String?, o2: String?): Int {
        var numToken1 = 0
        var numToken2 = 0
        var stringToken1 = ""
        var stringToken2 = ""
        var compString = 0
        var compNum = 0
        try {
            val regExp = "(\\d{1,2})(\\D)".toRegex()
            val groups1 = regExp.find(o1!!)?.groups ?: throw NullPointerException("Reg exp not found for $o1")
            numToken1 = groups1[1]!!.value.toInt()
            stringToken1 = groups1[2]!!.value
            val groups2 = regExp.find(o2!!)?.groups ?: throw NullPointerException("Reg exp not found for $o2")
            numToken2 = groups2[1]!!.value.toInt()
            stringToken2 = groups2[2]!!.value
            compString = stringToken1.compareStringToken(stringToken2)
            compNum = numToken1.compareIntToken(numToken2)

        }catch (e: Exception){
            e.printStackTrace()
        }
        return when {
            compString > 0 -> 1
            compString < 0 -> -1
            else -> compNum
        }
    }
    private fun String.compareStringToken(s: String): Int{
        val sequence = "mhdwMY"
        var res = sequence.indexOf(this) - sequence.indexOf(s)
        if(res < 0) res = -1
        if(res > 0) res = 1
        return when {
            res < 0 -> -1
            res > 0 -> 1
            else -> 0
        }
    }

    private fun Int.compareIntToken(v : Int): Int{
        return when {
            this == v -> 0
            this > v -> 1
            else -> -1
        }
    }

}
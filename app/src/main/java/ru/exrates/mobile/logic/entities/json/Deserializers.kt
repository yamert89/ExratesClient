package ru.exrates.mobile.logic.entities.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.std.ArrayBlockingQueueDeserializer
import ru.exrates.mobile.logic.entities.CurrencyPair
import java.util.concurrent.ArrayBlockingQueue

class ArrayBlockingDeserializer : JsonDeserializer<ArrayBlockingQueue<Double>>(){
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): ArrayBlockingQueue<Double> {
        val list = p!!.readValueAs(ArrayList::class.java)
        if (list.isEmpty()) return ArrayBlockingQueue(1)
        val doubleList = ArrayList<Double>()
        list.forEach { doubleList.add(it.toDouble()) }
        return ArrayBlockingQueue(list.size, false, list)

    }

}
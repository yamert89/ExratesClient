package ru.exrates.mobile.logic.entities

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.util.concurrent.ArrayBlockingQueue

class ArrayBlockingQueueDeSerializer(): StdDeserializer<ArrayBlockingQueue<Double>>(ArrayBlockingQueue::class.java) {
    override fun deserialize(
        p: JsonParser?,
        ctxt: DeserializationContext?
    ): ArrayBlockingQueue<Double> {
        val list = ArrayList<Double>()
        if (p == null) throw NullPointerException("Json parser is null")
        while (p.nextToken() == JsonToken.VALUE_NUMBER_FLOAT) list.add(p.doubleValue)
        return if(list.size > 0) ArrayBlockingQueue(list.size, false, list) else ArrayBlockingQueue(1)
    }
}



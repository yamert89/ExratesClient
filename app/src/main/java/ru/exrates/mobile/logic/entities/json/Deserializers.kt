package ru.exrates.mobile.logic.entities.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import ru.exrates.mobile.logic.entities.CurrencyPair
import java.util.concurrent.ArrayBlockingQueue

class ArrayBlockingDeserializer : JsonDeserializer<ArrayBlockingQueue<Double>>(){
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): ArrayBlockingQueue<Double> {
        val list: ArrayList<Double> = p!!.readValueAs(object :
            TypeReference<java.util.ArrayList<Double>?>() {})
        if (list.isEmpty()) return ArrayBlockingQueue(1)

        return ArrayBlockingQueue(list.size, false, list)

    }

}
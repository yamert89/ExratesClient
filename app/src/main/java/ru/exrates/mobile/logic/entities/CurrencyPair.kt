package ru.exrates.mobile.logic.entities


import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import ru.exrates.mobile.logic.entities.json.ArrayBlockingDeserializer
import java.util.*
import java.util.concurrent.ArrayBlockingQueue


data class CurrencyPair(
    val symbol: String,
    val price: Double,
    val priceChange: TreeMap<String, Double>,
    val updateTimes: Array<Long?>,
    @JsonDeserialize(using = ArrayBlockingDeserializer::class)
    val priceHistory: ArrayBlockingQueue<Double>,
    val visible: Boolean = true,
    var exchangeName : String? = null
) : java.io.Serializable{

    companion object{
        fun createEmptyInstance() = CurrencyPair("", 0.0, TreeMap(), emptyArray(), ArrayBlockingQueue(1))
    }





    //@JsonDeserialize(using = ArrayBlockingQueueDeSerializer::class)





}
package ru.exrates.mobile.logic.entities


import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import ru.exrates.mobile.logic.entities.json.ArrayBlockingDeserializer
import ru.exrates.mobile.logic.entities.json.UpdateTimesDeserializer
import java.util.*
import java.util.concurrent.ArrayBlockingQueue


data class CurrencyPair(
    val symbol: String,
    val price: Double,
    @JsonDeserialize(using = UpdateTimesDeserializer::class)
    val priceChange: TreeMap<String, Double>,
    val updateTimes: Array<Long?>,
    val priceHistory: List<Double>,
    val historyPeriods: List<String>?,
    val visible: Boolean = true,
    var exchangeName: String,
    var exId : Int

) : java.io.Serializable{

    companion object{
        fun createEmptyInstance() = CurrencyPair("", 0.0, TreeMap(), emptyArray(), emptyList(), emptyList(), false, "", 0)
    }





    //@JsonDeserialize(using = ArrayBlockingQueueDeSerializer::class)





}
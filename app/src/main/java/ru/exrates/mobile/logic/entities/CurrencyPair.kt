package ru.exrates.mobile.logic.entities


import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import ru.exrates.mobile.logic.entities.json.ArrayBlockingDeserializer
import ru.exrates.mobile.logic.entities.json.UpdateTimesDeserializer
import java.util.*
import java.util.concurrent.ArrayBlockingQueue


data class CurrencyPair(
    val baseCurrency: String,
    val quoteCurrency: String,
    val symbol: String,
    val price: Double,
    @JsonDeserialize(using = UpdateTimesDeserializer::class)
    val priceChange: TreeMap<String, Double>,
    val priceHistory: List<Double>,
    val historyPeriods: List<String>?,
    val exchangeName: String,
    val exId : Int,
    val status:Int,
    val unavailable: Boolean

) : java.io.Serializable{

    companion object{
        fun createEmptyInstance() = CurrencyPair("", "", "", 0.0, TreeMap(), emptyList(), emptyList(), "", 0, 0, false)
    }
    fun symbolItem() = "$baseCurrency / $quoteCurrency"





    //@JsonDeserialize(using = ArrayBlockingQueueDeSerializer::class)





}
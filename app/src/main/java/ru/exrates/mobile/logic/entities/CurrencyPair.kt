package ru.exrates.mobile.logic.entities


import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.util.concurrent.ArrayBlockingQueue


data class CurrencyPair(val symbol: String, val price: Double, val priceChange: Map<String, Double>,
                        val updateTimes: Array<Long?>,
                        @JsonDeserialize(using = ArrayBlockingQueueDeSerializer::class) val priceHistory: ArrayBlockingQueue<Double>, val visible: Boolean = true ) : java.io.Serializable{ //todo replace with data class

    //@JsonDeserialize(using = ArrayBlockingQueueDeSerializer::class)





}
package ru.exrates.mobile.logic.entities


import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import java.util.concurrent.ArrayBlockingQueue

@Serializable
class CurrencyPair(val symbol: String,
                   val price: Double,
                   val priceChange: Map<String, Double>,
                   @ContextualSerialization val priceHistory: ArrayBlockingQueue<Double>,
                   var visible: Boolean = true) {


}
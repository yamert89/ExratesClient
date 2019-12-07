package ru.exrates.mobile.logic.entities


import java.util.concurrent.ArrayBlockingQueue


class CurrencyPair() : java.io.Serializable{ //todo replace with data class
    lateinit var symbol: String
    var price: Double = 0.0
    lateinit var priceChange: Map<String, Double>
    lateinit var updateTimes: Array<Long?>
    lateinit var priceHistory: ArrayBlockingQueue<Double>
    val visible: Boolean = true

    constructor(symbol: String, price: Double, priceChange: Map<String, Double>,
                updateTimes: Array<Long?>, priceHistory: ArrayBlockingQueue<Double>) : this(){
        this.symbol = symbol
        this.price = price
        this.priceChange = priceChange
        this.updateTimes = updateTimes
        this. priceHistory = priceHistory
    }

}
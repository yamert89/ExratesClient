package ru.exrates.mobile.logic.entities

import java.util.concurrent.ArrayBlockingQueue

class CurrencyPair(val symbol: String, val price: Double, val priceChange: Map<String, Double>,
                   val priceHistory: ArrayBlockingQueue<Double>) {


}
package ru.exrates.mobile.logic.entities.json

data class ExchangePayload(val exId: Int, val interval: String, val pairs: Array<String>){
    override fun toString(): String {
        return "exId: $exId, timeout: $interval, pairs: ${pairs.joinToString()}"
    }
}

data class ExchangeNamesObject(val id: Int, val name: String, val delimiter: String = "", val pairs: List<String>){ //todo delimiter will removed?
    fun getSplitedCurNames(symbol: String): Pair<String, String> {
        val arr = symbol.split(" / ")
        return arr[0] to arr[1]
    }

    fun getSymbol(c1: String, c2: String) = "$c1$delimiter$c2"
}

data class CursPeriod(val interval: String, val values: Map<String, Double>, val status: Int)

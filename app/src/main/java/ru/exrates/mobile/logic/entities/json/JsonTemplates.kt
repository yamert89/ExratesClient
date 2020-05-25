package ru.exrates.mobile.logic.entities.json

class ExchangePayload(val exId: Int, val interval: String, val pairs: Array<String>){
    override fun toString(): String {
        return "exId: $exId, timeout: $interval, pairs: ${pairs.joinToString()}"
    }
}

class ExchangeNamesObject(val id: Int, val name: String, val pairs: List<String>)

class CursPeriod(val interval: String, val values: Map<String, Double>)

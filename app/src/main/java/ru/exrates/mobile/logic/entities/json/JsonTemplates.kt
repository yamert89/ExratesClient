package ru.exrates.mobile.logic.entities.json

import java.io.Serializable

class ExchangePayload(val exId: Int, val timeout: String, val pairs: Array<String>){
    override fun toString(): String {
        return "exId: $exId, timeout: $timeout, pairs: ${pairs.joinToString()}"
    }
}

class ExchangeNamesObject(val id: Int, val name: String, val pairs: List<String>)

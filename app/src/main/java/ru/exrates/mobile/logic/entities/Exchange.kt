package ru.exrates.mobile.logic.entities

class Exchange(): java.io.Serializable{ //todo replace with data class
    lateinit var name: String
    lateinit var pairs: MutableList<CurrencyPair>
    lateinit var changePeriods: List<String>
    val showHidden: Boolean = true

constructor(name: String, pairs: MutableList<CurrencyPair>, changePeriods: List<String>): this(){
    this.name = name
    this.pairs = pairs
    this.changePeriods = changePeriods
}

    override fun toString(): String {
        return "$name | ${pairs.size} pairs"
    }
}





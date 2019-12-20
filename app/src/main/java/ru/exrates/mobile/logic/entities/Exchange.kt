package ru.exrates.mobile.logic.entities

data class Exchange(val name: String, val pairs: MutableList<CurrencyPair>, val changePeriods: List<String>, val showHidden: Boolean = true): java.io.Serializable{

    override fun toString(): String {
        return "$name | ${pairs.size} pairs"
    }
}





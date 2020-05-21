package ru.exrates.mobile.logic.entities

import ru.exrates.mobile.logic.logD

data class Exchange(val exId: Int, val name: String, val pairs: MutableList<CurrencyPair>,
                    val changePeriods: List<String>,
                    val historyPeriods: List<String>,
                    val showHidden: Boolean = true,
                    val status: Int,
                    val delimiter: String = ""
                    ): java.io.Serializable{

    override fun toString(): String {
        return "$name | ${pairs.size} pairs"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Exchange
        if (exId != other.exId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = exId
        result = 31 * result + name.hashCode()
        result = 31 * result + pairs.hashCode()
        result = 31 * result + changePeriods.hashCode()
        result = 31 * result + historyPeriods.hashCode()
        result = 31 * result + showHidden.hashCode()
        return result
    }

}

class SelectedExchange(id: Int){
    var listener : (() -> Unit)? = null
    var id : Int = id
        set(ex){
            logD("Selected exchange old id = $id, new id = $ex")
            field = ex
            listener?.invoke()
        }

}





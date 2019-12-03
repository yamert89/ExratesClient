package ru.exrates.mobile.logic.entities


import kotlinx.serialization.Serializable
@Serializable
data class Exchange(val name: String, val id: Int,
               val pairs: MutableList<CurrencyPair>,
               val changePeriods: List<String>,
               val showHidden: Boolean = true) : java.io.Serializable{



}
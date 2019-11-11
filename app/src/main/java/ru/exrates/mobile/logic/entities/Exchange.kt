package ru.exrates.mobile.logic.entities


import kotlinx.serialization.Serializable
@Serializable
class Exchange(val name: String,
               val pairs: MutableList<CurrencyPair>,
               val changePeriods: List<String>,
               val showHidden: Boolean = true) {



}
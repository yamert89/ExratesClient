package ru.exrates.mobile.logic.entities


import kotlinx.serialization.Serializable
@Serializable
class Exchange(val name: String, val pairs: List<CurrencyPair>, val changePeriods: List<String>) {



}
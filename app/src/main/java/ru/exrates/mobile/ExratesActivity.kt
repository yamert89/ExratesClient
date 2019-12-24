package ru.exrates.mobile

import kotlinx.serialization.internal.MapEntry
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange

interface ExratesActivity {

    fun updateExchangeData(exchange: Exchange)

    fun updatePairData(map: Map<String, CurrencyPair>)

    suspend fun firstLoadActivity()

    fun save(vararg args : MapEntry<String, Any>)

    fun saveState()




}
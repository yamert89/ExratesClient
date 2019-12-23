package ru.exrates.mobile

import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange

interface ExratesActivity {

    fun updateExchangeData(exchange: Exchange)

    fun updatePairData(map: Map<String, CurrencyPair>)


}
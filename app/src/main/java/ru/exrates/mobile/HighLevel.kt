package ru.exrates.mobile

import android.util.Log
import java.text.DecimalFormat

const val EXRATES = ":EXRATES:"
const val EXCH_NAME = "exchName"
const val EXCH_STORAGE = "exchStorage"
const val DEFAULT_STORAGE = "defstoraage"
const val SAVED_EXCHANGE = "savedExchange"
const val SAVED_EXCHANGES = "savedExchanges"
const val SAVED_CURRENCY_NAME_LIST = "SCNL"
const val SAVED_EXCHANGE_NAME_LIST = "SENL"
const val SAVED_CUR_IDX = "SCIX"
const val SAVED_EX_IDX = "SEXIX"
const val SAVED_CURRENCIES_LIST = "SCL"
const val DEFAULT = "defaulValueforAny"
const val DEFAULT_MAIN_CURRENCY_NAME = "DMCN"
const val DEFAULT_MAIN_EXCHANGE_NAME = "DMEN"

const val CURRENT_INTERVAL = "curIntrvl"
const val EXTRA_CURRENCY_NAME = "excrn"
const val STORAGE_EXCHANGES = "exchanges"
const val SAVED_CURRENCY = "savedcurrency"

const val IS_FIRST_LOAD = "firstload"

fun log_d(message: String) = Log.d(EXRATES, message)

fun log_e(message: String) = Log.e(EXRATES, message)

fun Double.toNumeric() = DecimalFormat("####.########################").format(this)



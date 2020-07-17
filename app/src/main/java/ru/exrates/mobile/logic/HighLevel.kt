package ru.exrates.mobile.logic

import android.util.Log
import java.lang.NumberFormatException
import java.text.DecimalFormat
import java.util.*

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
const val SAVED_EXID = "SEXID"
const val SAVED_CURRENCIES_ADAPTER = "SCAD"
const val SAVED_CURRENCIES_ADAPTER_BINANCE = "SC1"
const val SAVED_CURRENCIES_ADAPTER_P2PB2B = "SC2"
const val SAVED_EXCHANGE_NAMES_ADAPTER = "SeNa"
const val SAVED_CURRENCIES_NAMES = "SCn"
const val SAVED_HISTRORY_INTERVAL = "SHINT"
const val DEFAULT = "defaulValueforAny"
const val DEFAULT_MAIN_CURRENCY_NAME = "DMCN"
const val DEFAULT_MAIN_EXCHANGE_NAME = "DMEN"

const val CURRENT_INTERVAL = "curIntrvl"
const val CURRENT_GRAPH_INTERVAL = "CGI"
const val CURRENT_GRAPH_INTERVAL_IDX = "CGIidx"
const val SAVED_GRAPH_INTERVAL_IDX = "CGIidx"
const val CURRENT_EXCHANGE = "CUREXCH"
const val CURRENT_EXCHANGE_ID = "CUREXCHid"
const val CURRENT_PAIR_INFO = "CURPINFO"
const val CURRENT_PAIR_ = "CURPair"
const val CURRENT_CUR_1 = "CUR1"
const val CURRENT_CUR_2 = "CUR2"
const val EXTRA_CURRENCY_NAME_1 = "excrn1"
const val EXTRA_CURRENCY_NAME_2 = "excrn2"
const val EXTRA_EXCHANGE_ICO = "exEXico"
const val EXTRA_EXCHANGE_ID = "exEXID"
const val EXTRA_PERIOD = "exper"
const val EXTRA_MAX_LIMIT = "e34GD"
const val EXTRA_MIN_LIMIT = "eS34GGr"
const val EXTRA_EX_ID = "nyt45fdg"
const val EXTRA_CUR_ICO = "ECI"
const val STORAGE_EXCHANGES = "exchanges"
const val SAVED_CURRENCY = "savedcurrency"
const val NOTIFICATION_CHANNEL = "435235fdg"
const val PREF_NOTIFICATION_MIN = "pnm"
const val PREF_NOTIFICATION_MAX = "pnmax"
const val PREF_NOTIFICATION_CUR = "pnc"
const val PREF_NOTIFICATION_EX = "pne"
const val EMPTY_CUR_ITEM = "<...>"

const val IS_FIRST_LOAD = "firstload"
const val CURRENCY_HISTORIES_MAIN_NUMBER = 10
const val CURRENCY_HISTORIES_CUR_NUMBER = 25

fun logD(message: String) = Log.d(EXRATES, message)

fun logE(message: String) = Log.e(EXRATES, message)

fun logT(message: String) = Log.v(EXRATES, message)

fun logW(message: String) = Log.w(EXRATES, message)

fun Double.toNumeric(precision: Int = 10 ): String = DecimalFormat("####.${"#".repeat(precision)}").format(this).replace(",", ".")

fun Float.toNumeric(): String = this.toDouble().toNumeric()

fun String.toSafetyFloat(): Float{
    val str = this.replace(",", ".")
    logD("string $str converting to safety float")
    return try {
        str.toDouble().toNumeric().toFloat()
    }catch (e: NumberFormatException){
        str.substring(0, this.length - 1).toSafetyFloat()
    }
}







package ru.exrates.mobile

import com.fasterxml.jackson.module.kotlin.readValue

class IconProducer(private val app: MyApp) {
    private var iconList: Array<JsonUnit> = emptyArray()

    init {
        val applicationContext = app.applicationContext
        val inputStream = applicationContext.resources.assets.open("manifest.json")
        iconList = app.om.readValue(inputStream)
    }


    fun getIconIdentifier(symbol: String): Int{
        return app.resources.getIdentifier(symbol.toLowerCase(), null, null)
    }
}

class JsonUnit(val symbol: String, val name: String, val color: String)
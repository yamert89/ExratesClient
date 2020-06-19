package ru.exrates.mobile.logic.rest

import ru.exrates.mobile.ExRates
import ru.exrates.mobile.services.MainService

class ServiceModel(private val restService: RestService, private val service: MainService) {

    fun getPair(c1: String, c2: String, limit: Int){
        restService.getPair(c1, c2, limit).enqueue(ServicePairCallback(service))
    }
}
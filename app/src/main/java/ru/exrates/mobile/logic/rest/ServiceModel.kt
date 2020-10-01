package ru.exrates.mobile.logic.rest

import ru.exrates.mobile.ExRates
import ru.exrates.mobile.services.MainService
import ru.exrates.mobile.view.prefs.NotificationPreferenceDialogFragment
import ru.exrates.mobile.view.prefs.ServiceCallbackReceiver

class ServiceModel(private val restService: RestService, private val receiver: ServiceCallbackReceiver) : RestModel{

    fun getPair(c1: String, c2: String, limit: Int){
        if (receiver !is MainService) return
        restService.getPair(c1, c2, limit).enqueue(ServicePairCallback(receiver))
    }

    fun onePair(c1: String, c2: String, exId: Int){
        if(receiver !is NotificationPreferenceDialogFragment) return
        restService.addOnePair(c1, c2, exId, "1d").enqueue(NotificationPairPriceCallback(receiver)) //todo interval clear
    }
}
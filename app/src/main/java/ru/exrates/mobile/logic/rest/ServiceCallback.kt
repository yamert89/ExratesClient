package ru.exrates.mobile.logic.rest

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.logE
import ru.exrates.mobile.services.MainService
import ru.exrates.mobile.view.prefs.NotificationPreferenceDialogFragment
import ru.exrates.mobile.view.prefs.ServiceCallbackReceiver


abstract class ServiceCallback<T>(val receiver: ServiceCallbackReceiver): Callback<T> {
    override fun onFailure(call: Call<T>, t: Throwable) {
        logE("failed service callback")
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.body() == null) throw IllegalStateException("service response is null")
    }
}

class ServicePairCallback(private val service: MainService): ServiceCallback<MutableList<CurrencyPair>>(service){
    override fun onResponse(
        call: Call<MutableList<CurrencyPair>>,
        response: Response<MutableList<CurrencyPair>>
    ) {
        super.onResponse(call, response)
        service.updatePair(response.body()!!)
    }
}
class NotificationPairPriceCallback(private val notificationPreferenceDialogFragment: NotificationPreferenceDialogFragment): ServiceCallback<CurrencyPair>(notificationPreferenceDialogFragment){
    override fun onResponse(call: Call<CurrencyPair>, response: Response<CurrencyPair>) {
        super.onResponse(call, response)
        notificationPreferenceDialogFragment.updateRange(response.body()!!)
    }
}


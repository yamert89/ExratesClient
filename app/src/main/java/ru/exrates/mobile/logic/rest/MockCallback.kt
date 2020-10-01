package ru.exrates.mobile.logic.rest

import retrofit2.Call
import retrofit2.Response
import ru.exrates.mobile.presenters.Presenter
import ru.exrates.mobile.view.ExratesActivity

abstract class MockCallback<T>(activity: ExratesActivity, presenter: Presenter): ExCallback<T>(activity, presenter) {

}
class MockCheckCallback(activity: ExratesActivity, presenter: Presenter) : MockCallback<Pair<Int, String>>(activity, presenter){
    override fun onResponse(call: Call<Pair<Int, String>>, response: Response<Pair<Int, String>>) {
        super.onResponse(call, response)
    }
}
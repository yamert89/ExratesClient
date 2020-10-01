package ru.exrates.mobile.logic.rest

import retrofit2.Call

interface MockRestService: RestService {
    override fun check(versionToken: String): Call<Pair<Int, String>>
}
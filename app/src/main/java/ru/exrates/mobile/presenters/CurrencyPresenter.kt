package ru.exrates.mobile.presenters

import ru.exrates.mobile.logic.rest.RestModel
import ru.exrates.mobile.view.ExratesActivity
import ru.exrates.mobile.view.MainActivity

class CurrencyPresenter(private val basic: BasePresenter) : Presenter by basic{
    private val storage = basic.storage
    private val app = basic.app
    private lateinit var restModel: RestModel
    private var activity: ExratesActivity? = null
    private lateinit var mainActivity: MainActivity

    override fun start() {

    }


    /*
     *******************************************************************************
     * Callback methods
     *******************************************************************************/

    /*
     *******************************************************************************
     * Private methods
     *******************************************************************************/

    override fun task() {
        TODO("Not yet implemented")
    }

    override fun attachView(view: ExratesActivity, presenter: Presenter?) {
        basic.attachView(view, this)
        activity = view
        mainActivity = activity as MainActivity
        restModel = basic.restModel
    }

    override fun detachView() {
        activity = null

    }


    /*
     ******************************************************************************
     * Public methods for activity
     *******************************************************************************/



}
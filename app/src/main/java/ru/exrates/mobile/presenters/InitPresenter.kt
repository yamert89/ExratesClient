package ru.exrates.mobile.presenters

import android.content.Intent
import ru.exrates.mobile.MyApp
import ru.exrates.mobile.logic.logD
import ru.exrates.mobile.logic.rest.ClientCodes
import ru.exrates.mobile.view.MainActivity
import ru.exrates.mobile.view.dialogs.ServerMessage

class InitPresenter(app: MyApp): BasePresenter(app) {
    val clientToken = "3f585cdf-6138-4b1a-88e1-40bd3ff0693b"
    var needsClose = false
    override fun resume() {
        super.resume()
        if (needsClose) activity!!.finish()
        activityRestModel.checkMessages(clientToken)
    }

    /*
    * Callback
    */
    fun showMessage(response: Pair<Int, String>){
        when(response.first){
            ClientCodes.CLIENT_NEEDS_UPDATE ->{
                needsClose = true
                ServerMessage(this, "Please, update this app", response.second, true).show(activity!!.supportFragmentManager, "serverMessage")
            }
            ClientCodes.CLIENT_SHOW_DIALOG_WITH_LINK ->{
                val arr = response.second.split("|")
                ServerMessage(this, arr[0], arr[1]).show(activity!!.supportFragmentManager, "serverMessage")
            }
            ClientCodes.CLIENT_SHOW_DIALOG ->{
                ServerMessage(this, response.second).show(activity!!.supportFragmentManager, "serverMessage")
            }
            ClientCodes.CLIENT_NOTHING -> activity!!.startActivity(Intent(activity, MainActivity::class.java))

        }
        logD("End show")
    }
}
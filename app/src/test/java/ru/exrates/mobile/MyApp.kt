package ru.exrates.mobile

import android.app.Application
import ru.exrates.mobile.logic.DataProvider

class MyApp(): Application(){
    val dataProvider: DataProvider = DataProvider()

}
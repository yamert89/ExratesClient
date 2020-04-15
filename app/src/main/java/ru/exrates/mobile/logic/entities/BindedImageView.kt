package ru.exrates.mobile.logic.entities

import android.content.Context
import android.widget.ImageView

class BindedImageView(context: Context): androidx.appcompat.widget.AppCompatImageView(context) {
    var bindedPairName: String = ""
}
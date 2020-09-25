package ru.exrates.mobile.view

import android.os.Bundle
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.progress.view.*
import ru.exrates.mobile.R
import ru.exrates.mobile.presenters.InitPresenter

class InitialActivity: ExratesActivity() {


    lateinit var presenter: InitPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.initial)
        supportActionBar!!.hide()
        progressLayout = ConstraintLayout(applicationContext)
        //progressLayout = findViewById(R.id.progress)
        presenter = InitPresenter(app)
        presenter.attachView(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.resume()
    }
}
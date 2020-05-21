package ru.exrates.mobile.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ru.exrates.mobile.presenters.BasePresenter
import ru.exrates.mobile.presenters.MainPresenter
import ru.exrates.mobile.view.MainActivity

class ConnectionFailed(private val presenter: BasePresenter): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity).apply {
            setMessage("Не удалось подключиться к серверу")
            setPositiveButton("Попробовать снова") { dialog, which ->
                presenter.resume()
            }
            setNegativeButton("Закрыть приложение") { _, _ ->
                activity?.finish()
            }
        }.create()

    }
}
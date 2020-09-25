package ru.exrates.mobile.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ru.exrates.mobile.presenters.BasePresenter
import ru.exrates.mobile.view.MainActivity

class ServerMessage(private val presenter: BasePresenter, private val message: String, private val link: String = "", private val closeable: Boolean = false): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity).apply {
            setMessage(message)
            if (closeable) {
                setNeutralButton("Update") { dialog, which ->
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                }
                setNegativeButton("Закрыть приложение") { _, _ ->
                    activity?.finish()
                }
            } else{
                setPositiveButton("OK") { dialog, which ->
                    startActivity(Intent(requireActivity().applicationContext, MainActivity::class.java))
                }
                if (link.isNotEmpty()) setNeutralButton("Go") { dialog, which ->
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                }
            }
        }.create()
    }
}
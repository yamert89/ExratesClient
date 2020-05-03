package ru.exrates.mobile.logic.activities

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.Toast


class SearchButtonClickListener(private val autoCompleteTextView: AutoCompleteTextView,
                                private val spinner: Spinner,
                                private val context: Context): View.OnClickListener {
    var activated = false

    override fun onClick(v: View?) {
        when(activated){
            false -> showSearch()
            true -> {
                if (autoCompleteTextView.text.isEmpty()) {
                    hideSearch()
                } else{
                    val text = autoCompleteTextView.text.toString()
                    val adapter = spinner.adapter as ArrayAdapter<String>
                    if (adapter.getPosition(text) == -1) {
                        hideSearch()
                        Toast.makeText(context, "Incorrect Pair", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        hideSearch()
                        spinner.setSelection(adapter.getPosition(text))
                    }

                }
            }
        }
    }

    private fun showSearch(){
        autoCompleteTextView.visibility = View.VISIBLE
        spinner.visibility = View.INVISIBLE
        activated = true
    }

    private fun hideSearch(){
        autoCompleteTextView.visibility = View.INVISIBLE
        spinner.visibility = View.VISIBLE
        activated = false
    }
}
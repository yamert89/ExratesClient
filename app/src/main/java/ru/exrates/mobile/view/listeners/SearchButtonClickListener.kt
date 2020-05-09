package ru.exrates.mobile.view.listeners

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.inputmethod.InputMethodManager
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

        /*val activity = (context as ContextWrapper).baseContext as Activity

        val imm: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.

        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it

        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)*/

    }
}
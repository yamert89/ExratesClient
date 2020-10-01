package ru.exrates.mobile.view.listeners

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.widget.*
import androidx.core.animation.doOnEnd
import androidx.core.view.marginStart


class SearchButtonClickListener(private val autoCompleteTextView: AutoCompleteTextView,
                                private val spinner: Spinner,
                                private val goTo: ImageButton,
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
        goTo.isEnabled = false
       /* ObjectAnimator.ofObject(spinner, "layout_marginStart", 0).apply {
            duration = 1000
            start()
        }*/
        ObjectAnimator.ofFloat(autoCompleteTextView, "translationX", 0f).apply {
            duration = 500
            start()
        }
        activated = true
    }

    private fun hideSearch(){
        ObjectAnimator.ofFloat(autoCompleteTextView, "translationX", 500f).apply {
            duration = 500
            start()
            doOnEnd {
                autoCompleteTextView.visibility = View.INVISIBLE
                spinner.visibility = View.VISIBLE
                activated = false
                goTo.isEnabled = true
            }
        }



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
package com.example.dsi_android_ui.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.example.dsi_android_ui.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.SnackbarContentLayout

abstract class BaseDialogFragment<B : ViewBinding> : DialogFragment() {

    protected lateinit var binding: B

    abstract fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?): B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_DSIAndroidUI_DialogFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getDialogBinding(inflater, container)
        return binding.root
    }


    /**
     * Creates a custom snackbar to display a message for the time duration provided in milliseconds,
     * if not it will last for [Snackbar.LENGTH_LONG] (2750) with an ICON to the left if a drawable
     * resource is given and an ACTION_BUTTON to the right of the message if an actionText and the
     * actionListener are given
     *
     * @param view           View from where the snackbar is been called
     * @param message        The String message that will be displayed on the snackbar
     * @param duration       How long to display the message. Can be [Snackbar.LENGTH_SHORT],
     * [Snackbar.LENGTH_LONG], [Snackbar.LENGTH_INDEFINITE], or a
     * custom duration in milliseconds.
     * @param icon           The resource identifier of the drawable
     * @param actionText     An String text for the action button
     * @param actionListener A OnClickListener callback to be invoked when the action is clicked
     */
    protected fun createCustomSnackbar(
        view: View,
        message: String,
        duration: Int = Snackbar.LENGTH_LONG,
        @DrawableRes icon: Int? = null,
        actionText: String? = null,
        actionListener: View.OnClickListener? = null
    ): Snackbar {
        val snack = Snackbar.make(view, message, duration)
        snack.setBackgroundTint(resources.getColor(R.color.snackbar_backgroundTint, resources.newTheme()))
        val textView = snack.view.findViewById<View>(R.id.snackbar_action) as TextView
        icon?.let {
            val imgClose = ImageView(context)
            imgClose.scaleType = ImageView.ScaleType.CENTER_INSIDE
            val layImageParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            imgClose.setImageResource(icon)
            (textView.parent as SnackbarContentLayout).addView(imgClose, 0, layImageParams)
        }
        if (actionText != null && actionListener != null) {
            snack.setActionTextColor(resources.getColor(R.color.sap_ui_link, resources.newTheme()))
            snack.setAction(actionText, actionListener)
        }
        return snack
    }


    fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}
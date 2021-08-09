package com.example.dsi_android_ui.utils

import android.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import com.example.dsi_android_ui.MainActivity
import com.example.dsi_android_ui.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.SnackbarContentLayout

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
fun createCustomSnackbar(
    view: View,
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    @DrawableRes icon: Int? = null,
    actionText: String? = null,
    actionListener: View.OnClickListener? = null
): Snackbar {
    val snack = Snackbar.make(view, message, duration)
    snack.setBackgroundTint(view.resources.getColor(R.color.snackbar_backgroundTint, null))
    val textView = snack.view.findViewById<View>(R.id.snackbar_action) as TextView
    icon?.let {
        val imgClose = ImageView(view.context)
        imgClose.scaleType = ImageView.ScaleType.CENTER_INSIDE
        val layImageParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imgClose.setImageResource(icon)
        (textView.parent as SnackbarContentLayout).addView(imgClose, 0, layImageParams)
    }
    if (actionText != null && actionListener != null) {
        snack.setActionTextColor(view.resources.getColor(R.color.sap_ui_link, null))
        snack.setAction(actionText, actionListener)
    }
    return snack
}

/**
 * Creates a custom confirmation dialog to display a message and set an action on confirmation.
 *
 * This dialog contains a message, a cancel button and a action button. Title can be added but it's
 * not required, if action is not given then it sets default text as CONFIRM and if a an action
 * listener is not given the action button will just close the dialog.
 *
 * **Basic example of use:**
 *
 * >`createCustomConfirmDialog(requireActivity(), requireView(), actionButtonTitle= R.string.delete) {
 * delete(id) }`
 *
 * @param activity             Activity from where the dialog is been called, use [androidx.fragment.app.Fragment.requireActivity]
 * @param currentView          View from where the dialog is called, use [androidx.fragment.app.Fragment.requireView]
 * @param message              Use @StringRes identifier to set this message
 * @param title                Use @StringRes identifier to set a title to this dialog
 * @param actionButtonTitle    Use @StringRes identifier to set the action button text, default
 * message is [R.string.confirm]
 * @param actionButtonListener A callback function to be called when the action is clicked
 *
 */
enum class CustomDialogButtonColor(val color: Int) {
    DANGER(R.color.danger_text), PRIMARY(R.color.blue_text)
}

fun createCustomConfirmDialog(
    activity: FragmentActivity,
    @StringRes message: Int = R.string.task_delete_message,
    @StringRes title: Int? = null,
    @StringRes cancelActionLabel: Int = R.string.cancel,
    @StringRes actionButtonLabel: Int = R.string.confirm,
    actionButtonColor: CustomDialogButtonColor = CustomDialogButtonColor.DANGER,
    actionButtonListener: (() -> Unit)? = null,
) {
    val view = MainActivity.getMainView();
    val builder = buildAlertDialog(
        activity,
        view,
        message,
        title,
        cancelActionLabel,
        actionButtonLabel,
        actionButtonListener
    )
    val alert = builder.create()
    with(alert) {
        show()
        val viewWidth = view.measuredWidth
        val height = window?.attributes?.height ?: 200
        val width = (viewWidth * 0.8).toInt()
        window?.setLayout(width, height)
        window?.setBackgroundDrawableResource(R.drawable.dialog_round_corners)
        getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(view.resources.getColor(R.color.blue_text, null))
        getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(view.resources.getColor(actionButtonColor.color, null))
    }
}

private fun buildAlertDialog(
    activity: FragmentActivity,
    view: ViewGroup,
    @StringRes message: Int,
    @StringRes title: Int? = null,
    @StringRes cancelActionLabel: Int = R.string.cancel,
    @StringRes actionButtonLabel: Int = R.string.confirm,
    actionButtonListener: (() -> Unit)? = null,
): AlertDialog.Builder {
    val builder = AlertDialog.Builder(view.context)
    return with(builder) {
        val inflatedView = activity.layoutInflater.inflate(R.layout.dialog_confirm_delete, null)
        inflatedView.findViewById<TextView>(R.id.confirmation_dialog_message).text =
            inflatedView.resources.getString(message)
        setView(inflatedView)
        title?.let { setTitle(it) }
        setPositiveButton(view.resources.getString(actionButtonLabel)) { dialog, _ ->
            actionButtonListener?.run {
                this()
            }
            dialog.cancel()
        }
        setNegativeButton(view.resources.getString(cancelActionLabel)) { dialog, _ ->
            dialog.cancel()
        }
    }
}
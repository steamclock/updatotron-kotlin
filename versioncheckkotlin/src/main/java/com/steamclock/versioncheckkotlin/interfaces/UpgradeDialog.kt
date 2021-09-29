package com.steamclock.versioncheckkotlin.interfaces

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.*
import com.steamclock.versioncheckkotlin.models.DisplayState


interface UpgradeDialog {
    fun show(context: Context, state: DisplayState)
    fun showDialog()
    fun dismissDialog()
}

class DefaultUpgradeDialog: UpgradeDialog {
    private var dialog: AlertDialog? = null

    override fun dismissDialog() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

    override fun showDialog() {
        dismissDialog()
        dialog?.show()
    }

    override fun show(context: Context, state: DisplayState) {
        when (state) {
            DisplayState.ForceUpdate -> {
                createBasicDialog(
                    context,
                    "Must Update",
                    "The version of the application is out of date and cannot run. Please update to the latest version from the Play Store.",
                    requiresUpdate = true,
                    canDismiss = false
                )
            }
            DisplayState.SuggestUpdate -> {
                createBasicDialog(
                    context,
                    "Should Update",
                    "The version of the application is out of date and should not run. Please update to the latest version from the Play Store.",
                    requiresUpdate = true,
                    canDismiss = true
                )
            }
            // todo 2021-09 Handle down for maintenance
            else -> {
                dialog?.dismiss()
                dialog = null
            }
        }

        dialog?.show()
    }

    private fun createBasicDialog(
        context: Context,
        title: String,
        message: String,
        requiresUpdate: Boolean,
        canDismiss: Boolean
    ) {

        val builder = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setCancelable(canDismiss)

            if (requiresUpdate) {
                // Do not set click listener here, we do not want the button to dismiss the dialog.
                setNeutralButton("Upgrade", null)
            }

            if (canDismiss) {
                setPositiveButton("OK") { _, _ ->
                    dialog = null
                    /* todo */
                }
            }
        }

        dialog = builder.create()

        // Tap dance a little to override the click listener so the dialog will not dismiss
        dialog?.apply {
            setOnShowListener {
                getButton(AlertDialog.BUTTON_NEUTRAL)?.setOnClickListener {
                    // todo Proxy out to Play Store.
                }
            }
        }
    }
}
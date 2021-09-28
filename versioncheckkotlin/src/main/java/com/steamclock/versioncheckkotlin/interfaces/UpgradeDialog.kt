package com.steamclock.versioncheckkotlin.interfaces
import android.app.Activity
import android.app.AlertDialog
import androidx.lifecycle.*
import com.steamclock.versioncheckkotlin.models.DisplayState

interface UpgradeDialog {
    fun showDialogForState(state: DisplayState)
}

class DefaultUpgradeDialog(
    private val activity: Activity): UpgradeDialog, LifecycleObserver {

    private var dialog: AlertDialog? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun dismissDialog() {
        dialog?.dismiss()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun showDialog() {
        if (dialog?.isShowing == false) {
            dialog?.show()
        }
    }

    override fun showDialogForState(state: DisplayState) {
        when (state) {
            DisplayState.ForceUpdate -> {
                createBasicDialog(
                    "Must Update",
                    "The version of the application is out of date and cannot run. Please update to the latest version from the Play Store.",
                    requiresUpdate = true,
                    canDismiss = false
                )
            }
            DisplayState.SuggestUpdate -> {
                createBasicDialog(
                    "Should Update",
                    "The version of the application is out of date and should not run. Please update to the latest version from the Play Store.",
                    requiresUpdate = true,
                    canDismiss = true)
            }
            // todo 2021-09 Handle down for maintenance
            else -> {
                dialog?.dismiss()
                dialog = null
            }
        }

        dialog?.show()
    }

    private fun createBasicDialog(title: String,
                                  message: String,
                                  requiresUpdate: Boolean,
                                  canDismiss: Boolean) {

        val builder = AlertDialog.Builder(activity).apply {
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
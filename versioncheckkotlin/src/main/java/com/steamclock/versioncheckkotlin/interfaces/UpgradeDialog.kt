package com.steamclock.versioncheckkotlin.interfaces
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import androidx.lifecycle.*
import com.steamclock.versioncheckkotlin.models.DisplayState

interface UpgradeDialog {
    fun showDialogForState(state: DisplayState)
}

class DefaultUpgradeDialog(private val context: Context): UpgradeDialog, LifecycleObserver {

    private var dialog: Dialog? = null

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
                createBasicDialog("FORCE", "Update it!")
            }
            DisplayState.SuggestUpdate -> {
                createBasicDialog("SUGGEST", "Update it!")
            }
            else -> {
                dialog?.dismiss()
                dialog = null
            }
        }

        dialog?.show()
    }

    private fun createBasicDialog(title: String, message: String) {
        dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> }
            .create()
    }
}
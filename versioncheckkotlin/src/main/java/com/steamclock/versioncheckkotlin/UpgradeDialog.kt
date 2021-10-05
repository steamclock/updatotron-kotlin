package com.steamclock.versioncheckkotlin.interfaces

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.steamclock.versioncheckkotlin.models.DisplayState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class DefaultUpgradeDialog(private val versionDisplayState: StateFlow<DisplayState>) : Application.ActivityLifecycleCallbacks {
    private var dialog: AlertDialog? = null
    private val coroutineScope = MainScope()
    private var needToShowDialog = false
    private var currentActivityContext: WeakReference<Context>? = null

    init {
        coroutineScope.launch {
            versionDisplayState.collect { displayState ->
                when (val ctx = currentActivityContext?.get()) {
                    null -> needToShowDialog = true
                    else -> showForState(ctx, displayState)
                }
            }
        }
    }

    private fun dismissDialog() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

    private fun reshowDialog() {
        dismissDialog()
        dialog?.show()
    }

    private fun showForState(context: Context, state: DisplayState) {
        dismissDialog()
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
            is DisplayState.DevelopmentFailure -> {
                createBasicDialog(
                    context,
                    "Version Check Error",
                    state.reason,
                    requiresUpdate = false,
                    canDismiss = true
                )
            }
            is DisplayState.DownForMaintenance -> {
                createBasicDialog(
                    context,
                    "Down for Maintenance",
                    "The server is currently down for maintenance. Please check back later.",
                    requiresUpdate = false,
                    canDismiss = false
                )
            }
            // todo 2021-09 Handle down for maintenance
            else -> {
                dialog?.dismiss()
                dialog = null
            }
        }
        dialog?.show()
        needToShowDialog = false
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

    //-----------------------------------------------------------------
    // Using ActivityLifecycleCallbacks to launch version checks and collect
    // state flows.
    //-----------------------------------------------------------------
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        /* No op */
    }

    override fun onActivityStarted(activity: Activity) {
       /* No op */
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivityContext = WeakReference(activity)
        if (needToShowDialog) {
            showForState(activity, versionDisplayState.value)
        } else {
            reshowDialog()
        }
    }

    override fun onActivityPaused(activity: Activity) {
        currentActivityContext = null
        dismissDialog()
    }

    override fun onActivityStopped(activity: Activity) {
        /* No op */
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        /* No op */
    }

    override fun onActivityDestroyed(activity: Activity) {
        /* No op */
    }
}
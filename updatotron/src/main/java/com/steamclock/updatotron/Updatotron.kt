package com.steamclock.updatotron

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.steamclock.updatotron.models.DisplayState
import com.steamclock.updatotron.models.Status
import com.steamclock.updatotron.models.Version
import com.steamclock.updatotron.models.VersionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.URL

class Updatotron(private val config: UpdatotronConfig):
    LifecycleObserver {

    /**
     * Status of the version check; if upgradeDialogHandler has already been set, there isn't
     * a need to observe this directly.
     */
    private val mutableStatusFlow = MutableStateFlow<Status>(Status.Unknown)
    val statusFlow: StateFlow<Status> = mutableStatusFlow

    /**
     * Indicates the display state for the Dialog; if upgradeDialogHandler has already been
     * set, there isn't a need to observe this directly.
     */
    private val mutableDisplayStateFlow = MutableStateFlow<DisplayState>(DisplayState.Clear)
    val displayStateFlow: StateFlow<DisplayState> = mutableDisplayStateFlow

    private val coroutineScope = MainScope()

    //--------------------------------------------------
    // App LifecycleObserver Hooks - requires lifecycle annotations
    //--------------------------------------------------
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppStarted() {
        // This is called when the app is started, or when coming back from background.
        runVersionCheck()
    }

    //--------------------------------------------------
    // Public methods
    //--------------------------------------------------
    /**
     * todo 2021-09-29 Requires testing when using actual network call
     */
    fun runVersionCheck() {
        coroutineScope.launch(Dispatchers.IO) {
            val jsonStr = config.urlFetcher.getData(URL(config.url))
            if (jsonStr == null) {
                // Failed to get data from URL
                setFailure("Failed to get config json from URL")
            } else {
                validateUsingJson(jsonStr)
            }
        }
    }

    //--------------------------------------------------
    // Private methods
    //--------------------------------------------------
    private fun validateUsingJson(jsonStr: String) {
        val appVersion = try {
            config.packageDetails.getAppVersion()
        } catch (e: Exception) {
            // Failed to parse app version
            setFailure("Failed to parse app version: ${e.message}")
            null
        }
        appVersion ?: return

        try {
            validateAppVersion(config.versionDataConverter.parse(jsonStr), appVersion)
        } catch (e: Exception) {
            // Failed to parse data
            setFailure("Failed to parse config json: ${e.message}")
            return
        }
    }

    private fun validateAppVersion(serverVersionData: VersionData, appVersion: Version) {
        val androidVersionData = serverVersionData.android
        if (androidVersionData == null) {
            // Failed to parse android data
            setFailure("Config json missing androidVersionData component")
            return
        }

        val serverMinVersion = androidVersionData.minimumVersion
        if (serverMinVersion == null) {
            // Failed to parse min version
            setFailure("Config json missing serverMinVersion")
            return
        }

        when {
            serverVersionData.serverForceVersionFailure == true -> {
                // Server has been set to force update no matter the app version
                setDisallowed()
            }
            appVersion < serverMinVersion -> {
                // App version now below the server minimum
                setDisallowed()
            }
            androidVersionData.containsBlockedVersion(appVersion) -> {
                // Current app version is now blocked
                setDisallowed()
            }
            serverVersionData.serverMaintenance == true -> {
                // Server is currently undergoing maintenance
                setMaintenance()
            }
            config.packageDetails.areTestUpdatesSupported() && androidVersionData.shouldUpdateForTesting(appVersion) -> {
                // If a newer test build is available
                setTestBuild()
            }
            else -> {
                // If we get all the way down here, the version is allowed!
                setAllowed()
            }
        }
    }

    //--------------------------------------------------
    // Status and States
    //--------------------------------------------------
    private fun setDisallowed() {
        mutableStatusFlow.value = Status.VersionDisallowed
        mutableDisplayStateFlow.value = DisplayState.ForceUpdate
    }

    private fun setAllowed() {
        mutableStatusFlow.value = Status.VersionAllowed
        mutableDisplayStateFlow.value = DisplayState.Clear
    }

    private fun setFailure(devMessage: String) {
        mutableStatusFlow.value = Status.FetchFailure

        if (config.packageDetails.isDevelopmentBuild()) {
            mutableDisplayStateFlow.value = DisplayState.DevelopmentFailure(devMessage)
        } else {
            mutableDisplayStateFlow.value = DisplayState.Clear
        }
    }

    private fun setMaintenance() {
        mutableStatusFlow.value = Status.VersionAllowed
        mutableDisplayStateFlow.value = DisplayState.DownForMaintenance
    }

    private fun setTestBuild() {
        mutableStatusFlow.value = Status.VersionAllowed
        mutableDisplayStateFlow.value = DisplayState.SuggestUpdate
    }
}
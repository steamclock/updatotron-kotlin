package com.steamclock.versioncheckkotlin

import com.steamclock.versioncheckkotlin.interfaces.*
import com.steamclock.versioncheckkotlin.models.DisplayState
import com.steamclock.versioncheckkotlin.models.Status
import com.steamclock.versioncheckkotlin.models.Version
import com.steamclock.versioncheckkotlin.models.VersionData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.Exception
import java.net.URL

class VersionRepository(
    private val appVersionName: String,
    private val appVersionCode: Int,
    private val url: String,
    private val urlFetcher: URLFetcher = NetworkURLFetcher,
    private val versionDataConverter: VersionDataConverter = DefaultVersionDataConverter) {

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

    /**
     * Should always be called using Dispatchers.IO?
     * todo 2021-09-29 Requires testing when using actual network call
     */
    fun runVersionCheck() {
        val jsonStr = urlFetcher.getData(URL(url))
        if (jsonStr == null) {
            // Failed to get data from URL
            setFailure()
        } else {
            validateUsingJson(jsonStr, appVersionName, appVersionCode, versionDataConverter)
        }
    }

    /**
     * Allows us the ability to test the validation process without having to worry about
     * mocking coroutine scope required to fetch the data.
     */
    fun validateUsingJson(
        jsonStr: String,
        appVersionName: String,
        appVersionCode: Int,
        versionDataConverter: VersionDataConverter = DefaultVersionDataConverter) {

        val appVersion = try {
            Version("$appVersionName@$appVersionCode")
        } catch (e: Exception) {
            // Failed to parse app version
            setFailure()
            null
        }
        appVersion ?: return

        try {
            validateAppVersion(versionDataConverter.parse(jsonStr), appVersion)
        } catch (e: Exception) {
            // Failed to parse data
            setFailure()
            return
        }

    }

    private fun validateAppVersion(serverVersionData: VersionData, appVersion: Version) {
        // todo 2021-09-27 Add support for latestTestVersion
        val androidVersionData = serverVersionData?.android
        if (androidVersionData == null) {
            // Failed to parse android data
            setFailure()
            return
        }

        val serverMinVersion = androidVersionData.minimumVersion
        if (serverMinVersion == null) {
            // Failed to parse min version
            setFailure()
            return
        }

        when {
            appVersion < serverMinVersion -> {
                // App version now below the server minimum
                setDisallowed()
            }
            androidVersionData.containsBlockedVersion(appVersion) -> {
                // Current app version is now blocked
                setDisallowed()
            }
            else -> {
                // If we get all the way down here, the version is allowed!
                setAllowed()
            }
        }
    }

    // todo possibly use externalScope to emit in a couroutine scope

    private fun setDisallowed() {
        mutableStatusFlow.value = Status.VersionDisallowed
        // todo Currently always ForceUpdate, add logic to determine when shouldUpdate is required
        // If dialog handler set, have it handle the state immediately
        mutableDisplayStateFlow.value = DisplayState.ForceUpdate
    }

    private fun setAllowed() {
        mutableStatusFlow.value = Status.VersionAllowed
        mutableDisplayStateFlow.value = DisplayState.Clear
    }

    private fun setFailure() {
        mutableStatusFlow.value = Status.FetchFailure
        mutableDisplayStateFlow.value = DisplayState.Clear
    }
}
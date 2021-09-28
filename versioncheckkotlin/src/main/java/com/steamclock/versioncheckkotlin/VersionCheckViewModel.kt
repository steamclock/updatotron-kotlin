package com.steamclock.versioncheckkotlin

import androidx.lifecycle.*
import com.steamclock.versioncheckkotlin.interfaces.*
import com.steamclock.versioncheckkotlin.models.DisplayState
import com.steamclock.versioncheckkotlin.models.Status
import com.steamclock.versioncheckkotlin.models.Version
import com.steamclock.versioncheckkotlin.models.VersionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.URL

class VersionCheckViewModel: ViewModel() {

    /**
     * Status of the version check; if upgradeDialogHandler has already been set, there isn't
     * a need to observe this directly.
     */
    private val mutableStatus = MutableLiveData<Status>()
    val status = mutableStatus as LiveData<Status>

    /**
     * Indicates the display state for the Dialog; if upgradeDialogHandler has already been
     * set, there isn't a need to observe this directly.
     */
    private val mutableDisplayState = MutableLiveData<DisplayState>()
    val displayState = mutableDisplayState as LiveData<DisplayState>

    /**
     * If set, this interface will be notified with any DisplayState changes that have occurred
     * during the version check.
     */
    private var upgradeDialogHandler: UpgradeDialog? = null

    /**
     * Either passed in directly via validateUsingJson, or is the result of running the
     * URLFetcher with the given URL.
     */
    private var serverVersionData: VersionData? = null

    init {
        mutableStatus.value = Status.Unknown
        mutableDisplayState.value = DisplayState.Clear
    }

    fun runVersionCheck(appVersionName: String,
                        appVersionCode: String,
                        url: String,
                        urlFetcher: URLFetcher = NetworkURLFetcher,
                        versionDataConverter: VersionDataConverter = DefaultVersionDataConverter,
                        upgradeDialog: UpgradeDialog? = null) {

        upgradeDialogHandler = upgradeDialog
        viewModelScope.launch(Dispatchers.IO) {
            val jsonStr = urlFetcher.getData(URL(url))
            withContext(Dispatchers.Main) {
                if (jsonStr == null) {
                    // Failed to get data from URL
                    setFailure()
                } else {
                    validateUsingJson(jsonStr, appVersionName, appVersionCode, versionDataConverter)
                }

            }
        }
    }

    /**
     * Allows us the ability to test the validation process without having to worry about
     * mocking coroutine scope required to fetch the data.
     */
    fun validateUsingJson(
        jsonStr: String,
        appVersionName: String,
        appVersionCode: String,
        versionDataConverter: VersionDataConverter = DefaultVersionDataConverter) {

        try {
            serverVersionData = versionDataConverter.parse(jsonStr)
        } catch (e: Exception) {
            // Failed to parse data
            setFailure()
            return
        }

        try {
            val appVersion = Version("$appVersionName@$appVersionCode")
            validateAppVersion(appVersion)
        } catch (e: Exception) {
            // Failed to parse app version
            setFailure()
            return
        }
    }

    private fun validateAppVersion(appVersion: Version) {
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

    private fun setDisallowed() {
        mutableStatus.postValue(Status.VersionDisallowed)

        // todo Currently always ForceUpdate, add logic to determine when shouldUpdate is required
        // If dialog handler set, have it handle the state immediately
        val newState = DisplayState.ForceUpdate
        mutableDisplayState.postValue(newState)
        upgradeDialogHandler?.showDialogForState(newState)
    }

    private fun setAllowed() {
        mutableStatus.postValue(Status.VersionAllowed)
        val newState = DisplayState.Clear
        mutableDisplayState.postValue(newState)
        upgradeDialogHandler?.showDialogForState(newState)
    }

    private fun setFailure() {
        mutableStatus.postValue(Status.FetchFailure)
        val newState = DisplayState.Clear
        mutableDisplayState.postValue(newState)
        upgradeDialogHandler?.showDialogForState(newState)
    }
}
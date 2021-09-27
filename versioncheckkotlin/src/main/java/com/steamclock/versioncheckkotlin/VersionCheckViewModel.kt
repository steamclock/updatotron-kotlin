package com.steamclock.versioncheckkotlin

import androidx.lifecycle.*
import com.steamclock.versioncheckkotlin.interfaces.DefaultVersionDataConverter
import com.steamclock.versioncheckkotlin.interfaces.NetworkURLFetcher
import com.steamclock.versioncheckkotlin.interfaces.URLFetcher
import com.steamclock.versioncheckkotlin.interfaces.VersionDataConverter
import com.steamclock.versioncheckkotlin.models.DisplayState
import com.steamclock.versioncheckkotlin.models.Status
import com.steamclock.versioncheckkotlin.models.Version
import com.steamclock.versioncheckkotlin.models.VersionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.URL

class VersionCheckViewModel: ViewModel() {

    private val mutableStatus = MutableLiveData<Status>()
    val status = mutableStatus as LiveData<Status>

    // todo add displayState
    //private val mutableDisplayState = MutableLiveData<DisplayState>()
    //val displayState = mutableDisplayState as LiveData<DisplayState>

    private var serverVersionData: VersionData? = null

    init {
        mutableStatus.value = Status.Unknown
        //mutableDisplayState.value = DisplayState.Clear
    }

    fun runVersionCheck(url: String,
                        appVersionName: String,
                        appVersionCode: String,
                        urlFetcher: URLFetcher = NetworkURLFetcher,
                        versionDataConverter: VersionDataConverter = DefaultVersionDataConverter) {

        viewModelScope.launch(Dispatchers.IO) {
            val jsonStr = urlFetcher.getData(URL(url))
            if (jsonStr == null) {
                // Failed to get data from URL
                mutableStatus.postValue(Status.FetchFailure)
                return@launch
            }
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
        appVersionCode: String,
        versionDataConverter: VersionDataConverter = DefaultVersionDataConverter) {

        try {
            serverVersionData = versionDataConverter.parse(jsonStr)
        } catch (e: Exception) {
            // Failed to parse data
            mutableStatus.postValue(Status.FetchFailure)
        }

        try {
            val appVersion = Version("$appVersionName@$appVersionCode")
            validateAppVersion(appVersion)
        } catch (e: Exception) {
            // Failed to parse app version
            mutableStatus.postValue(Status.FetchFailure)
        }
    }

    private fun validateAppVersion(appVersion: Version) {
        // todo 2021-09-27 Add support for latestTestVersion
        val serverMinVersion = serverVersionData?.android?.minimumVersion ?: run {
            mutableStatus.postValue(Status.FetchFailure)
            return
        }

        if (appVersion < serverMinVersion) {
            // App version now below the server minimum
            mutableStatus.postValue(Status.VersionDisallowed)
            return
        }

        serverVersionData?.android?.blockedVersions?.let { blockedSet ->
            if (blockedSet.contains(appVersion)) {
                // App version has been flagged as being a blocked version
                mutableStatus.postValue(Status.VersionDisallowed)
                return
            }
        }

        // If we get all the way down here, the version is allowed!
        mutableStatus.postValue(Status.VersionAllowed)
    }
}
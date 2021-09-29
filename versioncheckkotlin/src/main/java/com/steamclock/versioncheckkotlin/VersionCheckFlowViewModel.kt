package com.steamclock.versioncheckkotlin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steamclock.versioncheckkotlin.interfaces.UpgradeDialogOld
import com.steamclock.versioncheckkotlin.models.DisplayState
import com.steamclock.versioncheckkotlin.models.Status
import com.steamclock.versioncheckkotlin.models.VersionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class VersionCheckFlowViewModel(
    private val versionRepository: VersionRepository
): ViewModel() {

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
    private var upgradeDialogHandler: UpgradeDialogOld? = null

    /**
     * Either passed in directly via validateUsingJson, or is the result of running the
     * URLFetcher with the given URL.
     */
    private var serverVersionData: VersionData? = null

    init {
        viewModelScope.launch {
            versionRepository.statusFlow.collect {
                mutableStatus.postValue(it)
            }

            versionRepository.displayStateFlow.collect {
                mutableDisplayState.postValue(it)
            }
        }
    }

    fun runVersionCheck(upgradeDialog: UpgradeDialogOld? = null) {
        upgradeDialogHandler = upgradeDialog
        viewModelScope.launch(Dispatchers.IO) {
            versionRepository.runVersionCheck()
        }
    }
}
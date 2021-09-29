package com.steamclock.versioncheckkotlin

import androidx.lifecycle.*
import com.steamclock.versioncheckkotlin.models.DisplayState
import com.steamclock.versioncheckkotlin.models.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class VersionCheckViewModel(
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

    fun runVersionCheck() {
        viewModelScope.launch(Dispatchers.IO) {
            versionRepository.runVersionCheck()
        }
    }
}

class VersionCheckViewModelFactory(private val versionRepository: VersionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VersionCheckViewModel::class.java)) {
            return VersionCheckViewModel(versionRepository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}
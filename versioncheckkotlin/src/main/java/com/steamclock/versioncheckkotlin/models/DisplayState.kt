package com.steamclock.versioncheckkotlin.models

sealed class DisplayState {
    object Clear: DisplayState()
    object SuggestUpdate: DisplayState()
    object ForceUpdate: DisplayState()
    object DownForMaintenance: DisplayState()
    class DevelopmentFailure(reason: String): DisplayState()
}
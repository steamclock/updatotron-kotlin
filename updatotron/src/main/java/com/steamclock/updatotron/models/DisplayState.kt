package com.steamclock.updatotron.models

sealed class DisplayState {
    object Clear: DisplayState()
    object SuggestUpdate: DisplayState()
    object ForceUpdate: DisplayState()
    object DownForMaintenance: DisplayState()
    class DevelopmentFailure(val reason: String): DisplayState()
}
package com.steamclock.updatotron.models

sealed class Status {
    object Unknown: Status()
    object FetchFailure: Status()
    object VersionAllowed: Status()
    object VersionDisallowed: Status()
}
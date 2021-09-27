package com.steamclock.versioncheckkotlin.models

data class PlatformVersionData(
    val minimumVersion: Version?,
    val blockedVersions: Set<Version>?,
    val latestTestVersion: Version?
)

data class VersionData(
    val android: PlatformVersionData?,
    val serverForceVersionFailure: Boolean?,
    val serverMaintenance: Boolean?
)
package com.steamclock.versioncheckkotlin.models

data class PlatformVersionData(
    val minimumVersion: Version?,
    val blockedVersions: Set<Version>?,
    val latestTestVersion: Version?) {

    /**
     * Returns true if the given version is contained in the BlockedVersions set.
     * Note, this only takes into account marketing version, not build number.
     * todo Check if this assumption is actually true
     */
    fun containsBlockedVersion(other: Version): Boolean {
        if (blockedVersions == null) return false
        if (blockedVersions.any { it.marketingComponentsEqual(other) }) return true
        return false
    }
}

data class VersionData(
    val android: PlatformVersionData?,
    val serverForceVersionFailure: Boolean?,
    val serverMaintenance: Boolean?
)
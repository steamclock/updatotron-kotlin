package com.steamclock.updatotron.models

data class PlatformVersionData(
    val minimumVersion: Version?,
    val blockedVersions: Set<Version>?,
    val latestTestVersion: Version?) {

    /**
     * Returns true if the given version is contained in the BlockedVersions set.
     * If the blocked version contains a build number, then the compared version must match
     * that build number directly. If the blocked version ONLY contains a build number, then
     * only the build number is taken into account.
     */
    fun containsBlockedVersion(other: Version): Boolean {
        if (blockedVersions == null) return false

        return blockedVersions.any { blocked ->
            val marketingEqual = blocked.marketingComponentsEqual(other)
            val buildEqual = blocked.build != null && blocked.build == other.build
            when {
                blocked.build == null -> {
                    // Blocked item is marketing components only (ex. 2.3.4)
                    marketingEqual
                }
                blocked.marketingComponents.isEmpty() -> {
                    // Blocked item is only a build number (ex. @300)
                    buildEqual
                }
                else -> {
                    // Blocked item contains both components (ex. 2.3.4@300)
                    marketingEqual && buildEqual
                }
            }
        }
    }

    fun shouldUpdateForTesting(other: Version): Boolean {
        return if (latestTestVersion == null) {
            false
        } else {
            other < latestTestVersion
        }
    }
}

data class VersionData(
    val android: PlatformVersionData?,
    val serverForceVersionFailure: Boolean?,
    val serverMaintenance: Boolean?
)
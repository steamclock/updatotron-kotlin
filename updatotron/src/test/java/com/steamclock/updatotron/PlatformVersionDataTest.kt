package com.steamclock.updatotron

import com.steamclock.updatotron.models.PlatformVersionData
import com.steamclock.updatotron.models.Version
import org.junit.Test
import org.junit.Assert.*

class PlatformVersionDataTest {

    private val platformNoBlocked = PlatformVersionData(
        minimumVersion = Version("1.2.0"),
        blockedVersions = null,
        latestTestVersion = null)

    private val platformMarketingBlocked = PlatformVersionData(
        minimumVersion = Version("1.2.0"),
        blockedVersions = setOf(Version("2.2.1"), Version("1.3")),
        latestTestVersion = null)

    private val platformBuildBlocked = PlatformVersionData(
        minimumVersion = Version("1.2.0"),
        blockedVersions = setOf(Version("@400"), Version("2.2.1@300"), Version("2.3@900")),
        latestTestVersion = null)

    private val latestTestVersion = PlatformVersionData(
        minimumVersion = Version("1.2.0"),
        blockedVersions = setOf(),
        latestTestVersion = Version("1.4"))

    @Test
    fun testContainsBlockedVersion() {
        val versionNotBlocked = Version("1.2.3@900")
        val versionNotBlocked2 = Version("2.3@1000")
        val marketingBlocked = Version("1.3@600")
        val buildBlocked = Version("1.2.3@400")
        val marketingAndBuildBlocked = Version("2.2.1@300")

        val shouldBeTrue = listOf(
            Pair(platformMarketingBlocked, marketingAndBuildBlocked),
            Pair(platformBuildBlocked, marketingAndBuildBlocked),
            Pair(platformBuildBlocked, buildBlocked),
            Pair(platformBuildBlocked, buildBlocked)
        )

        val shouldNotFalse = listOf(
            // No block list, all of these should succeed
            Pair(platformNoBlocked, versionNotBlocked),
            Pair(platformNoBlocked, marketingBlocked),
            Pair(platformNoBlocked, buildBlocked),
            Pair(platformNoBlocked, marketingAndBuildBlocked),
            // These should not be in any of the blocked lists
            Pair(platformBuildBlocked, versionNotBlocked),
            Pair(platformBuildBlocked, versionNotBlocked2),
            Pair(platformMarketingBlocked, versionNotBlocked),
            Pair(platformMarketingBlocked, versionNotBlocked2)
        )

        shouldBeTrue.forEach { assertTrue(it.first.containsBlockedVersion(it.second)) }
        shouldNotFalse.forEach { assertFalse(it.first.containsBlockedVersion(it.second)) }
    }

    @Test
    fun testShouldUpdateForTesting() {
        assertTrue(latestTestVersion.shouldUpdateForTesting(Version("1.2.2@200")))
        assertTrue(latestTestVersion.shouldUpdateForTesting(Version("1.3@400")))
        assertFalse(latestTestVersion.shouldUpdateForTesting(Version("1.4.0@600")))
        assertFalse(latestTestVersion.shouldUpdateForTesting(Version("2.4.0@800")))
    }
}
package com.steamclock.updatotron

import com.steamclock.updatotron.interfaces.DefaultVersionDataConverter
import com.steamclock.updatotron.utils.TestConstants
import org.junit.Test
import org.junit.Assert.*

class DefaultVersionDataConverterTest {

    /**
     * Simple test to ensure that the manual JSON parsing done by DefaultVersionDataConverter
     * produces a correct parse.
     */
    @Test
    fun basicParse() {
        val versionData = DefaultVersionDataConverter.parse(TestConstants.MockJson.validVersionDataJson)

        assertNotNull(versionData.android)
        val androidVersion = versionData.android ?: return

        assertNotNull("minimumVersion should not be null", androidVersion.minimumVersion)
        val minVersion = androidVersion.minimumVersion ?: return
        assertTrue("minimumVersion failed to parse correctly",minVersion.description == "1.1")

        assertNotNull("blockedVersions should not be null", androidVersion.blockedVersions)
        val blockedVersions = androidVersion.blockedVersions ?: return
        assertTrue("blockedVersions size is incorrect or failed to parse",blockedVersions.size == 3)
        // todo find nice way to check if set contains a string (right now these are full Versions)

        assertNotNull("latestTestVersion should not be null", androidVersion.latestTestVersion)
        val latestTestVersion = androidVersion.latestTestVersion ?: return
        assertTrue("latestTestVersion failed to parse correctly", latestTestVersion.description == "1.4.2@400")

        assertTrue("serverForceVersionFailure failed to parse correctly", versionData.serverForceVersionFailure == false)
        assertTrue("serverMaintenance failed to parse correctly", versionData.serverMaintenance == false)
    }
}
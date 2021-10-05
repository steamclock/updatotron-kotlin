package com.steamclock.versioncheckkotlin

import app.cash.turbine.test
import com.steamclock.versioncheckkotlin.models.DisplayState
import com.steamclock.versioncheckkotlin.models.Status
import com.steamclock.versioncheckkotlin.utils.TestConstants
import com.steamclock.versioncheckkotlin.utils.confirmLastEmit
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.time.ExperimentalTime
import org.junit.Assert.*

/**
 * Using Turbine library to make testing hot flows (state flows) more simple.
 * Requires ExperimentalTime annotation for now.
 */
@ExperimentalTime
class VersionCheckTest {

    // Since the config changes per test, we do not set this up in @Before.
    private lateinit var versionCheck: VersionCheck

    // Before check run
    @Test
    fun `Status set to Unknown before check is run`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.validApp)
        versionCheck.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to Clear before check is run`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.validApp)
        versionCheck.displayStateFlow.test {
            assertEquals(DisplayState.Clear, awaitItem())
            confirmLastEmit()
        }
    }

    /**
     * On version OK
     */
    @Test
    fun `Status set to VersionAllowed when app version is ok`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.validApp)
        versionCheck.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            versionCheck.runVersionCheck()
            assertEquals(Status.VersionAllowed, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to Clear (only once) when app version is ok`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.validApp)
        versionCheck.displayStateFlow.test {
            assertEquals(DisplayState.Clear, awaitItem())
            versionCheck.runVersionCheck()
            // DisplayState should not get updated/emit
            confirmLastEmit()
        }
    }

    /**
     * When app version too old
     */
    @Test
    fun `Status set to VersionDisallowed when app version older`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.appOldVersion)
        versionCheck.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            versionCheck.runVersionCheck()
            assertEquals(Status.VersionDisallowed, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to ForceUpdate when app version older`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.appOldVersion)
        versionCheck.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            versionCheck.runVersionCheck()
            assertEquals(awaitItem(), DisplayState.ForceUpdate)
            confirmLastEmit()
        }
    }

    /**
     * When app version is blocked
     */
    @Test
    fun `Status set to VersionDisallowed when app version is blocked`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.appVersionBlocked)
        versionCheck.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            versionCheck.runVersionCheck()
            assertEquals(Status.VersionDisallowed, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to ForceUpdate when app version is blocked`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.appVersionBlocked)
        versionCheck.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            versionCheck.runVersionCheck()
            assertEquals(awaitItem(), DisplayState.ForceUpdate)
            confirmLastEmit()
        }
    }

    /**
     * Json malformed
     */
    @Test
    fun `Status set to FetchFailure when json is malformed`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.jsonMalformed)
        versionCheck.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            versionCheck.runVersionCheck()
            assertEquals(Status.FetchFailure, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to Clear (only once) when json is malformed`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.jsonMalformed)
        versionCheck.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            versionCheck.runVersionCheck()
            // DisplayState should not get updated/emit
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to DevelopmentFailure when json is malformed and is development build`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.jsonMalformedDevelopmentBuild)
        versionCheck.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            versionCheck.runVersionCheck()
            assertTrue(awaitItem() is DisplayState.DevelopmentFailure)
            confirmLastEmit()
        }
    }

    /**
     * Json valid, but missing Android data
     */
    @Test
    fun `Status set to FetchFailure when json missing android property`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.jsonMissingAndroid)
        versionCheck.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            versionCheck.runVersionCheck()
            assertEquals(Status.FetchFailure, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to Clear (only once) when json missing android property`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.jsonMissingAndroid)
        versionCheck.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            versionCheck.runVersionCheck()
            // DisplayState should not get updated/emit
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to DevelopmentFailure when json missing android property and is development build`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.jsonMissingAndroidDevelopmentBuild)
        versionCheck.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            versionCheck.runVersionCheck()
            assertTrue(awaitItem() is DisplayState.DevelopmentFailure)
            confirmLastEmit()
        }
    }

    /**
     * LatestTest Available
     */
    @Test
    fun `Status set to VersionAllowed when latestTestVersion available and app side loaded`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.latestTestVersionAvailable)
        versionCheck.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            versionCheck.runVersionCheck()
            assertEquals(Status.VersionAllowed, awaitItem())
            confirmLastEmit()
        }
    }

    // todo need to flip assertEquals prop order

    @Test
    fun `DisplayState set to SuggestUpdate when latestTestVersion available and app side loaded`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.latestTestVersionAvailable)
        versionCheck.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            versionCheck.runVersionCheck()
            assertEquals(awaitItem(),DisplayState.SuggestUpdate)
            confirmLastEmit()
        }
    }

    /**
     * LatestTest Not Applicable
     */
    @Test
    fun `Status set to VersionAllowed when latestTestVersion available but not applicable`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.latestTestVersionNotApplicable)
        versionCheck.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            versionCheck.runVersionCheck()
            assertEquals(Status.VersionAllowed, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to Clear (only once) when latestTestVersion available but not applicable`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.latestTestVersionNotApplicable)
        versionCheck.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            versionCheck.runVersionCheck()
            // DisplayState should not get updated/emit
            confirmLastEmit()
        }
    }

    /**
     * Server Maintenance
     */
    @Test
    fun `Status still set to VersionAllowed when the server is down for maintenance`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.serverMaintenanceActive)
        versionCheck.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            versionCheck.runVersionCheck()
            assertEquals(Status.VersionAllowed, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to DownForMaintenance when the server is down for maintenance`() = runBlocking {
        versionCheck = VersionCheck(TestConstants.VersionCheckConfig.serverMaintenanceActive)
        versionCheck.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            versionCheck.runVersionCheck()
            assertEquals(awaitItem(),DisplayState.DownForMaintenance)
            confirmLastEmit()
        }
    }

}
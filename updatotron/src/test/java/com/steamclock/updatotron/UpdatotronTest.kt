package com.steamclock.updatotron

import app.cash.turbine.test
import com.steamclock.updatotron.models.DisplayState
import com.steamclock.updatotron.models.Status
import com.steamclock.updatotron.utils.TestConstants
import com.steamclock.updatotron.utils.confirmLastEmit
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.time.ExperimentalTime
import org.junit.Assert.*

/**
 * Using Turbine library to make testing hot flows (state flows) more simple.
 * Requires ExperimentalTime annotation for now.
 */
@ExperimentalTime
class UpdatotronTest {

    // Since the config changes per test, we do not set this up in @Before.
    private lateinit var updatotron: Updatotron

    // Before check run
    @Test
    fun `Status set to Unknown before check is run`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.validApp)
        updatotron.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to Clear before check is run`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.validApp)
        updatotron.displayStateFlow.test {
            assertEquals(DisplayState.Clear, awaitItem())
            confirmLastEmit()
        }
    }

    /**
     * On version OK
     */
    @Test
    fun `Status set to VersionAllowed when app version is ok`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.validApp)
        updatotron.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            updatotron.runVersionCheck()
            assertEquals(Status.VersionAllowed, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to Clear (only once) when app version is ok`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.validApp)
        updatotron.displayStateFlow.test {
            assertEquals(DisplayState.Clear, awaitItem())
            updatotron.runVersionCheck()
            // DisplayState should not get updated/emit
            confirmLastEmit()
        }
    }

    /**
     * When app version too old
     */
    @Test
    fun `Status set to VersionDisallowed when app version older`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.appOldVersion)
        updatotron.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            updatotron.runVersionCheck()
            assertEquals(Status.VersionDisallowed, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to ForceUpdate when app version older`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.appOldVersion)
        updatotron.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            updatotron.runVersionCheck()
            assertEquals(awaitItem(), DisplayState.ForceUpdate)
            confirmLastEmit()
        }
    }

    /**
     * When app version is blocked
     */
    @Test
    fun `Status set to VersionDisallowed when app version is blocked`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.appVersionBlocked)
        updatotron.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            updatotron.runVersionCheck()
            assertEquals(Status.VersionDisallowed, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to ForceUpdate when app version is blocked`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.appVersionBlocked)
        updatotron.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            updatotron.runVersionCheck()
            assertEquals(awaitItem(), DisplayState.ForceUpdate)
            confirmLastEmit()
        }
    }

    /**
     * Json malformed
     */
    @Test
    fun `Status set to FetchFailure when json is malformed`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.jsonMalformed)
        updatotron.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            updatotron.runVersionCheck()
            assertEquals(Status.FetchFailure, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to Clear (only once) when json is malformed`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.jsonMalformed)
        updatotron.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            updatotron.runVersionCheck()
            // DisplayState should not get updated/emit
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to DevelopmentFailure when json is malformed and is development build`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.jsonMalformedDevelopmentBuild)
        updatotron.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            updatotron.runVersionCheck()
            assertTrue(awaitItem() is DisplayState.DevelopmentFailure)
            confirmLastEmit()
        }
    }

    /**
     * Json valid, but missing Android data
     */
    @Test
    fun `Status set to FetchFailure when json missing android property`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.jsonMissingAndroid)
        updatotron.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            updatotron.runVersionCheck()
            assertEquals(Status.FetchFailure, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to Clear (only once) when json missing android property`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.jsonMissingAndroid)
        updatotron.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            updatotron.runVersionCheck()
            // DisplayState should not get updated/emit
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to DevelopmentFailure when json missing android property and is development build`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.jsonMissingAndroidDevelopmentBuild)
        updatotron.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            updatotron.runVersionCheck()
            assertTrue(awaitItem() is DisplayState.DevelopmentFailure)
            confirmLastEmit()
        }
    }

    /**
     * LatestTest Available
     */
    @Test
    fun `Status set to VersionAllowed when latestTestVersion available and app side loaded`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.latestTestVersionAvailable)
        updatotron.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            updatotron.runVersionCheck()
            assertEquals(Status.VersionAllowed, awaitItem())
            confirmLastEmit()
        }
    }

    // todo need to flip assertEquals prop order

    @Test
    fun `DisplayState set to SuggestUpdate when latestTestVersion available and app side loaded`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.latestTestVersionAvailable)
        updatotron.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            updatotron.runVersionCheck()
            assertEquals(awaitItem(),DisplayState.SuggestUpdate)
            confirmLastEmit()
        }
    }

    /**
     * LatestTest Not Applicable
     */
    @Test
    fun `Status set to VersionAllowed when latestTestVersion available but not applicable`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.latestTestVersionNotApplicable)
        updatotron.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            updatotron.runVersionCheck()
            assertEquals(Status.VersionAllowed, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to Clear (only once) when latestTestVersion available but not applicable`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.latestTestVersionNotApplicable)
        updatotron.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            updatotron.runVersionCheck()
            // DisplayState should not get updated/emit
            confirmLastEmit()
        }
    }

    /**
     * Server Maintenance
     */
    @Test
    fun `Status still set to VersionAllowed when the server is down for maintenance`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.serverMaintenanceActive)
        updatotron.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            updatotron.runVersionCheck()
            assertEquals(Status.VersionAllowed, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to DownForMaintenance when the server is down for maintenance`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.serverMaintenanceActive)
        updatotron.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            updatotron.runVersionCheck()
            assertEquals(awaitItem(),DisplayState.DownForMaintenance)
            confirmLastEmit()
        }
    }

    /**
     * Server Force Version Update
     */
    @Test
    fun `Status still set to VersionDisallowed when the serverForceVersionFailure flag is active`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.serverForceVersionFailureActive)
        updatotron.statusFlow.test {
            assertEquals(Status.Unknown, awaitItem())
            updatotron.runVersionCheck()
            assertEquals(Status.VersionDisallowed, awaitItem())
            confirmLastEmit()
        }
    }

    @Test
    fun `DisplayState set to ForceUpdate when the serverForceVersionFailure flag is active`() = runBlocking {
        updatotron = Updatotron(TestConstants.Configs.serverForceVersionFailureActive)
        updatotron.displayStateFlow.test {
            assertEquals(awaitItem(),DisplayState.Clear)
            updatotron.runVersionCheck()
            assertEquals(awaitItem(),DisplayState.ForceUpdate)
            confirmLastEmit()
        }
    }


}
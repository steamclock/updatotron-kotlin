import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.steamclock.versioncheckkotlin.utils.TestConstants
import com.steamclock.versioncheckkotlin.VersionCheckViewModel
import com.steamclock.versioncheckkotlin.models.Status
import com.steamclock.versioncheckkotlin.utils.getOrAwaitValue
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class VersionCheckTest : TestCase() {

    // Executes each task synchronously using Architecture Components.
    // Allows us to wait for the next post to a LiveData object.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Test properties
    private val mockAppName = "1.1"
    private val mockAppNameShouldFail = "1.0"
    private val mockAppBuild = "400"
    private lateinit var versionCheckVM: VersionCheckViewModel

    @Before
    fun setup() {
        versionCheckVM = VersionCheckViewModel()
    }

    @Test
    fun `Status set to Unknown when ViewModel is initialized`() {
        val status: Status = versionCheckVM.status.getOrAwaitValue()
        assertTrue(status == Status.Unknown)
    }

    @Test
    fun `Status set to Allowed when app version is ok`() {
        versionCheckVM.validateUsingJson(
            TestConstants.validVersionDataJson,
            mockAppName,
            mockAppBuild
        )
        val status: Status = versionCheckVM.status.getOrAwaitValue()
        assertTrue(status == Status.VersionAllowed)
    }

    @Test
    fun `Status set to Disallowed when app version older`() {
        versionCheckVM.validateUsingJson(
            TestConstants.validVersionDataJson,
            mockAppNameShouldFail,
            mockAppBuild
        )
        val status: Status = versionCheckVM.status.getOrAwaitValue()
        assertTrue(status == Status.VersionDisallowed)
    }

    @Test
    fun `Status set to FetchFailure when json is malformed`() {
        versionCheckVM.validateUsingJson(
            TestConstants.malformedJson,
            mockAppName,
            mockAppBuild
        )
        val status: Status = versionCheckVM.status.getOrAwaitValue()
        assertTrue(status == Status.FetchFailure)
    }

    @Test
    fun `Status set to FetchFailure when json missing android property`() {
        versionCheckVM.validateUsingJson(
            TestConstants.invalidVersionDataJson,
            mockAppName,
            mockAppBuild
        )
        val status: Status = versionCheckVM.status.getOrAwaitValue()
        assertTrue(status == Status.FetchFailure)
    }
}
package com.steamclock.versioncheckkotlinsample

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.steamclock.versioncheckkotlin.DefaultUpgradeDialog
import com.steamclock.versioncheckkotlin.VersionCheck
import com.steamclock.versioncheckkotlin.VersionCheckConfig
import com.steamclock.versioncheckkotlin.interfaces.DefaultPackageDetails
import com.steamclock.versioncheckkotlin.interfaces.URLFetcher
import java.net.URL

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        setupVersionCheck()
    }

    private fun setupVersionCheck() {
        val versionChecker = VersionCheck(
            VersionCheckConfig(
                packageDetails = DefaultPackageDetails(
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE,
                    packageManager,
                    packageName),
                url = "https://doesn't_matter",
                urlFetcher = MockURLFetcher
            )
        )
        val upgradeDialog = DefaultUpgradeDialog(versionChecker.displayStateFlow)

        ProcessLifecycleOwner.get().lifecycle.addObserver(versionChecker)
        registerActivityLifecycleCallbacks(upgradeDialog)
    }
}

object MockURLFetcher: URLFetcher {
    override fun getData(url: URL): String? {
        return """
                    {
                        "ios" : {
                            "minimumVersion": "1.1",
                            "blockedVersions": ["1.2.0", "1.2.1", "@301"],
                            "latestTestVersion": "1.4.2@400"
                        },
                        "android" : {
                            "minimumVersion": "1.1",
                            "blockedVersions": ["1.2.0", "1.2.1", "@301"],
                            "latestTestVersion": "1.4.2@400"
                        },
                        "serverForceVersionFailure": false,
                        "serverMaintenance": false
                    }
                    """
    }
}
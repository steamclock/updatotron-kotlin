package com.steamclock.versioncheckkotlinsample

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.steamclock.versioncheckkotlin.VersionCheck
import com.steamclock.versioncheckkotlin.VersionCheckConfig
import com.steamclock.versioncheckkotlin.interfaces.DefaultPackageDetails
import com.steamclock.versioncheckkotlin.interfaces.DefaultUpgradeDialog
import com.steamclock.versioncheckkotlin.interfaces.URLFetcher
import kotlinx.coroutines.*
import java.net.URL
import kotlin.random.Random

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        setupVersionCheck()
    }

    private fun setupVersionCheck() {
        val versionChecker = VersionCheck(
            VersionCheckConfig(
                appVersionName = BuildConfig.VERSION_NAME,
                appVersionCode = BuildConfig.VERSION_CODE,
                url = "https://doesn't_matter",
                urlFetcher = MockURLFetcher,
                packageDetails = DefaultPackageDetails(packageManager, packageName)
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
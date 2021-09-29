package com.steamclock.versioncheckkotlinsample

import android.app.Application
import com.steamclock.versioncheckkotlin.VersionChecker
import com.steamclock.versioncheckkotlin.VersionRepository
import com.steamclock.versioncheckkotlin.interfaces.DefaultUpgradeDialog
import kotlinx.coroutines.*

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        setupVersionCheck()
    }

    private fun setupVersionCheck() {
        val versionChecker = VersionChecker(
            VersionRepository(
                appVersionName = BuildConfig.VERSION_NAME,
                appVersionCode = BuildConfig.VERSION_CODE,
                url = "https://doesn't_matter",
                urlFetcher = MainActivity.MockURLFetcher
            ),
            MainScope(),
            DefaultUpgradeDialog()
        )
        registerActivityLifecycleCallbacks(versionChecker)
    }
}
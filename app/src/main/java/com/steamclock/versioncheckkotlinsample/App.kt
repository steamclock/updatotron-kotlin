package com.steamclock.versioncheckkotlinsample

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.widget.Toast
import com.steamclock.versioncheckkotlin.VersionCheck
import com.steamclock.versioncheckkotlin.VersionRepository
import com.steamclock.versioncheckkotlin.interfaces.DefaultUpgradeDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        setupVersionCheck()
    }

    private fun setupVersionCheck() {
        val versionChecker = VersionCheck(
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
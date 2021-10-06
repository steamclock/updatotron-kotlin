package com.steamclock.updatotron.utils

import com.steamclock.updatotron.interfaces.PackageDetails
import com.steamclock.updatotron.models.Version

object MockPackageDetails {
    class IsDevelopmentBuild(private val appVersionName: String, private val appVersionCode: Int): PackageDetails {
        override fun getAppVersion() = Version(appVersionName, appVersionCode)
        override fun isDevelopmentBuild() = true
        override fun areTestUpdatesSupported() = false
    }

    class IsNotDevelopmentBuild(private val appVersionName: String, private val appVersionCode: Int): PackageDetails {
        override fun getAppVersion() = Version(appVersionName, appVersionCode)
        override fun isDevelopmentBuild() = false
        override fun areTestUpdatesSupported() = false
    }

    class TestUpdatesSupported(private val appVersionName: String, private val appVersionCode: Int): PackageDetails {
        override fun getAppVersion() = Version(appVersionName, appVersionCode)
        override fun isDevelopmentBuild() = false
        override fun areTestUpdatesSupported() = true
    }

    class TestUpdatesNotSupported(private val appVersionName: String, private val appVersionCode: Int): PackageDetails {
        override fun getAppVersion() = Version(appVersionName, appVersionCode)
        override fun isDevelopmentBuild() = false
        override fun areTestUpdatesSupported() = false
    }
}
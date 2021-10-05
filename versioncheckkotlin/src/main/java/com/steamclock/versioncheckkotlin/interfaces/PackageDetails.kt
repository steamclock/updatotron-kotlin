package com.steamclock.versioncheckkotlin.interfaces

import android.content.pm.PackageManager
import android.os.Build
import com.steamclock.versioncheckkotlin.models.Version

interface PackageDetails {
    fun getAppVersion(): Version
    fun isDevelopmentBuild(): Boolean
    fun areTestUpdatesSupported(): Boolean
}

@Suppress("DEPRECATION")
class DefaultPackageDetails(
    private val appVersionName: String,
    private val appVersionCode: Int,
    private val packageManager: PackageManager,
    private val packageName: String): PackageDetails {

    override fun getAppVersion() = Version(appVersionName, appVersionCode)

    override fun isDevelopmentBuild(): Boolean {
        return true
    }

    /**
     * By default we assume that all apps that do not come from an official installer are "side
     * loaded" and that side loaded apps are able to receive test updates.
     */
    override fun areTestUpdatesSupported(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val info = packageManager.getInstallSourceInfo(packageName)
                info.installingPackageName == null
            } else {
                val installer = packageManager.getInstallerPackageName(packageName)
                installer == null
            }
        } catch (e: Exception) {
            false
        }
    }
}
package com.steamclock.versioncheckkotlin.interfaces

import android.content.pm.PackageManager
import android.os.Build

interface PackageDetails {
    fun wasSideLoaded(): Boolean
}

@Suppress("DEPRECATION")
class DefaultPackageDetails(private val pm: PackageManager, private val name: String): PackageDetails {
    override fun wasSideLoaded(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val info = pm.getInstallSourceInfo(name)
                info.installingPackageName == null
            } else {
                val installer = pm.getInstallerPackageName(name)
                installer == null
            }
        } catch (e: Exception) {
            false
        }
    }
}
package com.steamclock.versioncheckkotlin.utils

import com.steamclock.versioncheckkotlin.interfaces.PackageDetails

object MockPackageDetails {
    object WasSideLoaded: PackageDetails {
        override fun wasSideLoaded() = true
    }

    object WasNotSideLoaded: PackageDetails {
        override fun wasSideLoaded() = false
    }
}
package com.steamclock.versioncheckkotlin

import com.steamclock.versioncheckkotlin.interfaces.*

data class VersionCheckConfig(
    val appVersionName: String,
    val appVersionCode: Int,
    val url: String,
    val urlFetcher: URLFetcher = NetworkURLFetcher,
    val versionDataConverter: VersionDataConverter = DefaultVersionDataConverter,
    val packageDetails: PackageDetails? = null
)
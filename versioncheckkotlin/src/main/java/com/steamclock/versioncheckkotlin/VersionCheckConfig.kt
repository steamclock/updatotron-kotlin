package com.steamclock.versioncheckkotlin

import com.steamclock.versioncheckkotlin.interfaces.*

data class VersionCheckConfig(
    val packageDetails: PackageDetails,
    val url: String,
    val urlFetcher: URLFetcher = NetworkURLFetcher,
    val versionDataConverter: VersionDataConverter = DefaultVersionDataConverter,
)